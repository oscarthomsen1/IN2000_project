package com.example.in2000project

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SettingsActivityTest {

    @get:Rule
    val activityRule: ActivityScenarioRule<SettingsActivity> = ActivityScenarioRule(SettingsActivity::class.java)

    @Test
    fun test_isActivityInView() {
        onView(withId(R.id.settings_activity)).check(matches(isDisplayed()))  // Visibility test method 1
    }

    @Test
    fun test_visibility_of_toolbar_and_the_settings() {
        // Toolbar visibility check
        onView(withId(R.id.my_toolbar))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))  // Visibility test method 2

        // settings frame layout visibility check
        onView(withId(R.id.settings))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}