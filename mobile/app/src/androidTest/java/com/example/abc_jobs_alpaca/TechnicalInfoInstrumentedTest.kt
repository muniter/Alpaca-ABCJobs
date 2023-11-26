package com.example.abc_jobs_alpaca

import android.content.Intent
import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.abc_jobs_alpaca.view.WelcomeActivity
import io.github.serpro69.kfaker.Faker
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.hasToString
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.startsWith
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TechnicalInfoInstrumentedTest {
    private val faker = Faker()

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
    fun createAndDeleteTechnicalInfoItem() {
        login()

        onView(withId(R.id.fab))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.spinnerTechnicalItem)).perform(click());
        onData(anything()).atPosition(1).perform(click())

        val bio = faker.random.randomString(28)
        onView(withId(R.id.editTextDescriptionInfo))
            .perform(clearText())
            .perform(typeText(bio), closeSoftKeyboard())

        onView(withId(R.id.technicalInfoSaveButton))
            .check(matches(ViewMatchers.isEnabled()))
            .perform(click())

        Thread.sleep(1000)
        activityTestRule.finishActivity();
        activityTestRule.launchActivity(null)

        login()

        onView(withId(R.id.deleteTechnicalInfoItem))
            .check(matches(ViewMatchers.isEnabled()))
            .perform(click())

        onView(withId(android.R.id.button1))
            .perform(click())

        Thread.sleep(1000)
        activityTestRule.finishActivity();
        activityTestRule.launchActivity(null)

        login()

        onView(withId(R.id.deleteTechnicalInfoItem))
            .check(doesNotExist())
    }


    private fun login() {

        onView(withId(R.id.button_user_registered))
            .perform(click())

        onView(withId(R.id.editTextEmail))
            .perform(clearText())
            .perform(typeText("candidato16@email.com"), closeSoftKeyboard())

        onView(withId(R.id.editTextPassword))
            .perform(clearText())
            .perform(typeText("123456789"), closeSoftKeyboard())

        onView(withId(R.id.button_login))
            .check(matches(ViewMatchers.isEnabled()))
            .perform(click())

        Thread.sleep(1500)

        onView(withId(R.id.text_home))
            .check(matches(ViewMatchers.isDisplayed()))

        onView(withId(R.id.drawer_layout))
            .check(matches(DrawerMatchers.isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open());

        onView(withId(R.id.nav_technical_info))
            .perform(click())

        Thread.sleep(500)
    }
}