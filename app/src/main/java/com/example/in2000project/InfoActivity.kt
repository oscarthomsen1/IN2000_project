package com.example.in2000project


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.in2000project.databinding.ActivityInfoBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    private val TAG = "InfoActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        // Setting the currently chosen theme from settings
        val sharedPrefs: SharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this)
        setTheme(if (sharedPrefs.getBoolean("darkmode_switch", false)) R.style.Theme_IN2000ProjectDark else R.style.Theme_IN2000ProjectLight)

        // Basic onCreate constructor
        super.onCreate(savedInstanceState)

        // Enabling the use of binding
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting a onclick listener for the bottom navigation menu.
        val bottomNavigationMenu = findViewById<BottomNavigationView>(
            R.id.bottom_navigation)
        bottomNavigationMenu.selectedItemId = R.id.navInfo
        setNavigationMenuOnItemSelectedListener(bottomNavigationMenu)

        // Setting a onclick listener for the floating action button to settings activity.
        val foatingActionButton = findViewById<FloatingActionButton>(
            R.id.floatingActionButton)
        setFloatingActionButtonOnClickListener(foatingActionButton)
    }

    private fun setNavigationMenuOnItemSelectedListener(bottomNavigationMenu: BottomNavigationView){
        bottomNavigationMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navHome-> {
                    Log.d(TAG, "Home at the navigation menu was pressed.")
                    startActivity( Intent(this, MainActivity::class.java) )
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    this.finish()
                }
                R.id.navMap-> {
                    Log.d(TAG, "Map at the navigation menu was pressed.")
                    startActivity( Intent(this, MapsActivity::class.java) )
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    this.finish()
                }
                R.id.navInfo-> {
                    Log.d(TAG, "Info at the navigation menu was pressed.")
                }
            }
            true
        }
    }

    private fun setFloatingActionButtonOnClickListener(button: FloatingActionButton) {
        button.setOnClickListener {
            Log.d(TAG, "Settings button clicked!")
            startActivity( Intent(this, SettingsActivity::class.java) )
            this.finish()
        }
    }
}