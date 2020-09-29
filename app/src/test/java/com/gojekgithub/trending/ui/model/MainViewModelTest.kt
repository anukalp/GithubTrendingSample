package com.gojekgithub.trending.ui.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gojekgithub.trending.CoroutinesTestRule
import com.gojekgithub.trending.R
import com.gojekgithub.trending.constants.FilterResponse
import com.gojekgithub.trending.constants.NetworkResponse
import com.gojekgithub.trending.constants.Status
import com.gojekgithub.trending.data.model.GitRepositoryModel
import com.gojekgithub.trending.data.repo.TrendingRepository
import com.gojekgithub.trending.util.getOrAwaitValue
import com.gojekgithub.trending.utils.NetworkHelper
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
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import java.io.InputStreamReader

class MainViewModelTest {

    private val trendingRepository = Mockito.mock(TrendingRepository::class.java)
    private val networkHelper = Mockito.mock(NetworkHelper::class.java)
    private lateinit var mainViewModel: MainViewModel
    private val gson = Gson()

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
    fun `test verify loading state`() = coroutinesTestRule.testDispatcher.runBlockingTest {
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
    fun `test verify network success`() = coroutinesTestRule.testDispatcher.runBlockingTest {
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
    fun `test verify network success and null body`() = coroutinesTestRule.testDispatcher.runBlockingTest {
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
    fun `test verify repo throws exception`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        Mockito.`when`(networkHelper.isNetworkConnected()).thenReturn(true)
        Mockito.`when`(trendingRepository.getRepositories()).thenReturn(flow {
            throw Exception("Error in repo!")
        })
        mainViewModel = MainViewModel(trendingRepository, networkHelper)
        val resource = mainViewModel.repos.value
        MatcherAssert.assertThat(mainViewModel.repos, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(resource!!.status, CoreMatchers.`is`(Status.Error))
        MatcherAssert.assertThat(resource!!.data, CoreMatchers.nullValue())
        MatcherAssert.assertThat(
            resource!!.message,
            CoreMatchers.`is`("java.lang.Exception: Error in repo!")
        )
        Mockito.verify(trendingRepository, Mockito.times(1)).getRepositories()
    }

    @Test
    fun `test verify network error response`() = coroutinesTestRule.testDispatcher.runBlockingTest {
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
    fun `test verify sort when not success`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        Mockito.`when`(networkHelper.isNetworkConnected()).thenReturn(true)
        val testFlow = flow {
            emit(NetworkResponse.Error(Exception("400 bad request")))
        }
        Mockito.`when`(trendingRepository.getRepositories()).thenReturn(testFlow)

        mainViewModel = MainViewModel(trendingRepository, networkHelper)

        testFlow.collect {
            mainViewModel.sortData(R.id.action_name)
            Mockito.verify(trendingRepository, Mockito.never()).filterRepos(anyInt(), any())
        }
    }

    @Test
    fun `test verify sort when success`() = coroutinesTestRule.testDispatcher.runBlockingTest {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        val result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )
        Mockito.`when`(networkHelper.isNetworkConnected()).thenReturn(true)
        val testFlow = flow {
            emit(NetworkResponse.Success(result))
        }
        Mockito.`when`(trendingRepository.getRepositories()).thenReturn(testFlow)

        mainViewModel = MainViewModel(trendingRepository, networkHelper)

        val data = arrayListOf<GitRepositoryModel>()
        data.addAll(result)
        data.sortByDescending { repoModel ->
            repoModel.stars
        }
        Mockito.`when`(
            trendingRepository.filterRepos(
                R.id.action_name,
                mainViewModel.repos.value!!.data
            )
        ).thenReturn(
            flow {
                emit(FilterResponse.Success(data))
            })
        mainViewModel.sortData(R.id.action_name)
        Mockito.verify(trendingRepository, Mockito.times(1)).filterRepos(anyInt(), any())

        val resource = mainViewModel.repos.getOrAwaitValue()
        MatcherAssert.assertThat(mainViewModel.repos, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(resource!!.status, CoreMatchers.`is`(Status.Success))
        MatcherAssert.assertThat(resource!!.data, CoreMatchers.`is`(data))
    }


}