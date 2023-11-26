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
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.abc_jobs_alpaca.view.WelcomeActivity
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ExamsTest {

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
    fun startAndCancelExam() {
        login()

        onView(withId(R.id.listExam))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, MyViewAction.clickChildViewWithId(R.id.startExamBtn)))

        Thread.sleep(500)

        onView(withId(R.id.radioButton))
            .perform(click())

        val question1: ViewInteraction = onView(withId(R.id.questionBodyTextView))
        var question1Text = MyViewAction.getText(question1)

        onView(withId(R.id.submitButton))
            .perform(click())

        Thread.sleep(1500)

        val question2: ViewInteraction = onView(withId(R.id.questionBodyTextView))
        var question2Text = MyViewAction.getText(question2)

        Assert.assertNotEquals(question1Text, question2Text);

        onView(withId(R.id.leaveButton))
            .perform(click())

        onView(Matchers.allOf(withId(R.id.startExamBtn), ViewMatchers.isDisplayed()))
    }

    private fun login() {

        onView(withId(R.id.button_user_registered))
            .perform(click())

        onView(withId(R.id.editTextEmail))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("candidato16@email.com"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.editTextPassword))
            .perform(ViewActions.clearText())
            .perform(ViewActions.typeText("123456789"), ViewActions.closeSoftKeyboard())

        onView(withId(R.id.button_login))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(click())

        Thread.sleep(1500)

        onView(withId(R.id.text_home))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        onView(withId(R.id.drawer_layout))
            .check(ViewAssertions.matches(DrawerMatchers.isClosed(Gravity.LEFT))) // Left Drawer should be closed.
            .perform(DrawerActions.open());

        onView(withId(R.id.nav_exam_list))
            .perform(click())

        Thread.sleep(1500)
    }
}

object MyViewAction {
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

    fun getText(matcher: ViewInteraction): String {
        var text = String()
        matcher.perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "Text of the view"
            }

            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView
                text = tv.text.toString()
            }
        })

        return text
    }
}