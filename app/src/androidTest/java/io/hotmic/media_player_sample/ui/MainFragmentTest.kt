package io.hotmic.media_player_sample.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import io.hotmic.media_player_sample.MainActivity
import io.hotmic.media_player_sample.R
import io.hotmic.media_player_sample.adapter.StreamItemAdapter
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainFragmentTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    /**
     * Recyclerview comes into view
     */
    @Test
    fun test_isMainFragmentVisible_onAppLaunch() {
        onView(withId(R.id.rv_stream_list)).check(matches(isDisplayed()))
    }

    /**
     * Select list item, navigate to Player Screen
     */
    @Test
    fun text_selectListItem_isPlayerScreenVisible() {
        Thread.sleep(3000L)     // Data retrieve loading time
        onView(withId(R.id.rv_stream_list)).perform(actionOnItemAtPosition<StreamItemAdapter.ViewHolder>(0, click()))
        onView(withId(R.id.player_fragment_container)).check(matches(isDisplayed()))
        onView(withId(R.id.fcv_main_container)).check(matches(not(isDisplayed())))
    }

    /**
     * On backpressed from Player Screen
     */
    @Test
    fun text_onBackPress_isMainScreenVisible() {
        Thread.sleep(3000L)     // Data retrieve loading time
        onView(withId(R.id.rv_stream_list)).perform(actionOnItemAtPosition<StreamItemAdapter.ViewHolder>(0, click()))
        onView(withId(R.id.player_fragment_container)).check(matches(isDisplayed()))
        onView(withId(R.id.fcv_main_container)).check(matches(not(isDisplayed())))
        pressBack()
        onView(withId(R.id.fcv_main_container)).check(matches(isDisplayed()))
        onView(withId(R.id.player_fragment_container)).check(matches(not(isDisplayed())))
    }

}