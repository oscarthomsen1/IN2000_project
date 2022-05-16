package com.example.in2000project


import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
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
        Assert.assertEquals("com.example.in2000project", appContext.packageName)
    }

    @Test
    fun testNavigationBetweenInfoAndSettings() {
//        intended(hasComponent(ComponentName(getTargetContext(), MapsActivity::class.java)))

        onView(withId(R.id.floatingActionButton)).perform(click())
        pressBack()
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