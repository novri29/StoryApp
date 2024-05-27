package com.nov.storyapp.view.main

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.nov.storyapp.R
import com.nov.storyapp.helper.EspressoIdlingResource
import com.nov.storyapp.view.home.HomeActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Intents.init()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        release()
    }

    @Test
    fun loginLogout_Success() {
        Espresso.onView(withId(R.id.loginButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.loginButton)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.ed_login_email))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.ed_login_email)).perform(ViewActions.typeText("plmokn@gmail.com"))

        Espresso.onView(withId(R.id.ed_login_password))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.ed_login_password)).perform(ViewActions.typeText("plmokn123"))

        Espresso.onView(withId(R.id.loginButton))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.loginButton)).perform(ViewActions.click())
        Thread.sleep(5000L)
        intended(hasComponent(HomeActivity::class.java.name))

        Espresso.onView(withId(R.id.btnSetting)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.buttonLogout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.buttonLogout)).perform(ViewActions.click())
    }
}