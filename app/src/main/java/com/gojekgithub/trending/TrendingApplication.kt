package com.gojekgithub.trending

import android.app.Application
import com.gojekgithub.trending.di.DaggerTrendingApplicationComponent
import com.gojekgithub.trending.di.TrendingAppModule
import com.gojekgithub.trending.di.TrendingApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class TrendingApplication : Application(), HasAndroidInjector {

    @JvmField
    @Inject
    @Volatile
    var appDispatchingAndroidInjector: DispatchingAndroidInjector<Any>? = null

    private lateinit var daggerAppComponent: TrendingApplicationComponent

    /**
     * Lazily injects the [DaggerTrendingApplicationComponent]'s members. Injection cannot be performed in [ ][Application.onCreate] since [android.content.ContentProvider]s' [ ][android.content.ContentProvider.onCreate] method will be called first and might
     * need injected members on the application. Injection is not performed in the constructor, as
     * that may result in members-injection methods being called before the constructor has completed,
     * allowing for a partially-constructed instance to escape.
     */
    private fun injectIfNecessary() {
        if (appDispatchingAndroidInjector == null) {
            synchronized(this) {
                if (appDispatchingAndroidInjector == null) {
                    initApplicationComponents()
                    checkNotNull(appDispatchingAndroidInjector) {
                        ("The AndroidInjector returned from applicationInjector() did not inject the "
                                + "DaggerApplication")
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        injectIfNecessary()
    }

    override fun androidInjector(): AndroidInjector<Any> {
        injectIfNecessary()
        return appDispatchingAndroidInjector!!
    }

    private fun initApplicationComponents() {
        val time = System.currentTimeMillis()
        daggerAppComponent = DaggerTrendingApplicationComponent.builder()
            .trendingAppModule(TrendingAppModule(this@TrendingApplication)).build()
        daggerAppComponent?.inject(this)
    }
}