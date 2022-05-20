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
class InfoActivityTest {

    // Sets up the InfoActivity so it can be tested
    @get:Rule
    val activityRule: ActivityScenarioRule<InfoActivity> = ActivityScenarioRule(InfoActivity::class.java)

    // Tests if InfoActivity is currently the active activity
    @Test
    fun test_isActivityInView() {
        onView(ViewMatchers.withId(R.id.info_activity))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    // Tests if the floating action button is currently on screen.
    @Test
    fun test_visibility_floating_action_button() {
        onView(ViewMatchers.withId(R.id.floatingActionButton))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    // Tests if the headline textview is currently on screen.
    @Test
    fun test_visibility_textview() {
        onView(ViewMatchers.withId(R.id.textView))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    // Tests if the first paragraph is currently on screen.
    @Test
    fun test_visibility_textview2() {
        onView(ViewMatchers.withId(R.id.textView2))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    // Tests if the second paragraph is currently on screen.
    @Test
    fun test_visibility_textview3() {
        onView(ViewMatchers.withId(R.id.textView3))

            // This view is actually not visible unless
            // we scroll a bit down in the scrollview.
            .perform(ViewActions.scrollTo())
            .check(matches(ViewMatchers.isDisplayed()))
    }

    // Tests if the first image is currently on screen.
    @Test
    fun test_visibility_imageview() {
        onView(ViewMatchers.withId(R.id.imageView))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    // Tests if the bottom navigation menu is currently on screen.
    @Test
    fun test_visibility_bottom_navigation() {
        onView(ViewMatchers.withId(R.id.bottom_navigation))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    // Tests if the textview displays the correct text.
    @Test
    fun test_text_in_textview() {
        onView(ViewMatchers.withId(R.id.textView))
            .check(matches(withText(R.string.Nordlys)))
    }

    // Tests if the textview displays the correct text.
    @Test
    fun test_text_in_textview2() {
        onView(ViewMatchers.withId(R.id.textView2))
            .check(matches(withText(R.string.nordlysInfo1)))
    }

    // Tests if the textview displays the correct text.
    @Test
    fun test_text_in_textview3() {
        onView(ViewMatchers.withId(R.id.textView3))
            .check(matches(withText(R.string.nordlysInfo3)))
    }
}