package com.example.in2000project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000project.data.AuroraData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    val datasource: AuroraData = AuroraData()

    fun loadProbability(placename: String){
        viewModelScope.launch(Dispatchers.IO){
            datasource.AuroraProbabilityNowcast(placename)
        }
    }
}