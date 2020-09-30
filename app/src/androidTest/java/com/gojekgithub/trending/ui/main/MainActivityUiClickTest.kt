package com.gojekgithub.trending.ui.main

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
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
import com.gojekgithub.trending.utils.DrawableMatcher
import com.gojekgithub.trending.utils.EspressoTestUtil
import com.gojekgithub.trending.utils.MainRecyclerViewValidations.clickRecyclerViewItems
import com.gojekgithub.trending.utils.MainRecyclerViewValidations.validateSuccessLayout
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
class MainActivityUiClickTest {

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
    fun testClick_RetryButton_When_Failure() {
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
            .check(matches(DrawableMatcher.withDrawable(R.drawable.nointernet_connection)))
        onView(withId(R.id.text_title)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText(R.string.error)))
        onView(withId(R.id.text_description)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText(R.string.error_description)))

        onView(withId(R.id.button_retry)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .check(matches(withText(R.string.retry)))
            .perform(click())

        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        val result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(gson.toJson(result)))

        CountDownTestUtil.waitForUI(1)

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

    @Test
    fun testClick_When_SortByStars() {
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

        validateSuccessLayout(fragment)

        validateUIForRecyclerView(result)

        openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext())

        // Click the item.
        onView(withText(R.string.sort_stars))
            .perform(click())

        val sortedDataByStars = arrayListOf<GitRepositoryModel>()
        sortedDataByStars.addAll(result)
        sortedDataByStars.sortByDescending { repoModel ->
            repoModel.stars
        }

        CountDownTestUtil.waitForUI(1)

        //Click to expand ui
        clickRecyclerViewItems(result)

        validateUIForRecyclerView(sortedDataByStars, true)
    }

    @Test
    fun testClick_When_SortByNameItem() {
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

        validateSuccessLayout(fragment)

        validateUIForRecyclerView(result)

        openActionBarOverflowOrOptionsMenu(
            ApplicationProvider.getApplicationContext())

        // Click the item.
        onView(withText(R.string.sort_name))
            .perform(click())

        val sortedDataByName = arrayListOf<GitRepositoryModel>()
        sortedDataByName.addAll(result)
        sortedDataByName.sortBy { repoModel ->
            repoModel.name
        }

        CountDownTestUtil.waitForUI(1)

        //Click to expand ui
        clickRecyclerViewItems(result)

        validateUIForRecyclerView(sortedDataByName, true)
    }


    @Test
    fun testRecycler_When_ViewExpandAndCollapse() {
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

        validateSuccessLayout(fragment)

        //Click to expand ui
        clickRecyclerViewItems(result)

        CountDownTestUtil.waitForUI(300, TimeUnit.MILLISECONDS)

        //Verify Expandable UI
        validateUIForRecyclerView(result, false)

        CountDownTestUtil.waitForUI(300, TimeUnit.MILLISECONDS)

        //Click to collapse ui
        clickRecyclerViewItems(result)

        //Verify collapsed ui
        validateUIForRecyclerView(result, true)
    }


}