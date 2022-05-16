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
class InfoActivityTest {
    @get:Rule
    val activityRule: ActivityScenarioRule<InfoActivity> = ActivityScenarioRule(InfoActivity::class.java)

    @Test
    fun test_isActivityInView() {
        onView(ViewMatchers.withId(R.id.info_activity))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_floating_action_button() {
        onView(ViewMatchers.withId(R.id.floatingActionButton))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_textview() {
        onView(ViewMatchers.withId(R.id.textView))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_textview2() {
        onView(ViewMatchers.withId(R.id.textView2))
            .check(matches(ViewMatchers.isDisplayed()))
    }

//    @Test
//    fun test_visibility_textview5() {
//        onView(ViewMatchers.withId(R.id.textView5))
//            .check(matches(ViewMatchers.isDisplayed()))
//    }

    @Test
    fun test_visibility_imageview() {
        onView(ViewMatchers.withId(R.id.imageView))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_visibility_bottom_navigation() {
        onView(ViewMatchers.withId(R.id.bottom_navigation))
            .check(matches(ViewMatchers.isDisplayed()))
    }
}