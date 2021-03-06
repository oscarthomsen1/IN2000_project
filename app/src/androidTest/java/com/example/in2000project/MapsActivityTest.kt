package com.example.in2000project

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MapsActivityTest {

    // Sets up the MainActivity so it can be tested
    @get:Rule
    val activityRule: ActivityScenarioRule<MapsActivity> = ActivityScenarioRule(MapsActivity::class.java)

    // Tests if the correct activity is currently being displayed
    @Test
    fun test_isActivityInView() {
        onView(ViewMatchers.withId(R.id.maps_activity))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    /**
     * The rest of the tests are testing of certain views
     * are currently being displayed on the screen.
     */
    @Test
    fun test_visibility_map() {
        onView(ViewMatchers.withId(R.id.map))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_bottom_navigation() {
        onView(ViewMatchers.withId(R.id.bottom_navigation))
            .check(matches(ViewMatchers.isDisplayed()))
    }
}