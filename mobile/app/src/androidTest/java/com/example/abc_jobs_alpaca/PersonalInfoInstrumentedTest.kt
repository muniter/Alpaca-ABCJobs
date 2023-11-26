package com.example.abc_jobs_alpaca

import android.content.Intent
import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.abc_jobs_alpaca.view.WelcomeActivity
import io.github.serpro69.kfaker.Faker
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersonalInfoInstrumentedTest {
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
    fun fillAndCheckPersonalInfo() {
        login()

        onView(withId(R.id.editTextFullName))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(withId(R.id.editTextDate))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(withId(R.id.countrySpinner))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(withId(R.id.editTextCity))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(withId(R.id.editTextAddress))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(withId(R.id.editTextPhone))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(withId(R.id.editTextBio))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(withId(R.id.addOrEditButton))
            .perform(click())

        onView(withId(R.id.editTextFullName))
            .check(matches(isDisplayed()))
            .check(matches(not(isEnabled())))

        onView(withId(R.id.editTextDate))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(withId(R.id.countrySpinner))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(withId(R.id.editTextCity))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(withId(R.id.editTextAddress))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(withId(R.id.editTextPhone))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        onView(withId(R.id.editTextBio))
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))

        val city = faker.address.city()
        fillAndCheck(R.id.editTextCity, R.id.labelCityError, city)

        val address = faker.address.fullAddress()
        fillAndCheck(R.id.editTextAddress, R.id.labelAddressError, address)

        var phone = faker.random.nextLong(999999999999999)
        if(phone < 10)
            phone += 10
        fillAndCheck(R.id.editTextPhone, R.id.labelPhoneError, phone.toString())

        val bio = faker.random.randomString(28)
        fillAndCheck(R.id.editTextBio, R.id.labelBioError, bio)

        onView(withId(R.id.button_save_personal_info))
            .check(matches(isEnabled()))
            .perform(click())

        activityTestRule.finishActivity();
        activityTestRule.launchActivity(null)

        login()
        onView(withId(R.id.editTextCity))
            .check(matches(ViewMatchers.withText(city)))

        onView(withId(R.id.editTextAddress))
            .check(matches(ViewMatchers.withText(address)))

        onView(withId(R.id.editTextPhone))
            .check(matches(ViewMatchers.withText(phone.toString())))

        onView(withId(R.id.editTextBio))
            .check(matches(ViewMatchers.withText(bio)))
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
            .check(matches(isEnabled()))
            .perform(click())

        Thread.sleep(1500)

        onView(withId(R.id.text_home))
            .check(matches(isDisplayed()))

        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open());

        onView(withId(R.id.nav_personal_info))
            .perform(click())

        Thread.sleep(1500)
    }

    private fun fillAndCheck(field: Int, validationLabel: Int, validValue: String) {
        onView(withId(field))
            .perform(clearText())
            .perform(typeText("a"), closeSoftKeyboard())

        onView(withId(validationLabel))
            .check(matches(isDisplayed()))

        onView(withId(R.id.button_save_personal_info))
            .check(matches(not(isEnabled())))

        onView(withId(field))
            .perform(clearText())
            .perform(typeText(validValue), closeSoftKeyboard())

        onView(withId(validationLabel))
            .check(matches(not(isDisplayed())))
    }
}