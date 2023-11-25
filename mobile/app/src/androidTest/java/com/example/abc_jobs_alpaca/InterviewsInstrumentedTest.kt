package com.example.abc_jobs_alpaca

import android.content.Intent
import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.abc_jobs_alpaca.view.WelcomeActivity
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InterviewsInstrumentedTest {

    @Rule
    @JvmField
    var activityTestRule: ActivityTestRule<WelcomeActivity> =
        ActivityTestRule(WelcomeActivity::class.java, true, false)

    @Before
    fun setUp() {
        val intent = Intent()

        activityTestRule.launchActivity(intent)
    }

    @Test
    fun checkInterviews() {

        login()
        Espresso.onView(allOf(ViewMatchers.withId(R.id.vacancyNameLabel), isDisplayed()))

    }

    private fun login() {

        Espresso.onView(ViewMatchers.withId(R.id.button_user_registered))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("candidato16@email.com"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("123456789"), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.button_login))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(ViewActions.click())

        Thread.sleep(1500)

        Espresso.onView(ViewMatchers.withId(R.id.text_home))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open());

        Espresso.onView(ViewMatchers.withId(R.id.nav_interview_list))
            .perform(ViewActions.click())

        Thread.sleep(500)
    }
}
