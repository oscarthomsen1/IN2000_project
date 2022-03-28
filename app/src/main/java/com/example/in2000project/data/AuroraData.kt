package com.example.in2000project.data

//Ansvarlig: Tiril
class AuroraData {

    fun AuroraProbabilityNow(){
        //regner ut sannsynligheten nå
        //bruker GetLocation til å hente den valgte plasseringen i lat og long
        //sender denne infoen til de forskjellige Check-funksjonene
        //bruker resultatene til å avgjøre om det er sannsynlighet for nordlysobservasjoner
    }

    fun AuroraProbabilityForecast(){
        //henter ut data for de neste 3 dagene
        //dette burde kanskje flyttes til en egen klasse for å kunne bruke dataen til å lage en grafisk fremstilling
    }

    fun GetSunrise(){
        //hente og sette info fra API i en variabel
    }

    fun GetClouds(){
        //hente og sette info fra API i en variabel
    }

    fun GetKp(){
        //hente og sette info fra API i en variabel
    }

    fun GetLocation(){
        //hente bredde og lengdegrad fra stedsnavn
    }

    fun CheckSunrise(){
        //tar in bredde og lengdegrad og sjekker mot informasjonen fra SunriseAPIet
        //returnerer en boolean
    }

    fun CheckClouds(){
        //sjekker mot locationforecast for både lave,middels og høye skyer
        //returnerer en boolean
    }

    fun CheckKp(){
        //sjekker mot NOAA tar hensyn til estimert kp og breddegrad
        //returnerer en boolean
    }
}