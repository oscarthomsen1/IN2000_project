package com.example.in2000project.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

//Ansvarlig Tiril
class AuroraData {
    //Tid- og dato-variabler
    //Kanskje hente datoen utenfor klassen hvis man skal bruke et tidspunkt til å hente sannsynlighet
    private val date: LocalDate = LocalDate.now()
    private var dateTime: LocalDateTime = LocalDateTime.now()

    //Posisjon-API
    private val positionSource = PositionStackDatasource()
    //Bredde og lengdegrad for spesifisert posisjon hentes fra API-et
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    //Sunrise
    private val sunriseSource = SunriseDataSource()
    private lateinit var sunriseData: Location
    private lateinit var sunriseTime: LocalDateTime
    private lateinit var sunsetTime: LocalDateTime

    //Clouds
    private val cloudScource = CloudDataSource()
    private lateinit var cloudData: MutableList<Timeseries?>
    private var cloudFraction by Delegates.notNull<Double>()

    //Kp
    private val kpSource = KpDatasource()
    lateinit var kpData: MutableList<Nordlys>
    private var kp by Delegates.notNull<Int>()

    //Sjanse for nordlys
    var nordlys = "Ingen verdi"

    fun createValues(): MutableList<Any> {
        val data = mutableListOf<Any>()
        data.add(nordlys)
        data.add(sunriseTime)
        data.add(sunsetTime)
        data.add(cloudFraction)
        data.add(kp)

        return data
    }

    //Hovedaktivitet
    suspend fun AuroraProbabilityNowcast(placeName: String): MutableList<Any>? {
        //Regner ut sannsynligheten nå for gitt posisjon
        //Sender denne infoen til de forskjellige Check-funksjonene

        GetLocation(placeName)
        GetSunrise()
        GetClouds()
        GetKp()


        //Når det funker å hente fra APIene må man checke og returnere
        if (lateInitCheck()){
            if (CheckSunrise() && CheckClouds() && CheckKp()){
                nordlys = "Høy sjanse for å se nordlys"
            } else if (!CheckKp()){
                nordlys = "For lav geomagnetisk aktivitet til å se nordlys"
            } else if (!CheckClouds()){
                nordlys = "For tykt skydekke til å se nordlys"
            } else if (!CheckSunrise()){
                nordlys = "For lyst til å se nordlys"
            }

            return createValues()
        }
        return null
    }


    //Funksjoner som henter fra API-datakildene
    suspend fun GetLocation(placeName: String){
        //hente bredde og lengdegrad fra stedsnavn
        val position = positionSource.fetchCordinates(placeName)

        lat = position?.latitude?.toDouble()!!
        lon = position.longitude?.toDouble()!!
    }

    suspend fun GetSunrise(){
        //hente og sette info fra API i en variabel sender in lon og lat og tid
        sunriseData = sunriseSource.FetchSunriseNowcast(lat, lon, date.toString())!!

        if (::sunriseData.isInitialized){
            setSunriseAndSunset()
        } else {
            //noe
        }
    }


    suspend fun GetClouds(){
        //hente og sette info fra API i en variabel
        cloudData = cloudScource.fetchSky(lat, lon)!!

        //Set cloudfraction som global variabel:
        if (::cloudData.isInitialized) {
            cloudFraction = cloudData.get(0)?.data?.instant?.details?.cloud_area_fraction!!
        } else {
            //HER MÅ EN VERDI SETTES
        }
    }

    suspend fun GetKp() {
        kpData = kpSource.fetchNordlys()!!

        if (::kpData.isInitialized){
            kpData.removeAt(0) //fjerner '["time_tag","kp","observed","noaa_scale"]'

            setKpValue()
        } else {
            //HER MÅ NOE SKJE
        }
    }


    //Funksjoner som setter de lokale variablene
    fun setSunriseAndSunset() {
        val sunriseTimeStringOffset = sunriseData.time?.get(0)?.sunrise?.time
        val sunriseTimeString = sunriseTimeStringOffset?.dropLast(6) //fjerner offset
        sunriseTime = LocalDateTime.parse(sunriseTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val sunsetTimeStringOffset = sunriseData.time?.get(0)?.sunset?.time
        val sunsetTimeString = sunsetTimeStringOffset?.dropLast(6) //fjerner offset
        sunsetTime = LocalDateTime.parse(sunsetTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    fun setKpValue() {
        //Finne nærmeste måling til tidspunkt
        var minsteTid = 24

        for (nordlysObjekt in kpData) {
            val tempTimestamp = nordlysObjekt.time_tag.toString()
            //konvertere tidsstempelet til ISO-standarden for å kunne formattere
            val timestamp = tempTimestamp.replace(' ', 'T')
            val kpTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            val tidMellom = dateTime.compareTo(kpTime)

            if (tidMellom < minsteTid){
                minsteTid = tidMellom
                kp = nordlysObjekt.kp?.toInt()!!
            }
        }
    }


    //Funkjsjoner som sjekker
    fun CheckSunrise(): Boolean{
        //sjekker mot informasjonen fra SunriseAPIet
        //returnerer en boolean

        when {
            dateTime.isBefore(sunriseTime) -> {
                return true
            }
            dateTime.isAfter(sunsetTime) -> {
                return true
            }
        }
        return false
    }


    fun CheckClouds(): Boolean{
        //sjekker mot locationforecast for både lave,middels og høye skyer
        //returnerer en boolean

        val midClouds: Double? = cloudData.get(0)?.data?.instant?.details?.cloud_area_fraction_medium
        val lowClouds: Double? = cloudData.get(0)?.data?.instant?.details?.cloud_area_fraction_low

        if (cloudFraction > 30.0){
            if ((lowClouds!! + midClouds!!) < 10.0){
                return true //Dette betyr at det kun er høye skyer
            }
            return false
        }
        return true
    }

    fun CheckKp(): Boolean{
        //sjekker kp-verdi fra NOAA og tar hensyn til estimert kp og breddegrad
        //returnerer en boolean

        //https://www.rando-lofoten.net/en/forecasts/aurora-borealis-forcast/514-prevision-with-the-kp-index
        //Kp-verider over terskelverdiene:
        if(lat >= 48 && kp >= 9 ||
            lat >= 50 && kp >= 8 ||
            lat >= 52 && kp >= 7 ||
            lat >= 54 && kp >= 6 ||
            lat >= 56 && kp >= 5 ||
            lat >= 58 && kp >= 4 ||
            lat >= 60 && kp >= 3 ||
            lat >= 63 && kp >= 2 ||
            lat >= 65 && kp >= 1) {
            return true
        }
        return false
    }

    fun lateInitCheck(): Boolean {
        var bool = true

        if (!::sunriseData.isInitialized) bool = false
        if (!::cloudData.isInitialized) bool = false
        if (!::kpData.isInitialized) bool = false

        return bool
    }
}