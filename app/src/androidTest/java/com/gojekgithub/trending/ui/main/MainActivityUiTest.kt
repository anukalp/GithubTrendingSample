package com.gojekgithub.trending.ui.main

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.gojekgithub.trending.R
import com.gojekgithub.trending.TestTrendingApplication
import com.gojekgithub.trending.data.model.GitRepositoryModel
import com.gojekgithub.trending.utils.CountDownTestUtil
import com.gojekgithub.trending.utils.EspressoTestUtil
import com.gojekgithub.trending.utils.MainRecyclerViewValidations.validateUIForRecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@LargeTest
class MainActivityUiTest {

    /**
     * Use [ActivityScenarioRule] to create and launch the activity under test, and close it
     * after test completes. This is a replacement for [androidx.test.rule.ActivityTestRule].
     */


    @get:Rule
    val activityRule = ActivityTestRule(TrendingActivity::class.java, true, false)

    @Inject
    lateinit var mockWebServer: MockWebServer

    private lateinit var app: TestTrendingApplication
    private val gson = Gson()

    @Before
    fun setup() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        app = instrumentation.targetContext.applicationContext as TestTrendingApplication
        app.inject(this)
    }

    @Test
    fun testActivity_UiElements_When_Loading() {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        val result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )
        mockWebServer.enqueue(
            MockResponse().setBodyDelay(2, TimeUnit.SECONDS).setResponseCode(
                200
            ).setBody(gson.toJson(result))
        )
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation()
                .targetContext, TrendingActivity::class.java
        )
        activityRule.launchActivity(intent)
        val fragment =
            activityRule.activity.supportFragmentManager.findFragmentByTag(MainFragment.TAG)

        onView(withId(R.id.toolbar)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.trending_title)).check(matches(withText(R.string.app_name)))
        onView(withId(R.id.container)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        MatcherAssert.assertThat(fragment, CoreMatchers.notNullValue())

        onView(withId(R.id.shimmerLayout)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.swipeContainer)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.layout_error)).check(matches(withEffectiveVisibility(Visibility.GONE)))

        //Added delay to see loading to recycler view ui
        CountDownTestUtil.waitForUI(2)

        onView(withId(R.id.shimmerLayout)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.layout_error)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.swipeContainer)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.listView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        validateUIForRecyclerView(result)
    }

    @Test
    fun testActivity_UiElements_When_ResponseError() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("Internal Server Error"))
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation()
                .targetContext, TrendingActivity::class.java
        )
        activityRule.launchActivity(intent)

        val fragment =
            activityRule.activity.supportFragmentManager.findFragmentByTag(MainFragment.TAG)

        onView(withId(R.id.toolbar)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.trending_title)).check(matches(withText(R.string.app_name)))
        onView(withId(R.id.container)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        MatcherAssert.assertThat(fragment, CoreMatchers.notNullValue())

        //Added delay to see espresso test in effect
        CountDownTestUtil.waitForUI(1)

        onView(withId(R.id.shimmerLayout)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.swipeContainer)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.layout_error)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))


        onView(withId(R.id.image_icon)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.button_retry)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun testActivityUiElements_When_ResponseSuccess() {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        val result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(gson.toJson(result)))

        val intent = Intent(
            InstrumentationRegistry.getInstrumentation()
                .targetContext, TrendingActivity::class.java
        )
        activityRule.launchActivity(intent)

        EspressoTestUtil.disableAnimations(activityRule)

        val fragment =
            activityRule.activity.supportFragmentManager.findFragmentByTag(MainFragment.TAG)

        onView(withId(R.id.toolbar)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.trending_title)).check(matches(withText(R.string.app_name)))
        onView(withId(R.id.container)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        MatcherAssert.assertThat(fragment, CoreMatchers.notNullValue())

        onView(withId(R.id.shimmerLayout)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.layout_error)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.swipeContainer)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.listView)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        validateUIForRecyclerView(result)

    }

}