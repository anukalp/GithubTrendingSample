package com.gojekgithub.trending.data.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gojekgithub.trending.CoroutinesTestRule
import com.gojekgithub.trending.R
import com.gojekgithub.trending.constants.FilterResponse
import com.gojekgithub.trending.constants.NetworkResponse
import com.gojekgithub.trending.data.api.TrendingApiService
import com.gojekgithub.trending.data.model.GitRepositoryModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response
import java.io.InputStreamReader

class TrendingRepoTest {
    private val trendingApiService = Mockito.mock(TrendingApiService::class.java)
    private lateinit var trendingRepository: TrendingRepository

    private val gson = Gson()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Before
    fun setup() {
        trendingRepository = TrendingRepository(trendingApiService)
    }

    @Test
    fun `test repo with api service success`() =  runBlocking {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        val result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )
        Mockito.`when`(trendingApiService.getRepositories()).thenReturn(Response.success(result))
        trendingRepository.getRepositories().collect {
            MatcherAssert.assertThat(it is NetworkResponse.Success, CoreMatchers.`is`(true))
            val successResponse = it as NetworkResponse.Success
            MatcherAssert.assertThat(successResponse.data, CoreMatchers.`is`(result))
            Mockito.verify(trendingApiService, Mockito.times(1)).getRepositories()
        }
    }


    internal class NoContentResponseBody :
        ResponseBody() {
        override fun contentLength(): Long {
            return 0
        }

        override fun contentType(): MediaType? {
            return null
        }

        override fun source(): BufferedSource {
            throw IllegalStateException("Cannot read raw response body of a converted body.")
        }
    }

    @Test
    fun `test repo with api service failure`() =  runBlocking {
        Mockito.`when`(trendingApiService.getRepositories()).thenReturn(
            Response.error(
                404,
                NoContentResponseBody()
            )
        )
        trendingRepository.getRepositories().collect {
            MatcherAssert.assertThat(it is NetworkResponse.Error, CoreMatchers.`is`(true))
            Mockito.verify(trendingApiService, Mockito.times(1)).getRepositories()
        }
    }

    @Test
    fun `test filter repos with null data`() = runBlocking {
        trendingRepository.filterRepos(R.id.action_stars, null).collect {
            MatcherAssert.assertThat(it is FilterResponse.Error, CoreMatchers.`is`(true))
            val errorResponse = it as FilterResponse.Error
            MatcherAssert.assertThat(errorResponse.data.message, CoreMatchers.`is`("Null RepoData"))
        }
    }

    @Test
    fun `test filter repos with action_stars`() = runBlocking {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        val result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )

        val data = arrayListOf<GitRepositoryModel>()
        data.addAll(result)
        data.sortByDescending { repoModel ->
            repoModel.stars
        }

        trendingRepository.filterRepos(R.id.action_stars, result).collect {
            MatcherAssert.assertThat(it is FilterResponse.Success, CoreMatchers.`is`(true))
            val successResponse = it as FilterResponse.Success
            MatcherAssert.assertThat(successResponse.data, CoreMatchers.`is`(data))
        }
    }

    @Test
    fun `test filter repos with action_name`() = runBlocking {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        val result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )

        val data = arrayListOf<GitRepositoryModel>()
        data.addAll(result)
        data.sortBy { repoModel ->
            repoModel.name
        }

        trendingRepository.filterRepos(R.id.action_name, result).collect {
            MatcherAssert.assertThat(it is FilterResponse.Success, CoreMatchers.`is`(true))
            val successResponse = it as FilterResponse.Success
            MatcherAssert.assertThat(successResponse.data, CoreMatchers.`is`(data))
        }
    }

    @Test
    fun `test filter repos with action_-1`() = runBlocking {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        val result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )

        trendingRepository.filterRepos(-1, result).collect {
            MatcherAssert.assertThat(it is FilterResponse.Error, CoreMatchers.`is`(true))
            val errorResponse = it as FilterResponse.Error
            MatcherAssert.assertThat(errorResponse.data.message, CoreMatchers.`is`("Item not supported -1"))
        }
    }


}