package com.gojekgithub.trending.utils

import androidx.fragment.app.Fragment
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.gojekgithub.trending.R
import com.gojekgithub.trending.data.model.GitRepositoryModel
import com.gojekgithub.trending.ui.holder.TrendingItemViewHolder
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.TimeUnit

object MainRecyclerViewValidations {
    fun validateUIForRecyclerView(githubRepoDetails: List<GitRepositoryModel>, collapsed: Boolean = true) {
        val matcher = RecyclerViewMatcher(R.id.listView)

        for (pos in githubRepoDetails.indices) {

            Espresso.onView(ViewMatchers.withId(R.id.listView))
                .perform(RecyclerViewActions.scrollToPosition<TrendingItemViewHolder>(pos))

            val repoDetails: GitRepositoryModel = githubRepoDetails[pos]
            val stars = NumberFormat.getNumberInstance(Locale.US).format(repoDetails.stars)
            val forks = NumberFormat.getNumberInstance(Locale.US).format(repoDetails.forks)
            val description = "${repoDetails.description}(${repoDetails.url})"

            Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.description))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.withText(description)))

            Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.profileImage))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

            Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.author))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.withText(repoDetails.author)))
            Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.title))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.withText(repoDetails.name)))

            if(collapsed){
                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.language))
                    .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.languageColor))
                    .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))

                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.languageColor))
                    .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
                    .check(ViewAssertions.matches(BackgroundColorMatcher.withDrawableBackground(repoDetails.languageColor)))

                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.stars))
                    .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.stars_text))
                    .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
                    .check(ViewAssertions.matches(ViewMatchers.withText(stars)))
                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.forks))
                    .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))

                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.forks_text))
                    .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
                    .check(ViewAssertions.matches(ViewMatchers.withText(forks)))
            } else {
                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.language))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.languageColor))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(BackgroundColorMatcher.withDrawableBackground(repoDetails.languageColor)))

                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.stars))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.stars_text))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(ViewMatchers.withText(stars)))
                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.forks))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

                Espresso.onView(matcher.atPositionWithTargetId(pos, R.id.forks_text))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(ViewMatchers.withText(forks)))
            }

            //Added delay to see espresso tests in effect
            CountDownTestUtil.waitForUI(300, TimeUnit.MILLISECONDS)
        }
    }

    fun clickRecyclerViewItems(githubRepoDetails: List<GitRepositoryModel>) {
        val matcher = RecyclerViewMatcher(R.id.listView)

        for (pos in githubRepoDetails.indices) {

            Espresso.onView(ViewMatchers.withId(R.id.listView))
                .perform(RecyclerViewActions.scrollToPosition<TrendingItemViewHolder>(pos))

            val repoDetails: GitRepositoryModel = githubRepoDetails[pos]
            val stars = NumberFormat.getNumberInstance(Locale.US).format(repoDetails.stars)
            val forks = NumberFormat.getNumberInstance(Locale.US).format(repoDetails.forks)
            val description = "${repoDetails.description}(${repoDetails.url})"

            // Click the item.
            Espresso.onView(matcher.atPositionWithTargetId(pos, -1))
                .perform(ViewActions.click())
        }
    }

    fun validateSuccessLayout(fragment: Fragment?) {
        Espresso.onView(ViewMatchers.withId(R.id.toolbar))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.trending_title))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.app_name)))
        Espresso.onView(ViewMatchers.withId(R.id.container))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        MatcherAssert.assertThat(fragment, CoreMatchers.notNullValue())

        Espresso.onView(ViewMatchers.withId(R.id.shimmerLayout))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        Espresso.onView(ViewMatchers.withId(R.id.layout_error))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        Espresso.onView(ViewMatchers.withId(R.id.swipeContainer))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        Espresso.onView(ViewMatchers.withId(R.id.listView))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }
}