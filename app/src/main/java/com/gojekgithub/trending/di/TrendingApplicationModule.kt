package com.gojekgithub.trending.di

import com.gojekgithub.trending.TrendingActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class TrendingApplicationModule {
    @ContributesAndroidInjector
    abstract fun testMainActivity(): TrendingActivity?
}
