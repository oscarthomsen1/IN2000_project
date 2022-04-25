package com.example.in2000project.adapers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.in2000project.R

class AuroraAdapter (private val data: MutableList<Any>): RecyclerView.Adapter<AuroraAdapter.ViewHolder>() {
    class ViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        val timeView: TextView = view.findViewById(R.id.currentTime)
        val weatherImageView: ImageView = view.findViewById(R.id.weatherImage)
        val auroraView: TextView = view.findViewById(R.id.northernLight)
        val kpIndexViwe: TextView = view.findViewById(R.id.kpIndex)
        val cloudView: TextView = view.findViewById(R.id.cloudCoverage)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_main, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return 0
    }
}
