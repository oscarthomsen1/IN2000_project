package com.example.in2000project

import com.example.in2000project.MainActivity
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


/**
 * All of the unit tests divided pr activity
 * Some of the activities has proven difficult to run unit tests on
 * because of the high dpendency of android specific libraries.
 * This has resulted in a lot more integration tests, than unit tests.
 */
@RunWith(MockitoJUnitRunner::class)
class UnitTests {

    // Simple test to confirm that the testing framework is running correctly
    @Test
    fun addition_isCorrect() {
        val number = 4
        assertThat(number).isEqualTo(2+2)
    }

    @Test
    fun test_MainActivity_INSERTMETHOD() {
        // Accesses the class MainActivity
        val classUnderTest = Mockito.mock(MainActivity::class.java)

        // Difines which method that is to be tested
        val method = classUnderTest.javaClass.getDeclaredMethod("datavalues1", String::class.java)

        // This particular method is private, so we need to make it accessible
        method.isAccessible = true

        // A predefined set of input parameters
        val parameters: MutableList<Float>
//        parameters.add(2.2F)
            //inn - MutableList<Float?>) : out - ArrayList<Entry>

    }
}