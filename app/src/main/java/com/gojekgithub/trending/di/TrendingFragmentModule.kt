package com.gojekgithub.trending.di

import com.gojekgithub.trending.ui.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
@Module
abstract class TrendingFragmentModule {

    @ContributesAndroidInjector(modules = [TrendingViewModelModule::class])
    abstract fun mainFragment(): MainFragment
}
