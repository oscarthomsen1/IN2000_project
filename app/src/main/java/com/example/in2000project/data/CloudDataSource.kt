package com.example.in2000project.data

//Ansvarlig: Oscar
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class CloudDataSource {
    private val TAG = "CloudDataSource"

    suspend fun fetchSky(lat : Double, lon : Double) : MutableList<Timeseries?>? {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://in2000-apiproxy.ifi.uio.no")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: MetApi = retrofit.create(MetApi::class.java)
            val respons = service.fetchSkyData(lat.toString(), lon.toString())
            return respons.properties?.timeseries
        } catch (exception : Exception) {
            Log.d(TAG,"A network request exception was thrown: ${exception.message}")
        }
        return null
    }
}

interface MetApi {
    @GET("https://in2000-apiproxy.ifi.uio.no/weatherapi/locationforecast/2.0/complete?")
    suspend fun fetchSkyData(
        @Query("lat") lat : String,
        @Query("lon") lon : String
    ) : SkyRespons
}

data class SkyRespons(var properties : Properties?)

data class Properties(var timeseries : MutableList<Timeseries?>?)

data class Timeseries(var data : Data?)

data class Data(var instant : Instant?)

data class Instant(var details : Details)

data class Details(var cloud_area_fraction : Double?, val cloud_area_fraction_high : Double?, val cloud_area_fraction_low : Double?, val cloud_area_fraction_medium : Double?)
