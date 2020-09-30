package com.gojekgithub.trending.di

import com.gojekgithub.trending.TrendingApplication
import com.gojekgithub.trending.ui.main.MainActivityUiTest
import com.gojekgithub.trending.ui.main.MainActivityUiClickTest
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [TrendingAppModule::class, TrendingApplicationModule::class,TestNetworkingModule::class, AndroidSupportInjectionModule::class])
interface TestApplicationComponent : TrendingApplicationComponent {
    override fun inject(application: TrendingApplication?)
    fun inject(baseUiClickTest: MainActivityUiClickTest)
    fun inject(baseTest: MainActivityUiTest)
}