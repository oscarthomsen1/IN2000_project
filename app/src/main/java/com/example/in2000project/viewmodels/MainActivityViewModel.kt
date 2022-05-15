package com.example.in2000project.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000project.data.AuroraData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//Ansvarlig Tiril
class MainActivityViewModel: ViewModel() {
    val TAG = "MainActivityViewModel"
    private val datasource: AuroraData = AuroraData()

    var viewModelData = MutableLiveData<List<Any?>>()

    fun loadProbability(lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO){
            datasource.auroraProbabilityNowcast(lat, lon).also {
                viewModelData.postValue(it)
            }
        }
    }

    fun getData(): MutableLiveData<List<Any?>> {
        return viewModelData
    }

    fun checkSun(): Boolean{
        return datasource.checkSunrise()
    }

    fun checkClouds(): Boolean{
        return datasource.checkClouds()
    }
}