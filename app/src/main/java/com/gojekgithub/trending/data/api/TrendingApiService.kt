package com.gojekgithub.trending.data.api

import com.gojekgithub.trending.data.model.GitRepositoryModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TrendingApiService {

    companion object {
        private const val SINCE = "daily"
        const val HEADER_FORCE_REMOTE = "custom_force_remote"
    }

    @GET("repositories")
    suspend fun getRepositories(
        @Header(HEADER_FORCE_REMOTE) forceRefresh : String = "false",
        @Query("language") language: String? = null,
        @Query("since") since: String? = SINCE,
        @Query("spoken_language_code") spoken_language_code: String? = null,
    ): Response<List<GitRepositoryModel>>

}