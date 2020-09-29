package com.gojekgithub.trending.ui.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gojekgithub.trending.CoroutinesTestRule
import com.gojekgithub.trending.R
import com.gojekgithub.trending.data.model.GitRepositoryModel
import com.gojekgithub.trending.data.repo.TrendingRepository
import com.gojekgithub.trending.util.getOrAwaitValue
import com.gojekgithub.trending.utils.NetworkHelper
import com.gojekgithub.trending.constants.NetworkResponse
import com.gojekgithub.trending.constants.Status
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.io.InputStreamReader

class MainViewModelTest {

    private val trendingRepository = Mockito.mock(TrendingRepository::class.java)
    private val networkHelper = Mockito.mock(NetworkHelper::class.java)
    private lateinit var mainViewModel: MainViewModel
    private val gson = Gson()

    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Test
    fun `test verify error state network disconnected`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            Mockito.`when`(networkHelper.isNetworkConnected()).thenReturn(false)
            mainViewModel = MainViewModel(trendingRepository, networkHelper)
            val resource = mainViewModel.repos.value
            MatcherAssert.assertThat(mainViewModel.repos, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(resource!!.status, CoreMatchers.`is`(Status.Error))
            MatcherAssert.assertThat(resource!!.data, CoreMatchers.nullValue())
            Mockito.verify(trendingRepository, Mockito.times(0)).getRepositories()
        }

    @Test
    fun `test verify loading State`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        val result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )
        Mockito.`when`(networkHelper.isNetworkConnected()).thenReturn(true)
        val myFlow = flow {
            delay(2000)
            emit(NetworkResponse.Success(result))
        }
        Mockito.`when`(trendingRepository.getRepositories()).thenReturn(myFlow)
        mainViewModel = MainViewModel(trendingRepository, networkHelper)
        val resource = mainViewModel.repos.value
        MatcherAssert.assertThat(mainViewModel.repos, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(resource!!.status, CoreMatchers.`is`(Status.Loading))
        MatcherAssert.assertThat(resource!!.data, CoreMatchers.nullValue())

        myFlow.collect {
            val resource = mainViewModel.repos.value
            MatcherAssert.assertThat(mainViewModel.repos, CoreMatchers.notNullValue())
            MatcherAssert.assertThat(resource!!.status, CoreMatchers.`is`(Status.Success))
            MatcherAssert.assertThat(resource!!.data, CoreMatchers.`is`(result))
            Mockito.verify(trendingRepository, Mockito.times(1)).getRepositories()
        }
    }

    @Test
    fun `test verify network success`() = testDispatcher.runBlockingTest {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        val result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )
        Mockito.`when`(networkHelper.isNetworkConnected()).thenReturn(true)
        Mockito.`when`(trendingRepository.getRepositories()).thenReturn(flow {
            emit(NetworkResponse.Success(result))
        })
        mainViewModel = MainViewModel(trendingRepository, networkHelper)
        val resource = mainViewModel.repos.value
        MatcherAssert.assertThat(mainViewModel.repos, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(resource!!.status, CoreMatchers.`is`(Status.Success))
        MatcherAssert.assertThat(resource!!.data, CoreMatchers.`is`(result))
        Mockito.verify(trendingRepository, Mockito.times(1)).getRepositories()
    }

    @Test
    fun `test verify network success and null body`() = testDispatcher.runBlockingTest {
        Mockito.`when`(networkHelper.isNetworkConnected()).thenReturn(true)
        Mockito.`when`(trendingRepository.getRepositories()).thenReturn(flow {
            emit(NetworkResponse.Success(null))
        })
        mainViewModel = MainViewModel(trendingRepository, networkHelper)
        val resource = mainViewModel.repos.value
        MatcherAssert.assertThat(mainViewModel.repos, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(resource!!.status, CoreMatchers.`is`(Status.Success))
        MatcherAssert.assertThat(resource!!.data, CoreMatchers.nullValue())
        Mockito.verify(trendingRepository, Mockito.times(1)).getRepositories()
    }

    @Test
    fun `test verify network error Response`() = testDispatcher.runBlockingTest {
        Mockito.`when`(networkHelper.isNetworkConnected()).thenReturn(true)
        Mockito.`when`(trendingRepository.getRepositories()).thenReturn(flow {
            emit(NetworkResponse.Error(Exception("400 bad request")))
        })
        mainViewModel = MainViewModel(trendingRepository, networkHelper)
        val resource = mainViewModel.repos.value
        MatcherAssert.assertThat(mainViewModel.repos, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(resource!!.status, CoreMatchers.`is`(Status.Error))
        MatcherAssert.assertThat(resource!!.data, CoreMatchers.nullValue())
        MatcherAssert.assertThat(
            resource!!.message,
            CoreMatchers.`is`("java.lang.Exception: 400 bad request")
        )
        Mockito.verify(trendingRepository, Mockito.times(1)).getRepositories()
    }


    @Test
    fun `test verify sort by stars`() = testDispatcher.runBlockingTest {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        var result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )
        Mockito.`when`(networkHelper.isNetworkConnected()).thenReturn(true)
        Mockito.`when`(trendingRepository.getRepositories()).thenReturn(flow {
            emit(NetworkResponse.Success(result))
        })

        mainViewModel = MainViewModel(trendingRepository, networkHelper)
        mainViewModel.sortData(R.id.action_stars)

        result = result.sortedByDescending { repoModel ->
            repoModel.stars
        }
        val resource = mainViewModel.repos.getOrAwaitValue()
        MatcherAssert.assertThat(resource, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(resource.status, CoreMatchers.`is`(Status.Success))
        MatcherAssert.assertThat(resource.data, CoreMatchers.`is`(result))
    }


    @Test
    fun `test verify sort by name`()  = testDispatcher.runBlockingTest {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        var result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )
        Mockito.`when`(networkHelper.isNetworkConnected()).thenReturn(true)
        Mockito.`when`(trendingRepository.getRepositories()).thenReturn(flow {
            emit(NetworkResponse.Success(result))
        })

        mainViewModel = MainViewModel(trendingRepository, networkHelper)
        mainViewModel.sortData(R.id.action_name)

        result = result.sortedBy { repoModel ->
            repoModel.name
        }
        val resource = mainViewModel.repos.getOrAwaitValue()
        MatcherAssert.assertThat(resource, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(resource.status, CoreMatchers.`is`(Status.Success))
        MatcherAssert.assertThat(resource.data, CoreMatchers.`is`(result))
    }


}