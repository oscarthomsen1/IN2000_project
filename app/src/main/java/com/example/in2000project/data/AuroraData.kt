package com.example.in2000project.data

import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//Ansvarlig Tiril
class AuroraData {
    //Tid- og dato-variabler
    //Kanskje hente datoen utenfor klassen hvis man skal bruke et tidspunkt til å hente sannsynlighet
    private val date: LocalDate = LocalDate.now()
    private var dateTime: LocalDateTime = LocalDateTime.now()

    //Bredde og lengdegrad for spesifisert posisjon hentes fra API-et
    private var lat: Double = 0.0
    private var lon: Double = 0.0


    //Sunrise
    private val sunriseSource = SunriseDataSource()
    private lateinit var sunriseData: Location
    private var sunriseTime: LocalDateTime? = null
    private var sunsetTime: LocalDateTime? = null

    //Clouds
    private val cloudScource = CloudDataSource()
    private lateinit var cloudData: MutableList<Timeseries?>
    private var cloudFraction: Double? = null

    //Kp
    private val kpSource = KpDatasource()
    private lateinit var kpData: MutableList<Nordlys>
    private var kp: Int? = null

    //Sjanse for nordlys
    private var nordlys = "Ingen verdi"

    //Liste med variabler som kan brukes i viewet
    private fun createValues(): MutableList<Any?> {
        val data = mutableListOf<Any?>()
        data.add(nordlys)
        data.add(sunriseTime)
        data.add(sunsetTime)
        data.add(cloudFraction)
        data.add(kp)

        return data
    }

    //Hovedaktivitet
    //Angir sannsynlighet for nordlys nå ved gitt posisjon
    //Henter fra API-ene
    //Sender denne infoen til de forskjellige Check-funksjonene
    suspend fun auroraProbabilityNowcast(latIn: Double, lonIn: Double): MutableList<Any?> {
        //Setter bredde og legndegrad for å kunne sende til API-ene
        lat = latIn
        lon = lonIn

        //Henter data fra API-ene
        getSunrise()
        getClouds()
        getKp()

        //Sjekker verdier som kan utløse excepions
        if (lateInitCheck() && nullCheck()){
            //Bruker check-funksjonene om det er mulig å se nordlys
            if (checkSunrise() && checkClouds() && checkKp()){
                nordlys = "Høy sjanse for å se nordlys"
            } else if (!checkKp()){
                nordlys = "For lav solvind-aktivitet til å se nordlys"
            } else if (!checkClouds()){
                nordlys = "For tykt skydekke til å se nordlys"
            } else if (!checkSunrise()){
                nordlys = "For lyst til å se nordlys"
            }
        }
        return createValues()
    }
    fun GetGraphData(): MutableList<MutableList<Float?>> {
        val grafData = mutableListOf<MutableList<Float?>>()
        grafData.add(0, setKpGraphData())
        grafData.add(1, setCloudGraphData(grafData[0].size))
        Log.d("grafData", grafData.toString())
        return grafData
    }

    private fun setKpGraphData(): MutableList<Float?> {
        val kpList = mutableListOf<Float?>()
        var minsteTid = 24
        var first = 0

        for (i in 0 until kpData.size) {
            val tempTimestamp = kpData[i].time_tag.toString()
            //konvertere tidsstempelet til ISO-standarden for å kunne formattere
            val timestamp = tempTimestamp.replace(' ', 'T')
            val kpTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            val tidMellom = dateTime.compareTo(kpTime)

            if (tidMellom >= -1) {
                if (tidMellom < minsteTid) {
                    minsteTid = tidMellom
                    kp = kpData[i].kp?.toInt()!!
                    first = i-1
                }
            }
        }
        for (j in first until kpData.size) {
            kpList.add(kpData[j].kp?.toFloat())
        }
        return kpList
    }
    private fun setCloudGraphData(size : Int): MutableList<Float?> {
        val cloudList = mutableListOf<Float?>()
        val total = (size*3)

        for(i in 1..total) {
            cloudList.add(cloudData[i]?.data?.instant?.details?.cloud_area_fraction?.toFloat())
        }
        return cloudList
    }

    private suspend fun getSunrise(){
        //hente og sette info fra API i en variabel sender in lon og lat og tid
        sunriseData = sunriseSource.FetchSunriseNowcast(lat, lon, date.toString())!!

        if (::sunriseData.isInitialized){
            setSunriseAndSunset()
        }
    }

    private suspend fun getClouds(){
        //hente og sette info fra API i en variabel
        cloudData = cloudScource.fetchSky(lat, lon)!!

        //Set cloudfraction som global variabel:
        if (::cloudData.isInitialized) {
            cloudFraction = cloudData[2]?.data?.instant?.details?.cloud_area_fraction!!
        }
    }

    private suspend fun getKp() {
        kpData = kpSource.fetchNordlys()!!

        if (::kpData.isInitialized){
            kpData.removeAt(0) //fjerner '["time_tag","kp","observed","noaa_scale"]'

            setKpValue()
        }
    }


    //Funksjoner som setter de lokale variablene
    private fun setSunriseAndSunset() {
        val sunriseTimeStringOffset = sunriseData.time?.get(0)?.sunrise?.time
        val sunriseTimeString = sunriseTimeStringOffset?.dropLast(6) //fjerner offset
        sunriseTime = LocalDateTime.parse(sunriseTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val sunsetTimeStringOffset = sunriseData.time?.get(0)?.sunset?.time
        val sunsetTimeString = sunsetTimeStringOffset?.dropLast(6) //fjerner offset
        sunsetTime = LocalDateTime.parse(sunsetTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    private fun setKpValue() {
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
    fun checkSunrise(): Boolean{
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


    fun checkClouds(): Boolean{
        //sjekker mot locationforecast for både lave,middels og høye skyer
        //returnerer en boolean

        val midClouds: Double? = cloudData[0]?.data?.instant?.details?.cloud_area_fraction_medium
        val lowClouds: Double? = cloudData[0]?.data?.instant?.details?.cloud_area_fraction_low

        if (cloudFraction!! > 30.0){
            if ((lowClouds!! + midClouds!!) < 10.0){
                return true //Dette betyr at det kun er høye skyer
            }
            return false
        }
        return true
    }

    private fun checkKp(): Boolean{
        //sjekker kp-verdi fra NOAA og tar hensyn til estimert kp og breddegrad
        //returnerer en boolean

        //Tall hentet fra siden
        //https://www.rando-lofoten.net/en/forecasts/aurora-borealis-forcast/514-prevision-with-the-kp-index

        //Kp-verider over terskelverdiene:
        if(lat >= 48 && kp!! >= 9 ||
            lat >= 50 && kp!! >= 8 ||
            lat >= 52 && kp!! >= 7 ||
            lat >= 54 && kp!! >= 6 ||
            lat >= 56 && kp!! >= 5 ||
            lat >= 58 && kp!! >= 4 ||
            lat >= 60 && kp!! >= 3 ||
            lat >= 63 && kp!! >= 2 ||
            lat >= 65 && kp!! >= 1) {
            return true
        }
        return false
    }


    //Sjekk-funksjoner for nullable og lateinit verdier
    private fun lateInitCheck(): Boolean {
        var bool = true

        if (!::sunriseData.isInitialized) bool = false
        if (!::cloudData.isInitialized) bool = false
        if (!::kpData.isInitialized) bool = false

        return bool
    }

    private fun nullCheck(): Boolean {
        var bool = true

        if (sunsetTime == null) bool = false
        if (sunriseTime == null) bool = false
        if (cloudFraction == null) bool = false
        if (kp == null) bool = false

        return bool
    }
}