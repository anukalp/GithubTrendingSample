package com.gojekgithub.trending.utils

import android.content.Context
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import dagger.internal.Preconditions

/**
 * Custom Matcher used to match text color [ColorInt] and color hexCode [@ColorRes] value
 * Espresso LifeCycle
 * 1. Interactors
 * 2. Matchers
 * 3. Action
 * 4. ViewAssertions
 */
internal class ColorChecker private constructor() {
    @ColorRes
    private var colorRes: Int? = null

    @ColorInt
    var colorInt: Int? = null
        private set

    fun matches(color: Int, context: Context?): Boolean {
        return if (colorInt != null) {
            color == colorInt
        } else color == Preconditions.checkNotNull(colorRes, "colorRes == null")?.let {
            ContextCompat.getColor(
                context!!,
                it
            )
        }
    }

    companion object {

        @CheckResult
        fun from(@ColorInt color: Int): ColorChecker {
            val matcher = ColorChecker()
            matcher.colorInt = color
            return matcher
        }
    }
}