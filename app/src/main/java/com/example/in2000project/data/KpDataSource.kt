package com.example.in2000project.data

//Ansvarlig: Oscar
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson


class KpDatasource {

    private var url : String = "https://services.swpc.noaa.gov/products/noaa-planetary-k-index-forecast.json"
    private val TAG = "KpDataSource"

    suspend fun fetchNordlys() : MutableList<Nordlys>? {
        try {
            val result = (Fuel.get(url).awaitString())
            val listene = Gson().fromJson(result, arrayListOf<ArrayList<String?>?>()::class.java)
            return makeNordlysObject(listene)
        } catch(exception: Exception) {
            Log.d(TAG,"A network request exception was thrown: ${exception.message}")
        }
        return null
    }

    private fun makeNordlysObject(listen: ArrayList<ArrayList<String?>?>?): MutableList<Nordlys>? {
        var counter = 0
        val nordlysListe = mutableListOf<Nordlys>()
        if (listen != null) {
            for (n in listen) {
                nordlysListe.add(counter, Nordlys(n?.get(0), n?.get(1), n?.get(2), n?.get(3)))
                counter++
            }
            Log.d(TAG, "Made nordlysobjekt")
            return nordlysListe
        }
        return null
    }
}

data class Nordlys(var time_tag : String?, var kp : String?, var observed : String?, var noaa_scale : String?)
