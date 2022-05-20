package com.example.in2000project


import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import org.junit.runner.RunWith


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
        assertThat("com.example.in2000project").isEqualTo(appContext.packageName)
    }

    @Test
    fun testNavigationBetweenInfoAndSettings() {
        // Test starts the app in InfoActivity
        // then it clicks on the settings button to navigate there
        onView(withId(R.id.floatingActionButton)).perform(click())

        // After loading SettingsActivity the app clicks the
        // standard android back button to navigate back to InfoActivity
        pressBack()

        // Navigating to SettingsActivity
        onView(withId(R.id.floatingActionButton)).perform(click())

        // Navigating back to InfoActivity by clicking the back button
        // on the action bar in the top left corner of the screen.
        onView(
            Matchers.allOf(
                withContentDescription("Navigate up"),
                isDisplayed()
            )
        ).perform(click())
    }
}