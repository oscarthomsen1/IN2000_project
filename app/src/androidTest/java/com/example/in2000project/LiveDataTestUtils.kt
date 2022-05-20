package com.example.in2000project

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

// Ansvarlig Tobias
/**
 * LiveData extension function.
 * Used for testing LiveData.
 * Gets the live data or waits for the data. (2 seconds timeout)
 */
fun<T> LiveData<T>.getOrAwaitValue(): T{
    var data: T? = null

    // Sets a latch to handle LiveData from other threads.
    val latch = CountDownLatch(1)

    val observer = object : Observer<T> {
        override fun onChanged(t: T) {
            data = t
            this@getOrAwaitValue.removeObserver(this)
            latch.countDown()
        }
    }

    this.observeForever(observer)

    // Sets a timeout of 2 seconds for the API response
    try {
        if(!latch.await(2, TimeUnit.SECONDS)){
            throw TimeoutException("Live Data never gets its value")
        }
    }finally {
        this.removeObserver(observer)
    }


    return data as T
}