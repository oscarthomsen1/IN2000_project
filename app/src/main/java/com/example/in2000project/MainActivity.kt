package com.example.in2000project

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.in2000project.data.*
import com.example.in2000project.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private val OSLO = "Oslo"
    private val URL_SUN = "https://cdn0.iconfinder.com/data/icons/weather-navy-volume-2/64/Summer-512.png"
    private val URL_CLOUDS = "https://cdn2.iconfinder.com/data/icons/weather-flat-14/64/weather04-512.png"
    private val URL_MOON = "https://media1.thehungryjpeg.com/thumbs2/800_3573745_ofa2jycw80r3wk1fpxny39aiy2cnbw00m2m0tymx_moon-icon.jpg"

    private val auroraDatasource = AuroraData()
    val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //region UpperMenu
        scope.launch {
            binding.northernLight.text = "Høy sjanse å se nordlys nå!" // replace with auroraDatasource.AuroraProbabilityNowcast(OSLO)
            binding.currentTime.text = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("H:m"))
                .toString()
            binding.cloudCoverage.text =  "50%" // replace with auroraDatasource.GetClouds()
            binding.kpIndex.text = "4" // replace with auroraDatasource.GetKp()
        }

        // Checks if there are clouds. If yes, shows cloud icon whether it's day or night
        // Checks if it is night. If yes shows moon icon
        // Otherwise shows sun icon
        if (auroraDatasource.CheckClouds()) {
            Glide.with(binding.weatherImage)
                .load(Uri.parse(URL_CLOUDS))
                .into(binding.weatherImage);
        } else if (true) { // replace with auroraDatasource.CheckSunrise() when it is working
            Glide.with(binding.weatherImage)
                .load(Uri.parse(URL_MOON))
                .into(binding.weatherImage);
        } else {
            Glide.with(binding.weatherImage)
                .load(Uri.parse(URL_SUN))
                .into(binding.weatherImage);
        }
        //endregion

        /**
         * A listener and the necessary variables for the bottom navigation menu.
         */
        //region BottomNavigationMenu
        val bottomNavigationMenu = findViewById<BottomNavigationView>(
            com.example.in2000project.R.id.bottom_navigation)
        bottomNavigationMenu.selectedItemId = com.example.in2000project.R.id.home
        bottomNavigationMenu.setOnItemSelectedListener {
            when(it.itemId){
                com.example.in2000project.R.id.home-> {
                    Log.d(TAG, "Home at the navigation menu was pressed.")
                    startActivity( Intent(this, MainActivity::class.java) )
                }
                com.example.in2000project.R.id.map-> {
                    Log.d(TAG, "Map at the navigation menu was pressed.")
                    startActivity( Intent(this, MapsActivity::class.java) )
                }
                com.example.in2000project.R.id.info-> {
                    Log.d(TAG, "Info at the navigation menu was pressed.")
                    startActivity( Intent(this, InfoActivity::class.java) )
                }
            }
            true
        }
        //endregion
    }
}