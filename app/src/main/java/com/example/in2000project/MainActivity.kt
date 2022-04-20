package com.example.in2000project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.in2000project.data.AuroraData
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

    private val viewModel = MainActivityViewModel()
    val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.loadProbability("Oslo")

        //region UpperMenu
        scope.launch {
            binding.northernLight.text = "Høy sjanse å se nordlys nå!" // replace with auroraDatasource.AuroraProbabilityNowcast(OSLO)
            binding.currentTime.text = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm"))
                .toString()
            binding.cloudCoverage.text =  "50%" // replace with auroraDatasource.GetClouds()
            binding.kpIndex.text = "4" // replace with auroraDatasource.GetKp()
        }

        /*
        // Checks if there are clouds. If yes, shows cloud icon whether it's day or night
        // Checks if it is night. If yes shows moon icon
        // Otherwise shows sun icon
        if (auroraDatasource.CheckClouds()) {
            Glide.with(binding.weatherImage)
                .load(R.drawable.ic_baseline_cloud_24)
                .into(binding.weatherImage);
            } else if (true) { // replace with auroraDatasource.CheckSunrise() when it is working
                Glide.with(binding.weatherImage)
                    .load(R.drawable.ic_baseline_moon_24)
                    .into(binding.weatherImage);
            } else {
                Glide.with(binding.weatherImage)
                    .load(R.drawable.ic_baseline_wb_sunny_24)
                    .into(binding.weatherImage);
            }

         */
        //endregion

        // Setting a onclick listener for the bottom navigation menu.
        val bottomNavigationMenu = findViewById<BottomNavigationView>(
            com.example.in2000project.R.id.bottom_navigation)
        bottomNavigationMenu.selectedItemId = com.example.in2000project.R.id.home
        setNavigationMenuOnItemSelectedListener(bottomNavigationMenu)

    }

    private fun setNavigationMenuOnItemSelectedListener(bottomNavigationMenu: BottomNavigationView){
        bottomNavigationMenu.setOnItemSelectedListener {
            when(it.itemId){
                com.example.in2000project.R.id.home-> {
                    Log.d(TAG, "Home at the navigation menu was pressed.")
                }
                com.example.in2000project.R.id.map-> {
                    Log.d(TAG, "Map at the navigation menu was pressed.")
                    startActivity( Intent(this, MapsActivity::class.java) )
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                com.example.in2000project.R.id.info-> {
                    Log.d(TAG, "Info at the navigation menu was pressed.")
                    startActivity( Intent(this, InfoActivity::class.java) )
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
            }
            true
        }
    }
}