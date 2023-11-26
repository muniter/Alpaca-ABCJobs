package com.example.abc_jobs_alpaca

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.abc_jobs_alpaca.view.WelcomeActivity
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @Rule
    @JvmField
    var activityTestRule : ActivityTestRule<WelcomeActivity> = ActivityTestRule(WelcomeActivity::class.java, true, false)

    @Before
    fun setUp(){
        val intent = Intent()

        activityTestRule.launchActivity(intent)
    }

    @Test
    fun checkButtonsText(){
        onView(withId(R.id.button_user_registered))
            .check(matches(withText("Ya tengo cuenta en ABC")))
        onView(withId(R.id.button_welcome_unregistered))
            .check(matches(withText("Registrarse")))
    }

    @Test
    fun checkValidationsAndLogin(){
        onView(withId(R.id.button_user_registered))
            .perform(click())

        onView(withId(R.id.editTextEmail))
            .perform(ViewActions.typeText("a"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.labelEmailError))
            .check(matches(isDisplayed()))

        onView(withId(R.id.button_login))
            .check(matches(Matchers.not(isEnabled())))

        onView(withId(R.id.editTextEmail))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("candidato1@email.com"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.labelEmailError))
            .check(matches(Matchers.not(isDisplayed())))

        onView(withId(R.id.button_login))
            .check(matches(Matchers.not(isEnabled())))

        onView(withId(R.id.editTextPassword))
            .perform(ViewActions.typeText("a"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.labelPasswordError))
            .check(matches(isDisplayed()))

        onView(withId(R.id.button_login))
            .check(matches(Matchers.not(isEnabled())))

        onView(withId(R.id.editTextPassword))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("123456789"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.labelPasswordError))
            .check(matches(Matchers.not(isDisplayed())))

        onView(withId(R.id.button_login))
            .check(matches(isEnabled()))

        onView(withId(R.id.button_login))
            .perform(click())

        Thread.sleep(500)

        onView(withId(R.id.text_home))
            .check(matches(isDisplayed()))
    }
}