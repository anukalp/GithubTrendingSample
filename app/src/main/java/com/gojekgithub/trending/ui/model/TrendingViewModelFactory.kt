package com.gojekgithub.trending.ui.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.gojekgithub.trending.data.repo.TrendingRepository
import com.gojekgithub.trending.utils.NetworkHelper

class TrendingViewModelFactory(
    context: Context, private val repository: TrendingRepository, private val networkHelper: NetworkHelper) : AndroidViewModelFactory((context as Application)) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == MainViewModel::class.java) {
            return MainViewModel(repository) as T
        }
        return super.create(modelClass)
    }
}