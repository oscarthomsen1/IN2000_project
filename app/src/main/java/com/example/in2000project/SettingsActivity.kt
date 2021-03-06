package com.example.in2000project

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager


class SettingsActivity : AppCompatActivity() {

    private val TAG = "SettingsActivity"
    private lateinit var actionBar: ActionBar


    override fun onCreate(savedInstanceState: Bundle?) {
        // Setting the currently chosen theme from settings
        val sharedPrefs: SharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this)
        setTheme(if (sharedPrefs.getBoolean("darkmode_switch", false)) R.style.Theme_IN2000ProjectDark else R.style.Theme_IN2000ProjectLight)

        // onCreate constructor
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        // Setting up actionBar
        setSupportActionBar(findViewById(R.id.my_toolbar))
        actionBar = supportActionBar!!
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true)

        // An onClickListener that restarts the activity when the darkmode
        // settings change.
        sharedPrefs.registerOnSharedPreferenceChangeListener { sharedPreferences, _ ->
            val darkmodeSwitch = sharedPreferences.getBoolean("darkmode_switch", false)
            Log.d(TAG, "Dark mode value changed to:$darkmodeSwitch")
            startActivity( Intent(this, SettingsActivity::class.java) )
        }
    }

    /**
     * Function that handles the case where you change some settings and
     * press the standard android back button. This makes sure that the instance
     * we are trying to return to gets refreshed with the newly changed settings.
     **/
    override fun onBackPressed() {
        val intent = Intent(this, InfoActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    // this event will enable the back function to the button on press
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "The back button in action bar from settings activity was pressed.")
        startActivity( Intent(this, InfoActivity::class.java) )
        this.finish()

        return super.onOptionsItemSelected(item)
    }


    // Inner class
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}