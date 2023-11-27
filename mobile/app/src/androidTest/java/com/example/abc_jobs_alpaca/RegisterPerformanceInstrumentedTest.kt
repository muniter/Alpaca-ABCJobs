package com.example.abc_jobs_alpaca

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.abc_jobs_alpaca.view.WelcomeActivity
import io.github.serpro69.kfaker.Faker
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterPerformanceInstrumentedTest {
    private val faker = Faker()

    @Rule
    @JvmField
    var activityTestRule : ActivityTestRule<WelcomeActivity> = ActivityTestRule(WelcomeActivity::class.java, true, false)

    @Before
    fun setUp(){
        val intent = Intent()

        activityTestRule.launchActivity(intent)
    }

    @Test
    fun checkRegisterTypeLabels(){
        login()

        onView(withId(R.id.list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, PerformanceListViewAction.clickChildViewWithId(R.id.employee_result_set)))

        Thread.sleep(500)
        val score = faker.random.nextInt(1,99)
        onView(withId(R.id.performance_employee_result_field))
            .perform(clearText())
            .perform(typeText(score.toString()), closeSoftKeyboard())

        onView(withId(R.id.permorfance_employee_save_button))
            .check(matches(isEnabled()))

        onView(withId(R.id.permorfance_employee_cancel_button))
            .check(matches(isEnabled()))
            .perform(click())

    }

    private fun login() {

        onView(withId(R.id.button_user_registered))
            .perform(click())

        onView(withId(R.id.editTextEmail))
            .perform(clearText())
            .perform(typeText("empresa1@email.com"), closeSoftKeyboard())

        onView(withId(R.id.editTextPassword))
            .perform(clearText())
            .perform(typeText("123456789"), closeSoftKeyboard())

        onView(withId(R.id.button_login))
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(1500)

        onView(withId(R.id.text_home))
            .check(matches(isDisplayed()))

        onView(withId(R.id.drawer_layout))
            .check(matches(DrawerMatchers.isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open());

        onView(withId(R.id.nav_employee_list))
            .perform(click())

        Thread.sleep(500)
    }
}

private object PerformanceListViewAction {
    fun clickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun perform(uiController: UiController?, view: View) {
                val v = view.findViewById<View>(id)
                v.performClick()
            }
        }
    }
}
