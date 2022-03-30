package com.example.in2000project.data

import java.time.LocalDate
import java.time.LocalTime

//Ansvarlig: Tiril
class AuroraData {
    //Time and date variables
    private val time: LocalTime = LocalTime.now()
    private val date = LocalDate.now()

    //Posisjon-API
    private val positionSource = PositionStackDatasource()
    //Bredde og lengdegrad for spesifisert posisjon hentes fra API-et
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    //Sunrise
    private val sunriseSource = SunriseDataSource()
    private lateinit var sunrise: Location

    //Clouds
    private val cloudScource = CloudDataSource()
    private lateinit var clouds: MutableList<Timeseries?>

    //Kp
    private val kpSource = KpDatasource()
    private var kp: Int = 0


    suspend fun AuroraProbabilityNowcast(placeName: String){
        //Regner ut sannsynligheten nå
        //Bruker GetLocation til å hente den valgte plasseringen i lat og lon
        //Sender denne infoen til de forskjellige Check-funksjonene

        GetLocation(placeName)
        GetSunrise()
        GetClouds()
        GetKp()

        if (CheckSunrise() && CheckClouds() && CheckKp()){
            // JA det er mulig å se nordlys
        }
    }

    fun AuroraProbabilityForecast(){
        //henter ut data for de neste 3 dagene
        //dette burde kanskje flyttes til en egen klasse for å kunne bruke dataen til å lage en grafisk fremstilling
    }

    suspend fun GetLocation(placeName: String){
        //hente bredde og lengdegrad fra stedsnavn
        val position = positionSource.fetchCordinates(placeName)

        lat = position?.latitude?.toDouble()!!
        lon = position.longitude?.toDouble()!!
    }

    suspend fun GetSunrise(){
        //hente og sette info fra API i en variabel sender in lon og lat og tid
        sunrise = sunriseSource.FetchSunriseNowcast(lat, lon, date.toString())!!
    }

    suspend fun GetClouds(){
        //hente og sette info fra API i en variabel
        clouds = cloudScource.fetchSky(lat, lon)!!
    }

    suspend fun GetKp(){
        //hente og sette info fra API i en variabel
        val kpList = kpSource.fetchNordlys()!!
        //må finne en måte å hente ut den "nærmeste" kp-varslingen fra listen og så hente ut element 1 som er kp: Int
        //kp = kpList.get(1)
    }

    fun CheckSunrise(): Boolean{
        //sjekker mot informasjonen fra SunriseAPIet
        //returnerer en boolean

        val sunriseTime = LocalTime.parse(sunrise.time?.get(0)?.sunrise?.time)
        val sunsetTime = LocalTime.parse(sunrise.time?.get(0)?.sunset?.time)

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

        return false
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
}