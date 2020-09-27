package com.gojekgithub.trending.data.repo

import com.gojekgithub.trending.data.api.TrendingApiService
import javax.inject.Inject

class TrendingRepository @Inject constructor(private val apiService: TrendingApiService) {

    suspend fun getRepositories() = apiService.getRepositories()

}