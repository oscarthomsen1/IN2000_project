package com.example.in2000project

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {
    @get:Rule
    val activityRule: ActivityScenarioRule<MainActivity> = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_isActivityInView() {
        onView(ViewMatchers.withId(R.id.main_activity))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_main_activity_scrollView() {
        onView(ViewMatchers.withId(R.id.main_activity_scrollView))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_søk() {
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
    fun test_visibility_weatherImage() {
        onView(ViewMatchers.withId(R.id.weatherImage))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_northernLight() {
        onView(ViewMatchers.withId(R.id.northernLight))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_kpIndexLabel() {
        onView(ViewMatchers.withId(R.id.kpIndexLabel))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_cloudCoverageLabel() {
        onView(ViewMatchers.withId(R.id.cloudCoverageLabel))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_kpIndex() {
        onView(ViewMatchers.withId(R.id.kpIndex))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_cloudCoverage() {
        onView(ViewMatchers.withId(R.id.cloudCoverage))
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