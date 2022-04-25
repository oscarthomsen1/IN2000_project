package com.example.in2000project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2000project.data.AuroraData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    val datasource: AuroraData = AuroraData()

    private var data = MutableLiveData<List<Any>>()

    fun loadProbability(placename: String){
        viewModelScope.launch(Dispatchers.IO){
            datasource.AuroraProbabilityNowcast(placename).also {
                data.postValue(it)
            }
        }
    }

    fun getData(): MutableLiveData<List<Any>> {
        return data
    }


}