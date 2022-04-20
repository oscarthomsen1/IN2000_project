package com.example.in2000project.data

import android.app.appsearch.SetSchemaRequest
import android.util.Log
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

//Ansvarlig Tiril
class AuroraData {
    //Tid- og dato-variabler
    //Kanskje hente datoen utenfor klassen hvis man skal bruke et tidspunkt til å hente sannsynlighet
    private val time: LocalTime = LocalTime.now()
    private val date: LocalDate = LocalDate.now()
    private var dateTime: LocalDateTime = LocalDateTime.now()

    //Posisjon-API
    private val positionSource = PositionStackDatasource()
    //Bredde og lengdegrad for spesifisert posisjon hentes fra API-et
    private var lat: Double = 57.0 //hardkodet for å kunne teste
    private var lon: Double = 48.0 // -"-

    //Sunrise
    private val sunriseSource = SunriseDataSource()
    private var sunriseData: Location? = null
    //private val sunriseTime
    //private val sunsetTime

    //Clouds
    private val cloudScource = CloudDataSource()
    private lateinit var cloudData: List<Timeseries?>
    private var cloudFraction: Double? = null

    //Kp
    private val kpSource = KpDatasource()
    lateinit var kpData: MutableList<Nordlys>
    var kp: Int? = null

    suspend fun AuroraProbabilityNowcast(placeName: String){
        //Regner ut sannsynligheten nå
        //Bruker GetLocation til å hente den valgte plasseringen i lat og lon
        //Sender denne infoen til de forskjellige Check-funksjonene

        //GetLocation(placeName)
        GetSunrise()
        GetClouds()
        GetKp()

        //Når det funker å hente fra APIene må man checke og returnere
    }

    suspend fun GetLocation(placeName: String){
        //hente bredde og lengdegrad fra stedsnavn
        val position = positionSource.fetchCordinates(placeName)

        lat = position?.latitude?.toDouble()!!
        lon = position.longitude?.toDouble()!!
    }

    suspend fun GetSunrise(){
        //hente og sette info fra API i en variabel sender in lon og lat og tid
        sunriseData = sunriseSource.FetchSunriseNowcast(lat, lon, date.toString())!!
        //SetSunriseAndSunset?
    }

    suspend fun GetClouds(){
        //hente og sette info fra API i en variabel
        cloudData = cloudScource.fetchSky(lat, lon)!!

        //Set cloudfraction som global variabel:
        cloudFraction = cloudData.get(0)?.data?.instant?.details?.cloud_area_fraction
    }

    suspend fun GetKp() {
        kpData = kpSource.fetchNordlys()!!
        setKpValue()
    }

    fun setKpValue() {
        //Finne nærmeste måling til tidspunkt
        var minsteTid = 24
        var naermesteTid: LocalDateTime? = null

        for (listing in kpData) {
            val timestamp = listing.time_tag
            val kpTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            val tidMellom = dateTime.compareTo(kpTime)

            if (tidMellom < minsteTid){
                minsteTid = tidMellom
                naermesteTid = kpTime
                kp = listing.kp!!
            }
        }
        Log.d("KP = ", kp.toString())
    }


    fun CheckSunrise(): Boolean{
        //sjekker mot informasjonen fra SunriseAPIet
        //returnerer en boolean

        val sunriseTimeString = sunriseData?.time?.get(0)?.sunrise?.time
        Log.d("SUNRISETIME: ", sunriseTimeString.toString())
        val sunriseTime = LocalTime.parse(sunriseTimeString)
        Log.d("SUNRISETIME: ", sunriseTime.toString())
        val sunsetTime = LocalTime.parse(sunriseData?.time?.get(0)?.sunset?.time)
        Log.d("SUNSETTIME: ", sunsetTime.toString())

        when {
            time.isBefore(sunriseTime) -> {
                return true
            }
            time.isAfter(sunsetTime) -> {
                return true
            }
        }
        return false
    }


    fun CheckClouds(): Boolean{
        //sjekker mot locationforecast for både lave,middels og høye skyer
        //returnerer en boolean

        //val highclouds: Double? = cloudData.get(0)?.data?.instant?.details?.cloud_area_fraction_high
        val midClouds: Double? = cloudData.get(0)?.data?.instant?.details?.cloud_area_fraction_medium
        val lowClouds: Double? = cloudData.get(0)?.data?.instant?.details?.cloud_area_fraction_low

        if (cloudFraction != null) {
            if (cloudFraction!! > 30.0){
                if ((lowClouds!! + midClouds!!) < 10.0){
                    return true //Dette betyr at det kun er høye skyer
                }
                return false
            }
        }
        return false
    }

    fun CheckKp(): Boolean{
        //sjekker kp-verdi fra NOAA og tar hensyn til estimert kp og breddegrad
        //returnerer en boolean

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
}

//problemer: Nullpointerexeption nåt api-kallene ikke går
//Lateinit-problemer