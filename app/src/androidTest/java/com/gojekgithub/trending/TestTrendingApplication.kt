package com.gojekgithub.trending

import com.gojekgithub.trending.di.DaggerTestApplicationComponent
import com.gojekgithub.trending.di.TestApplicationComponent
import com.gojekgithub.trending.di.TrendingAppModule
import com.gojekgithub.trending.ui.main.MainActivityTest

class TestTrendingApplication : TrendingApplication() {

    override fun initApplicationComponents() {
        daggerAppComponent = DaggerTestApplicationComponent.builder()
            .trendingAppModule(TrendingAppModule(this@TestTrendingApplication)).build()
        daggerAppComponent.inject(this)
    }

    fun inject(baseTest: MainActivityTest) {
        injectIfNecessary()
        (daggerAppComponent as TestApplicationComponent).inject(baseTest)
    }
}