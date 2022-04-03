package com.example.in2000project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.in2000project.databinding.ActivityInfoBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    private val TAG = "InfoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /**
         * A listener and the necessary variables for the bottom navigation menu.
         */
        //region BottomNavigationMenu
        val bottomNavigationMenu = findViewById<BottomNavigationView>(
            R.id.bottom_navigation)
        bottomNavigationMenu.selectedItemId = R.id.info
        bottomNavigationMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home-> {
                    Log.d(TAG, "Home at the navigation menu was pressed.")
                    startActivity( Intent(this, MainActivity::class.java) )
                }
                R.id.map-> {
                    Log.d(TAG, "Map at the navigation menu was pressed.")
                    startActivity( Intent(this, MapsActivity::class.java) )
                }
                R.id.info-> {
                    Log.d(TAG, "Info at the navigation menu was pressed.")
                    startActivity( Intent(this, InfoActivity::class.java) )
                }
            }
            true
        }
        //endregion
    }
}