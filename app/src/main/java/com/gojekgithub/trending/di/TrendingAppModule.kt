package com.gojekgithub.trending.di

import android.content.Context
import com.gojekgithub.trending.TrendingApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TrendingAppModule(private val appContext: TrendingApplication) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return appContext
    }

}