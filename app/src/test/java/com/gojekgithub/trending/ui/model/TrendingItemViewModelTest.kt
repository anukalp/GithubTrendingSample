package com.gojekgithub.trending.ui.model

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gojekgithub.trending.CoroutinesTestRule
import com.gojekgithub.trending.data.repo.TrendingRepository
import com.gojekgithub.trending.utils.NetworkHelper
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class TrendingItemViewModelTest {

    private val context = Mockito.mock(Application::class.java)
    private val trendingRepository = Mockito.mock(TrendingRepository::class.java)
    private val networkHelper = Mockito.mock(NetworkHelper::class.java)
    private lateinit var viewModel: TrendingViewModelFactory

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Before
    fun setup() {
        viewModel = TrendingViewModelFactory(context, trendingRepository, networkHelper)
    }

    @Test
    fun `test main view model`() {
        MatcherAssert.assertThat(viewModel.create(MainViewModel::class.java), CoreMatchers.notNullValue())
    }

    @Test(expected = RuntimeException::class)
    fun `test null view model`() {
        MatcherAssert.assertThat(viewModel.create(TrendingItemViewModel::class.java), CoreMatchers.nullValue())
    }


}