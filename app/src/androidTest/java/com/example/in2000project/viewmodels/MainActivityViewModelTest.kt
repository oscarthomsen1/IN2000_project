package com.example.in2000project.viewmodels

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.in2000project.getOrAwaitValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class MainActivityViewModelTest {
    private lateinit var viewModel: MainActivityViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        viewModel = MainActivityViewModel()
        viewModel.loadProbability(59.9139, 10.7522)
    }

    @Test
    fun testIfViewModelHasTag() {
        assertThat(viewModel.TAG).isEqualTo("MainActivityViewModel")

    }

    @Test
    fun testLiveData() {
        val result = viewModel.viewModelData.getOrAwaitValue()
        Log.d("testLiveData", result.toString())
        assertThat(result != null).isTrue()
    }

    @Test
    fun testGetData() {
        val result = viewModel.viewModelData
        Log.d("testGetData", result.toString())
        assertThat(result).isEqualTo(viewModel.getData())
    }
}