package com.gojekgithub.trending.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.gojekgithub.trending.data.repo.TrendingRepository
import com.gojekgithub.trending.ui.main.MainFragment
import com.gojekgithub.trending.ui.model.MainViewModel
import com.gojekgithub.trending.ui.model.TrendingViewModelFactory
import com.gojekgithub.trending.utils.NetworkHelper
import dagger.Module
import dagger.Provides

@Module
class TrendingViewModelModule {

    @Provides
    fun provideViewModelFactory(
        context: Context, trendingRepository: TrendingRepository, networkHelper: NetworkHelper,
    ): TrendingViewModelFactory {
        return TrendingViewModelFactory(
            context,
            trendingRepository,
            networkHelper
        )
    }

    @Provides
    fun provideMainViewModel(fragment: MainFragment, factory: TrendingViewModelFactory): MainViewModel {
        return ViewModelProvider(fragment, factory).get(MainViewModel::class.java)
    }
}

