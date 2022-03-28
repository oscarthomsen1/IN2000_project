package com.example.in2000project.data

//Ansvarlig: Oscar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class PositionStackDatasource {
    suspend fun fetchCordinates(query : String) : Position? {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://api.positionstack.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service : PositionStackApi = retrofit.create(PositionStackApi::class.java)
            val respons = service.fetchCordinates("e7b8d998277664d86be0823d1d776c87", query, "NO,DK,FI,FO,IS,SE")
            return respons.data[0]
        } catch (exception : Exception) {
            println("A network request exception was thrown: ${exception.message}")
        }
        return null
    }

    data class CordinatRepons(var data : List<Position?>)

    data class Position(var latitude : String?, var longitude : String?, var name : String?)
}

interface PositionStackApi {
    @GET("http://api.positionstack.com/v1/forward")
    suspend fun fetchCordinates(
        @Query("access_key") access_key: String,
        @Query("query") query : String,
        @Query("country") country : String
    ) : PositionStackDatasource.CordinatRepons
}