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
        // Sets up the class for testing
        viewModel = MainActivityViewModel()

        // Calls a method that gets all the information form the API
        // so that the class gets filled with data
        viewModel.loadProbability(59.9139, 10.7522)
    }

    // Test to ensure that the right class is currently active
    @Test
    fun testIfViewModelHasTag() {
        assertThat(viewModel.TAG).isEqualTo("MainActivityViewModel")

    }

    // LiveData test that references the custom made extension method
    // "getOrAwaitValue" from LiveDataTestUtils file.
    // Tests if the LiveData property in the viewmodel class
    // gets new data after loadProbability is called
    @Test
    fun testLiveData() {
        val result = viewModel.viewModelData.getOrAwaitValue()
        assertThat(result != null).isTrue()
    }

    // tests if the method getData actually gets the correct value
    @Test
    fun testGetData() {
        val result = viewModel.viewModelData
        assertThat(result).isEqualTo(viewModel.getData())
    }
}