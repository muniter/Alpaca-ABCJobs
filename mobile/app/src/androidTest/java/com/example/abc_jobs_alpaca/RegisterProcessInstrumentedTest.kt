package com.example.abc_jobs_alpaca

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.abc_jobs_alpaca.view.WelcomeActivity
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterProcessInstrumentedTest {

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
        Espresso.onView(ViewMatchers.withId(R.id.button_welcome_unregistered))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.button_user_register_candidate))
            .check(matches(ViewMatchers.withText("Candidato")))

        Espresso.onView(ViewMatchers.withId(R.id.button_welcome_register_enterprise))
            .check(matches(ViewMatchers.withText("Empresa Contratante")))

        Espresso.onView(ViewMatchers.withId(R.id.register_type_label))
            .check(matches(ViewMatchers.withText("Como quiere registrarse?")))
    }

    @Test
    fun checkRegisterCandidateForm(){
        Espresso.onView(ViewMatchers.withId(R.id.button_welcome_unregistered))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.button_user_register_candidate))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.labelName))
            .check(matches(ViewMatchers.withText("Nombres")))

        Espresso.onView(ViewMatchers.withId(R.id.editTextName))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.labelLastName))
            .check(matches(ViewMatchers.withText("Apellidos")))

        Espresso.onView(ViewMatchers.withId(R.id.editTextLastName))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.labelEmail))
            .check(matches(ViewMatchers.withText("Correo Electrónico")))

        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.labelPassword))
            .check(matches(ViewMatchers.withText("Contraseña")))

        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.labelRepeatPassword))
            .check(matches(ViewMatchers.withText("Repita la contraseña")))

        Espresso.onView(ViewMatchers.withId(R.id.editTextRepeatPassword))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.checkBoxTerms))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.button_register))
            .check(matches(not(isEnabled())))
    }

    @Test
    fun checkValidationsAreShownAndWorks(){
        Espresso.onView(ViewMatchers.withId(R.id.button_welcome_unregistered))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.button_user_register_candidate))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.editTextName))
            .perform(typeText("a"), closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.labelNameError))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.button_register))
            .check(matches(not(isEnabled())))

        Espresso.onView(ViewMatchers.withId(R.id.editTextName))
            .perform(typeText("valid name"), closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.labelNameError))
            .check(matches(not(isDisplayed())))

        Espresso.onView(ViewMatchers.withId(R.id.editTextLastName))
            .perform(typeText("a"), closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.labelLastNameError))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.button_register))
            .check(matches(not(isEnabled())))

        Espresso.onView(ViewMatchers.withId(R.id.editTextLastName))
            .perform(typeText("valid lastname"), closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.labelLastNameError))
            .check(matches(not(isDisplayed())))

        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(typeText("a"), closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.labelEmailError))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.button_register))
            .check(matches(not(isEnabled())))

        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(typeText("email@mail.co"), closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.labelEmailError))
            .check(matches(not(isDisplayed())))

        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(typeText("a"), closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.labelPasswordError))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.button_register))
            .check(matches(not(isEnabled())))

        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(clearText())
            .perform(typeText("123456789"), closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.labelPasswordError))
            .check(matches(not(isDisplayed())))

        Espresso.onView(ViewMatchers.withId(R.id.editTextRepeatPassword))
            .perform(typeText("987654321"), closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.labelRepeatPasswordError))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.button_register))
            .check(matches(not(isEnabled())))

        Espresso.onView(ViewMatchers.withId(R.id.editTextRepeatPassword))
            .perform(clearText())
            .perform(typeText("123456789"), closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.labelRepeatPasswordError))
            .check(matches(not(isDisplayed())))

        Espresso.onView(ViewMatchers.withId(R.id.checkBoxTerms))
            .perform(click())
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.labelTermsError))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.checkBoxTerms))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.labelTermsError))
            .check(matches(not(isDisplayed())))

        Espresso.onView(ViewMatchers.withId(R.id.button_register))
            .check(matches(isEnabled()))
    }
}