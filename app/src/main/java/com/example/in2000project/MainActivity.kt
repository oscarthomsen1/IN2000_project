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
import com.example.in2000project.viewmodels.MainActivityViewModel
import com.github.mikephil.charting.charts.LineChart
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
    private val localTime = LocalTime.now()

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



        //region onClick-Menubar
        //Ansvarlig Tobias
        // Setting a onclick listener for the bottom navigation menu.
        val bottomNavigationMenu = findViewById<BottomNavigationView>(
            R.id.bottom_navigation)
        bottomNavigationMenu.selectedItemId = R.id.navHome
        setNavigationMenuOnItemSelectedListener(bottomNavigationMenu)
        //endregion
    }

    //region oppdater view
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

                //region graph forecast
                //Ansvarlig: Oscar
                viewModel.loadGraphData()
                viewModel.getGraphData().observe(this) {
                    val kpLinje = LineDataSet(datavalues1(it!![0]), "kpVerdier")
                    val skydekeLinje = LineDataSet(datavalues2(it[1]) , "sky verdier")
                    costumizeGraph(binding.nordlysGraf)

                    kpLinje.color = getColor(R.color.md_theme_dark_onTertiary)
                    kpLinje.setDrawCircles(false)
                    kpLinje.valueFormatter = KPValueFormater()
                    skydekeLinje.color = getColor(R.color.md_theme_dark_surface)
                    skydekeLinje.setDrawCircles(false)
                    skydekeLinje.valueFormatter = SkyValueFormater()
                    val dataset = ArrayList<ILineDataSet>()
                    dataset.add(kpLinje)
                    dataset.add(skydekeLinje)

                    val data = LineData(dataset)
                    binding.nordlysGraf.data = data
                    binding.nordlysGraf.invalidate()
                    //end region
                }
            }
        }
    }

    fun setVisibility(view: View){
        if (view.visibility == View.INVISIBLE){
            view.visibility = View.VISIBLE
        }
    }

    fun onErrorView() {
        binding.sannsynlighetsView.findViewById<TextView>(R.id.location).text = "Ingen data"
    }
    //endregion

    //region onItemSelectedListner
    //Ansvarlig Tobias
    private fun setNavigationMenuOnItemSelectedListener(bottomNavigationMenu: BottomNavigationView){
        bottomNavigationMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navHome-> {
                    Log.d(TAG, "Home at the navigation menu was pressed.")
                }
                R.id.navMap-> {
                    Log.d(TAG, "Map at the navigation menu was pressed.")
                    startActivity( Intent(this, MapsActivity::class.java) )
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    this.finish()
                }
                R.id.navInfo-> {
                    Log.d(TAG, "Info at the navigation menu was pressed.")
                    startActivity( Intent(this, InfoActivity::class.java) )
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    this.finish()
                }
            }
            true
        }
    }
    //endregion

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
                    Toast.makeText(this, "Kunne ikke hente posisjon", Toast.LENGTH_SHORT).show()
                    onErrorView()
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
    private fun costumizeGraph(graph : LineChart) {
        graph.setDrawBorders(true)
        graph.description = null

        val legend = graph.legend
        val legendentries = ArrayList<LegendEntry>()
        val legendentryKp = LegendEntry()
        legendentryKp.formColor = getColor(R.color.md_theme_dark_onTertiary)
        legendentryKp.label = "KP"
        legendentries.add(legendentryKp)
        val legendEntrySky = LegendEntry()
        legendEntrySky.formColor = getColor(R.color.md_theme_dark_surface)
        legendEntrySky.label = "Skydekke"
        legendentries.add(legendEntrySky)
        legend.setCustom(legendentries)

        val yAxisLeft = graph.axisLeft
        yAxisLeft.valueFormatter = KPAxisFormater()
        val yAxisRight = graph.axisRight
        yAxisRight.valueFormatter = SkyAxisFormater()
        val xAxis = graph.xAxis
        xAxis.valueFormatter = XAxisFormater()
        xAxis.position = XAxis.XAxisPosition.BOTTOM
    }
    private fun datavalues1(data : MutableList<Float?>) : ArrayList<Entry> {
        val punkter = ArrayList<Entry>()
        for (i in 0 until data.size) {
            val x = (i*3).toFloat()
            punkter.add( Entry(x, data[i] as Float))
        }
        return punkter
    }
    private fun datavalues2(data : MutableList<Float?>) : ArrayList<Entry> {
        var i = 0
        val punkter = ArrayList<Entry>()
        when(localTime.hour-2) {
            0, 3, 6, 9, 12, 15, 18, 21 -> i = 2
            1, 4, 7, 10, 13, 16, 19, 22 -> i = if(localTime.minute > 30) { 4 } else { 1 }
            2, 5, 8, 11, 14, 17, 20, 23 -> i = 3
        }
        for (x in i until data.size) {
            val y = data[x]?.div(10)
            punkter.add(Entry(x-i.toFloat(), y as Float))
        }
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
            if (entry?.x?.toInt()?.rem(3) == 0) {
                return ((value * 10).toInt().toString() + "%")
            }
            return ""
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
            return (value*10).toInt().toString() + "%"
        }
    }
    private class XAxisFormater : IAxisValueFormatter {
        var localTimeHour = LocalTime.now().hour
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            axis?.axisMinimum = 0F
            axis?.granularity = 12F
            if (localTimeHour == 0) {
                when(value) {
                    0F -> return "00:00"
                    12F -> return "12:00"
                    24F -> return "00:00"
                    36F -> return "12:00"
                    48F -> return "00:00"
                    60F -> return "12:00"
                    72F -> return "00:00"
                }
            } else if (localTimeHour < 12) {
                when(value) {
                    0F -> return "$localTimeHour:00"
                    12F -> return (localTimeHour+12).toString() + ":00"
                    24F -> return "$localTimeHour:00"
                    36F -> return (localTimeHour+12).toString() + ":00"
                    48F -> return "$localTimeHour:00"
                    60F -> return (localTimeHour+12).toString() + ":00"
                }
            } else {
                when(value) {
                    0F -> return "$localTimeHour:00"
                    12F -> return (localTimeHour-12).toString() + ":00"
                    24F -> return "$localTimeHour:00"
                    36F -> return (localTimeHour-12).toString() + ":00"
                    48F -> return "$localTimeHour:00"
                    60F -> return (localTimeHour-12).toString() + ":00"
                }
            }
            return value.toString()
        }
    }
    //end region

}
