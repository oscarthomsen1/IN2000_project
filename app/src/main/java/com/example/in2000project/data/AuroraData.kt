package com.example.in2000project.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

//Ansvarlig: Tiril
class AuroraData {
    //Time and date variables
    val time = LocalTime.now()
    val date = LocalDate.now()
    val dateTime = LocalDateTime.now()

    //latitude and longitude for the chosen position
    var lat: Double = 0.0
    var lon: Double = 0.0

    //Sunrise
    val sunriseSource = SunriseDataSource()
    lateinit var sunrise: Location

    //Clouds

    //Kp
    val kpSource = KpDataSource()
    var kp: Int = 0


    suspend fun AuroraProbabilityNowcast(placeName: String){
        //regner ut sannsynligheten nå
        //bruker GetLocation til å hente den valgte plasseringen i lat og long
        //sender denne infoen til de forskjellige Check-funksjonene
        //bruker resultatene til å avgjøre om det er sannsynlighet for nordlysobservasjoner

        GetLocation(placeName)
        GetSunrise(lat, lon, date.toString())
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

    fun GetLocation(placeName: String){
        //hente bredde og lengdegrad fra stedsnavn

        lat = latfraplacename
        lon = lonfraplacename
    }

    suspend fun GetSunrise(lat: Double, lon: Double, date: String){
        //hente og sette info fra API i en variabel sender in lon og lat og tid
        sunrise = sunriseSource.fetchSunriseNowcast(lat, lon, date)!!
    }

    fun GetClouds(){
        //hente og sette info fra API i en variabel
    }

    fun GetKp(){
        //hente og sette info fra API i en variabel
        kp = kpSource.fetchKpNowcast()!!
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

    fun CheckClouds(){
        //sjekker mot locationforecast for både lave,middels og høye skyer
        //returnerer en boolean
    }

    fun CheckKp(): Boolean{
        //sjekker kp-verdi fra NOAA og tar hensyn til estimert kp og breddegrad
        //returnerer en boolean

        //fra https://github.com/alexcviek/project-1/blob/master/src/js/app.js
        //Kp-verider over terskelverdiene:
        if(lat < 60 && kp >= 5 ||
            lat >= 62 && kp >= 4 ||
            lat >= 65 && kp >= 3 ||
            lat >= 68 && kp >= 2 ||
            lat >= 70 && kp >= 1) {
            return true
        }
        return false
    }
}