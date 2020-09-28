package com.gojekgithub.trending.data.repo

import com.gojekgithub.trending.data.api.TrendingApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TrendingRepository @Inject constructor(private val apiService: TrendingApiService) {

    suspend fun getRepositories() = withContext(Dispatchers.IO) {
        apiService.getRepositories()
    }

}