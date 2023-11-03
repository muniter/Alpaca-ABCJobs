package com.example.abc_jobs_alpaca

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.abc_jobs_alpaca.view.MainActivity
import com.example.abc_jobs_alpaca.view.WelcomeActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WelcomeFragmentInstrumentedTest {

    @Rule
    @JvmField
    var activityTestRule : ActivityTestRule<WelcomeActivity> = ActivityTestRule(WelcomeActivity::class.java, true, false)

    @Before
    fun setUp(){
        val intent = Intent()

        activityTestRule.launchActivity(intent)
    }

    @Test
    fun checkRegisteredButtonText(){
        onView(withId(R.id.button_user_registered))   //first block
            .check(matches(withText("Ya tengo cuenta en ABC")))
    }

    @Test
    fun checkWelcomeUnregisteredButtonText(){
        onView(withId(R.id.button_welcome_unregistered))   //first block
            .check(matches(withText("Registrarse")))
    }
}