package com.example.in2000project.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000project.data.AuroraData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    private val datasource: AuroraData = AuroraData()

    private var data = MutableLiveData<List<Any?>>()

    fun loadProbability(placename: String){
        viewModelScope.launch(Dispatchers.IO){
            datasource.auroraProbabilityNowcast(placename).also {
                data.postValue(it)
            }
        }
    }

    fun getData(): MutableLiveData<List<Any?>> {
        return data
    }

    fun checkSun(): Boolean{
        return datasource.checkSunrise()
    }

    fun checkClouds(): Boolean{
        return datasource.checkClouds()
    }
}