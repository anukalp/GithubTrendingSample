package com.gojekgithub.trending.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

/**
 * Custom Matcher used to match background colorRes [ColorInt] and color hexCode value
 * Espresso LifeCycle
 * 1. Interactors
 * 2. Matchers
 * 3. Action
 * 4. ViewAssertions
 */
class BackgroundColorMatcher private constructor(private val colorChecker: ColorChecker) :
    TypeSafeMatcher<View>(
        View::class.java
    ) {
    override fun matchesSafely(item: View): Boolean {
        val drawable = item.background as ColorDrawable
        return colorChecker.matches(drawable.color, item.context)
    }

    override fun describeTo(description: Description) {
        description.appendText("with text color: " + colorChecker.colorInt)
    }

    companion object {
        /**
         * Matches that the text has the expected color.
         *
         *
         *
         * Example usage:
         * `onView(withId(R.id.view)).check(matches(withTextColor(R.color.blue)));`
         */
        @CheckResult
        fun withDrawableRes(@ColorInt drawableRes: Int): Matcher<View> {
            return BackgroundColorMatcher(ColorChecker.from(drawableRes))
        }

        /**
         * Matches that the text has the expected color.
         *
         *
         *
         * Example usage:
         * `onView(withId(R.id.view)).check(matches(withTextColor("#0000ff")));`
         */
        @CheckResult
        fun withDrawableBackground(color: String): Matcher<View> {
            return withDrawableRes(Color.parseColor(color))
        }
    }
}