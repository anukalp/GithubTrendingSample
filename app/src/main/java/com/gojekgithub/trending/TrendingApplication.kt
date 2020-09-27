package com.gojekgithub.trending

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector

class TrendingApplication : Application(), HasAndroidInjector {
    override fun androidInjector(): AndroidInjector<Any> {
        TODO("Not yet implemented")
    }

}