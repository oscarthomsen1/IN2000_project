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
class MainActivityTest {

    // Sets up the MainActivity so it can be tested
    @get:Rule
    val activityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    // Tests if the correct activity is currently being displayed
    @Test
    fun test_isActivityInView() {
        onView(ViewMatchers.withId(R.id.main_activity))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    /**
     * The rest of the tests are testing of certain views
     * are currently being displayed on the screen.
     */

    @Test
    fun test_visibility_main_activity_scrollView() {
        onView(ViewMatchers.withId(R.id.main_activity_scrollView))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_soek() {
        onView(ViewMatchers.withId(R.id.søk))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_autocomplete_fragment() {
        onView(ViewMatchers.withId(R.id.autocomplete_fragment))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_dinPosisjson() {
        onView(ViewMatchers.withId(R.id.dinPosisjson))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_sannsynlighetsView() {
        onView(ViewMatchers.withId(R.id.sannsynlighetsView))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_bottom_navigation() {
        onView(ViewMatchers.withId(R.id.bottom_navigation))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_location() {
        onView(ViewMatchers.withId(R.id.location))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_nordlysVarsel() {
        onView(ViewMatchers.withId(R.id.nordlysVarsel))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_nordlysGraf() {
        onView(ViewMatchers.withId(R.id.nordlysGraf))
            .check(matches(ViewMatchers.isDisplayed()))
    }
}