package com.example.abc_jobs_alpaca

import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.abc_jobs_alpaca.view.WelcomeActivity
import io.github.serpro69.kfaker.Faker
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrarEntrevistaVacanteInstrumentedTest {
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

        Espresso.onView(ViewMatchers.withId(R.id.vacancylist))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, VacanteListViewAction.clickChildViewWithId(R.id.vacancy_item)))

        Thread.sleep(1000)

        Espresso.onView(ViewMatchers.withId(R.id.shortlisted_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, VacanteListViewAction.clickChildViewWithId(R.id.shortlisted_result_set)))

        Thread.sleep(1000)

        val score = faker.random.nextInt(1,99)
        Espresso.onView(ViewMatchers.withId(R.id.technical_proof_result_field))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText(score.toString()), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.technical_proof_cancel_button))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        Espresso.onView(ViewMatchers.withId(R.id.technical_proof_cancel_button))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(ViewActions.click())

    }

    private fun login() {

        Espresso.onView(ViewMatchers.withId(R.id.button_user_registered))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("empresa1@email.com"), ViewActions.closeSoftKeyboard())

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

        Espresso.onView(ViewMatchers.withId(R.id.nav_vacancy))
            .perform(ViewActions.click())

        Thread.sleep(500)
    }
}

private object VacanteListViewAction {
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
