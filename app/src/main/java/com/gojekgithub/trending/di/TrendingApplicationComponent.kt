package com.gojekgithub.trending.di

import com.gojekgithub.trending.TrendingApplication
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [TrendingAppModule::class, TrendingApplicationModule::class, AndroidSupportInjectionModule::class])
interface TrendingApplicationComponent {
    fun inject(application: TrendingApplication?)
}