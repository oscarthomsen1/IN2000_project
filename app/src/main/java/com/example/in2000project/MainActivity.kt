package com.example.in2000project

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.in2000project.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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