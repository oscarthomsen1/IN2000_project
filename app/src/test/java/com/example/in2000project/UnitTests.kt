package com.example.in2000project

import com.github.mikephil.charting.data.Entry
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.Mockito.doCallRealMethod
import org.mockito.junit.MockitoJUnitRunner
import org.hamcrest.CoreMatchers.`is`
import java.security.KeyStore


/**
 * All of the unit tests divided pr activity
 * Some of the activities has proven difficult to run unit tests on
 * because of the high dependency of android specific libraries.
 * This has resulted in a lot more integration tests, than unit tests.
 */
@RunWith(MockitoJUnitRunner::class)
class UnitTests {
    private lateinit var SUT: MainActivity
    private val parameters = mutableListOf<Float?>()
    private val expectedResult = ArrayList<Entry>()

    // Setup part that mocks the MainActivity class so that we can test it
    // without its dependencies.
    @Before
    fun setUp() {
        // SUT - (System Under Test)
        // The class we are going to be testing
        SUT = Mockito.mock(MainActivity::class.java)

        // First create a sample input and output which we should expect.
        parameters.addAll(listOf(3.0F, 3.0F, 4.0F, 3.0F, 2.0F, 2.0F,
            3.0F, 3.0F, 2.0F, 3.0F, 2.0F, 2.0F, 2.0F, 2.0F, 3.0F, 2.0F,
            2.0F, 2.0F, 2.0F, 3.0F, 2.0F, 2.0F, 2.0F, 2.0F, 1.0F))

        expectedResult.addAll(listOf(Entry( 0.0F ,3.0F), Entry(3.0F, 3.0F),
            Entry(6.0F, 4.0F), Entry(9.0F, 3.0F), Entry(12.0F, 2.0F),
            Entry(15.0F, 2.0F), Entry(18.0F, 3.0F), Entry(21.0F, 3.0F),
            Entry(24.0F, 2.0F), Entry(27.0F, 3.0F), Entry(30.0F, 2.0F),
            Entry(33.0F, 2.0F), Entry(36.0F, 2.0F), Entry(39.0F, 2.0F),
            Entry(42.0F, 3.0F), Entry(45.0F, 2.0F), Entry(48.0F, 2.0F),
            Entry(51.0F, 2.0F), Entry(54.0F, 2.0F), Entry(57.0F, 3.0F),
            Entry(60.0F, 2.0F), Entry(63.0F, 2.0F), Entry(66.0F, 2.0F),
            Entry(69.0F, 2.0F), Entry(72.0F, 1.0F)))
    }

    // Simple test to confirm that the testing framework is running correctly
    // by confirming that simple math is working.
    @Test
    fun addition_isCorrect() {
        val number = 4
        assertThat(number).isEqualTo(2+2)
    }

    // Checks if the method does not return null.
    @Test
    fun test_datavalues1_does_not_return_null() {
        Mockito.`when`(SUT.datavalues1(parameters)).thenReturn(expectedResult)
        assertThat(SUT.datavalues1(parameters)).isNotNull()
    }

    // Test to check weather the method datavalues1 in MainActivity returns
    // the expected values given a certain input.
    @Test
    fun test_datavalues1_has_expected_output() {
        Mockito.`when`(SUT.datavalues1(parameters)).thenReturn(expectedResult)
        assertThat(SUT.datavalues1(parameters)).isEqualTo(expectedResult)
    }

    // Same test as the one above. Only this time we're using mockito
    // to verify the test, rather than using the google truth library
    @Test
    fun test_datavalues1_has_expected_output2() {
        doCallRealMethod().`when`(SUT).datavalues1(parameters)
        SUT.datavalues1(parameters)
        Mockito.verify(SUT).datavalues1(parameters)
    }

    // Checks if the method does not return null.
    @Test
    fun test_datavalues2_does_not_resturn_null() {
        Mockito.`when`(SUT.datavalues2(parameters)).thenReturn(expectedResult)
        assertThat(SUT.datavalues2(parameters)).isNotNull()
    }

    // Test to check weather the method datavalues1 in MainActivity returns
    // the expected values given a certain input.
    @Test
    fun test_datavalues2_has_expected_output() {
        Mockito.`when`(SUT.datavalues2(parameters)).thenReturn(expectedResult)
        assertThat(SUT.datavalues2(parameters)).isEqualTo(expectedResult)
    }
}