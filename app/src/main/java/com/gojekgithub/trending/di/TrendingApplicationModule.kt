package com.gojekgithub.trending.di

import com.gojekgithub.trending.ui.main.TrendingActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class TrendingApplicationModule {
    @ContributesAndroidInjector(modules = [TrendingFragmentModule::class])
    abstract fun mainActivity(): TrendingActivity?
}
