package com.example.in2000project

import android.content.ComponentName
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.view.View.VISIBLE
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.EnumSet.allOf


@LargeTest
@RunWith(AndroidJUnit4ClassRunner::class)
class ApplicationFlowTest {

    private lateinit var scenario: ActivityScenario<InfoActivity>


    @Before
    fun setUp() {
        scenario = launchActivity()
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test
    fun testAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.example.in2000project", appContext.packageName)
    }

    @Test
    fun testNavigationBetweenInfoAndSettings() {
//        intended(hasComponent(ComponentName(getTargetContext(), MapsActivity::class.java)))

        onView(withId(R.id.floatingActionButton)).perform(click())
        Espresso.pressBack()
        onView(withId(R.id.floatingActionButton)).perform(click())
        onView(
            Matchers.allOf(
                withContentDescription("Navigate up"),
                isDisplayed()
            )
        ).perform(click())



//        onView(withId(R.id.navMap)).perform(click())


    }
}