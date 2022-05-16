package com.example.in2000project

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.in2000project.databinding.ActivityMainBinding
import com.example.in2000project.utils.PermissionUtils
import com.example.in2000project.viewmodels.MainActivityViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalTime
import java.time.format.DateTimeFormatter

//test
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel = MainActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Setting the currently chosen theme from settings
        val sharedPrefs: SharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(this)
        setTheme(if (sharedPrefs.getBoolean("darkmode_switch", false)) R.style.Theme_IN2000ProjectDark else R.style.Theme_IN2000ProjectLight)

        // Basic onCreate constructor
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Gir appen tilgang til posisjon og oppdaterer views med posisjonsdata
        checkLocationPermission()

        //Knapp som oppdaterer viewet med brukerens posisjon
        val posisjonsKnapp = binding.dinPosisjson
        posisjonsKnapp.setOnClickListener {
            checkLocationPermission()
        }

        //region autoComplete-Søk
        //Ansvarlig Tiril
        //Vi benytter oss av Google sitt Places-API for søk etter steder
        Places.initialize(getApplicationContext(), getString(R.string.apiKey))
        Places.createClient(this)

        //Følgende kode er hentet fra: https://developers.google.com/maps/documentation/places/android-sdk/autocomplete
        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            .setTypeFilter(TypeFilter.CITIES)

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val lat = place.latLng?.latitude
                val lon = place.latLng?.longitude

                if (lat != null && lon != null) {
                    bind(lat, lon)
                    binding.sannsynlighetsView.findViewById<TextView>(R.id.location).text =
                        place.name
                }
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })
        //endregion

        //region graph forecast
        //Ansvarlig: Oscar

        val nordlysvarsel = binding.nordlysGraf

        val kpLinje = LineDataSet(datavalues1(), "kp verdier")
        kpLinje.color = Color.CYAN
        kpLinje.setDrawCircles(false)
        kpLinje.valueFormatter = KPValueFormater()
        val skydekeLinje = LineDataSet(datavalues2(), "sky verdier")
        skydekeLinje.color = Color.BLACK
        skydekeLinje.setDrawCircles(false)
        skydekeLinje.valueFormatter = SkyValueFormater()
        val dataset = ArrayList<ILineDataSet>()
        dataset.add(kpLinje)
        dataset.add(skydekeLinje)

        nordlysvarsel.setDrawBorders(true)
        nordlysvarsel.description = null

        val legend = nordlysvarsel.legend
        val legendentries = ArrayList<LegendEntry>()

        val legendentryKp = LegendEntry()
        legendentryKp.formColor = Color.CYAN
        legendentryKp.label = "KP"
        legendentries.add(legendentryKp)
        val legendEntrySky = LegendEntry()
        legendEntrySky.formColor = Color.BLACK
        legendEntrySky.label = "Skydekke"
        legendentries.add(legendEntrySky)

        legend.setCustom(legendentries)

        val yAxisLeft = nordlysvarsel.axisLeft
        yAxisLeft.valueFormatter = KPAxisFormater()
        val yAxisRight = nordlysvarsel.axisRight
        yAxisRight.valueFormatter = SkyAxisFormater()
        val xAxis = nordlysvarsel.xAxis
        xAxis.valueFormatter = XAxisFormater()
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        val data = LineData(dataset)
        nordlysvarsel.data = data
        nordlysvarsel.invalidate()

        //endregion

        // Setting a onclick listener for the bottom navigation menu.
        val bottomNavigationMenu = findViewById<BottomNavigationView>(
            R.id.bottom_navigation)
        bottomNavigationMenu.selectedItemId = R.id.home
        setNavigationMenuOnItemSelectedListener(bottomNavigationMenu)
    }

    //region updater view
    //Ansvarlig Julia
    //Metode som kommuniserer mot API-ene via ViewModelen og binder til viewet
    fun bind(lat: Double, lon: Double){
        viewModel.loadProbability(lat, lon).also {
            viewModel.getData().observe(this@MainActivity) {
                binding.sannsynlighetsView.findViewById<TextView>(R.id.currentTime).text =
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                        .toString()
                setVisibility(binding.sannsynlighetsView.findViewById<TextView>(R.id.currentTime))

                setVisibility(binding.sannsynlighetsView.findViewById<ImageView>(R.id.weatherImage))

                binding.sannsynlighetsView.findViewById<TextView>(R.id.northernLight).text =
                    it[0].toString()
                setVisibility(binding.sannsynlighetsView.findViewById<TextView>(R.id.northernLight))

                binding.sannsynlighetsView.findViewById<TextView>(R.id.kpIndex).text =
                    it[4].toString()
                setVisibility(binding.sannsynlighetsView.findViewById<TextView>(R.id.kpIndex))
                setVisibility(binding.sannsynlighetsView.findViewById<TextView>(R.id.kpIndexLabel))

                binding.sannsynlighetsView.findViewById<TextView>(R.id.cloudCoverage).text =
                    it[3].toString() + "%"
                setVisibility(binding.sannsynlighetsView.findViewById<TextView>(R.id.cloudCoverage))
                setVisibility(binding.sannsynlighetsView.findViewById<TextView>(R.id.cloudCoverageLabel))


                // Checks if there is a high chance of seeing aurora. If yes, show aurora icon
                // Checks if there are clouds. If yes, shows cloud icon whether it's day or night
                // Checks if it is night. If yes shows moon icon
                // Otherwise shows sun icon
                if (it[0] == "Høy sjanse for å se nordlys") {
                    Glide.with(binding.sannsynlighetsView.findViewById<ImageView>(R.id.weatherImage))
                        .load(R.drawable.ic_aurora)
                        .into(binding.sannsynlighetsView.findViewById(R.id.weatherImage))
                } else if (!viewModel.checkClouds()) {
                    Glide.with(binding.sannsynlighetsView.findViewById<ImageView>(R.id.weatherImage))
                        .load(R.drawable.ic_cloudy)
                        .into(binding.sannsynlighetsView.findViewById(R.id.weatherImage))
                } else if (viewModel.checkSun()) {
                    Glide.with(binding.sannsynlighetsView.findViewById<ImageView>(R.id.weatherImage))
                        .load(R.drawable.ic_moon)
                        .into(binding.sannsynlighetsView.findViewById(R.id.weatherImage))
                } else {
                    Glide.with(binding.sannsynlighetsView.findViewById<ImageView>(R.id.weatherImage))
                        .load(R.drawable.ic_sunny)
                        .into(binding.sannsynlighetsView.findViewById(R.id.weatherImage))
                }
            }
        }
    }

    fun setVisibility(view: View){
        if (view.visibility == View.INVISIBLE){
            view.visibility = View.VISIBLE
        }
    }
    //endregion


    private fun setNavigationMenuOnItemSelectedListener(bottomNavigationMenu: BottomNavigationView){
        bottomNavigationMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home-> {
                    Log.d(TAG, "Home at the navigation menu was pressed.")
                }
                R.id.map-> {
                    Log.d(TAG, "Map at the navigation menu was pressed.")
                    startActivity( Intent(this, MapsActivity::class.java) )
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                R.id.info-> {
                    Log.d(TAG, "Info at the navigation menu was pressed.")
                    startActivity( Intent(this, InfoActivity::class.java) )
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
            }
            true
        }
    }


    //region posisjonstilgang
    //Ansvarlig Tiril
    private fun checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null){
                    val lat = it.latitude
                    val lon = it.longitude

                    bind(lat, lon)
                    binding.sannsynlighetsView.findViewById<TextView>(R.id.location).text =
                        "Din posisjon"
                } else {
                    //TODO
                    //on error bind
                    //Type 'ingen data å vise'
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
    //endregion

    //region functions and classes for graph
    private fun datavalues1() : ArrayList<Entry> {
        val punkter = ArrayList<Entry>()
        punkter.add(Entry(0F, 2F))
        punkter.add(Entry(1F, 2F))
        punkter.add(Entry(3F, 1F))
        punkter.add(Entry(4F, 1F))
        punkter.add(Entry(5F, 3F))
        punkter.add(Entry(6F, 4F))
        punkter.add(Entry(7F, 0F))
        punkter.add(Entry(8F, 1F))
        punkter.add(Entry(9F, 1F))
        punkter.add(Entry(10F, 2F))
        punkter.add(Entry(11F, 1F))
        punkter.add(Entry(12F, 0F))
        punkter.add(Entry(13F, 0F))
        punkter.add(Entry(14F, 1F))
        punkter.add(Entry(15F, 2F))
        punkter.add(Entry(16F, 3F))
        return punkter
    }
    private fun datavalues2() : ArrayList<Entry> {
        val punkter = ArrayList<Entry>()
        punkter.add(Entry(0F, 4F))
        punkter.add(Entry(1F, 7F))
        punkter.add(Entry(3F, 8F))
        punkter.add(Entry(4F, 1F))
        punkter.add(Entry(5F, 2F))
        punkter.add(Entry(6F, 0F))
        punkter.add(Entry(7F, 1F))
        punkter.add(Entry(8F, 3F))
        punkter.add(Entry(9F, 3F))
        punkter.add(Entry(10F, 5F))
        punkter.add(Entry(11F, 7F))
        punkter.add(Entry(12F, 7F))
        punkter.add(Entry(13F, 8F))
        punkter.add(Entry(14F, 9F))
        punkter.add(Entry(15F, 10F))
        punkter.add(Entry(16F, 7F))
        return punkter
    }
    private class KPValueFormater : IValueFormatter {
        override fun getFormattedValue(
            value: Float,
            entry: Entry?,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler?
        ): String {
            return value.toInt().toString()
        }

    }
    private class SkyValueFormater : IValueFormatter {
        override fun getFormattedValue(
            value: Float,
            entry: Entry?,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler?
        ): String {
            return ((value*10).toInt().toString() + "%")
        }

    }
    private class KPAxisFormater : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            axis?.axisMinimum = 0F
            return value.toInt().toString()
        }

    }
    private class SkyAxisFormater : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            axis?.axisMinimum = 0F
            return (value*10).toInt().toString()
        }

    }
    private class XAxisFormater : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            axis?.axisMinimum = 0F
            axis?.setLabelCount(5, true)
            when (value) {
                0F -> return "00:00"
                4F -> return "12:00"
                8F -> return "00:00"
                12F -> return "12:00"
                16F -> return "00:00"

            }
            return value.toString()
        }

    }
    //end region

}
