package com.example.abc_jobs_alpaca

import android.content.Intent
import android.view.Gravity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.abc_jobs_alpaca.view.WelcomeActivity
import io.github.serpro69.kfaker.Faker
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorkInfoInstrumentedTest {
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
    fun createAndDeleteWorkInfoItem() {
        login()

        Espresso.onView(ViewMatchers.withId(R.id.fab))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.workInfoSaveButton))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))

        val profession = faker.company.profession()
        Espresso.onView(ViewMatchers.withId(R.id.editTextRole))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText(profession), ViewActions.closeSoftKeyboard())

        val company = faker.company.name()
        Espresso.onView(ViewMatchers.withId(R.id.editTextCompany))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText(company), ViewActions.closeSoftKeyboard())

        val aditionalInfo = faker.random.randomString(28)
        Espresso.onView(ViewMatchers.withId(R.id.editTextDescriptionWork))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText(aditionalInfo), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.workInfoSaveButton))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(ViewActions.click())

        Thread.sleep(1000)
        activityTestRule.finishActivity();
        activityTestRule.launchActivity(null)

        login()

        Espresso.onView(ViewMatchers.withId(R.id.deleteWorkInfoItem))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(android.R.id.button1))
            .perform(ViewActions.click())

        Thread.sleep(1000)
        activityTestRule.finishActivity();
        activityTestRule.launchActivity(null)

        login()

        Espresso.onView(ViewMatchers.withId(R.id.deleteWorkInfoItem))
            .check(ViewAssertions.doesNotExist())
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

        Espresso.onView(ViewMatchers.withId(R.id.nav_work_info))
            .perform(ViewActions.click())

        Thread.sleep(500)
    }
}