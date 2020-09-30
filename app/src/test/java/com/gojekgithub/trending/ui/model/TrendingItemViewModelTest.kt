package com.gojekgithub.trending.ui.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.gojekgithub.trending.CoroutinesTestRule
import com.gojekgithub.trending.data.model.GitRepositoryModel
import com.gojekgithub.trending.util.getOrAwaitValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import java.io.InputStreamReader
import java.text.NumberFormat
import java.util.*

class TrendingItemViewModelTest {

    private lateinit var viewModel: TrendingItemViewModel
    private val gson = Gson()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Test
    fun `test verify live Data`() {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        var result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )

        var data = result[0]
        data.expanded = false

        viewModel = TrendingItemViewModel(data)

        val languageColor = viewModel.getLanguageColor().getOrAwaitValue()
        val stars = viewModel.getStars().getOrAwaitValue()
        val forks = viewModel.getForks().getOrAwaitValue()
        val imageUrl = viewModel.getImageUrl().getOrAwaitValue()
        val description = viewModel.getDescription().getOrAwaitValue()
        val language = viewModel.getLanguage().getOrAwaitValue()
        val title = viewModel.getTitle().getOrAwaitValue()
        val author = viewModel.getAuthor().getOrAwaitValue()
        val expanded = viewModel.getExpanded().getOrAwaitValue()

        data?.also {
            MatcherAssert.assertThat(languageColor, CoreMatchers.`is`(it.languageColor))
            MatcherAssert.assertThat(imageUrl, CoreMatchers.`is`(it.avatar))
            MatcherAssert.assertThat(language, CoreMatchers.`is`(it.language))
            MatcherAssert.assertThat(title, CoreMatchers.`is`(it.name))
            MatcherAssert.assertThat(author, CoreMatchers.`is`(it.author))
            MatcherAssert.assertThat(expanded, CoreMatchers.`is`(it.expanded))
            MatcherAssert.assertThat(stars, CoreMatchers.`is`(NumberFormat.getNumberInstance(Locale.US).format(it.stars)))
            MatcherAssert.assertThat(forks, CoreMatchers.`is`(NumberFormat.getNumberInstance(Locale.US).format(it.forks)))
            MatcherAssert.assertThat(description, CoreMatchers.`is`("${it.description}(${it.url})"))
        }
    }

    @Test
    fun `test verify expanded Data`() {
        val reader: InputStreamReader =
            javaClass.classLoader.getResourceAsStream("api-response/repos-git.json").reader()
        var result: List<GitRepositoryModel> = gson.fromJson(
            reader,
            object : TypeToken<List<GitRepositoryModel?>?>() {}.type
        )


        var data = result[1]

        viewModel = TrendingItemViewModel(data)

        val languageColor = viewModel.getLanguageColor().getOrAwaitValue()
        val stars = viewModel.getStars().getOrAwaitValue()
        val forks = viewModel.getForks().getOrAwaitValue()
        val imageUrl = viewModel.getImageUrl().getOrAwaitValue()
        val description = viewModel.getDescription().getOrAwaitValue()
        val language = viewModel.getLanguage().getOrAwaitValue()
        val title = viewModel.getTitle().getOrAwaitValue()
        val author = viewModel.getAuthor().getOrAwaitValue()
        var expanded = viewModel.getExpanded().getOrAwaitValue()

        data?.also {
            MatcherAssert.assertThat(languageColor, CoreMatchers.`is`(it.languageColor))
            MatcherAssert.assertThat(imageUrl, CoreMatchers.`is`(it.avatar))
            MatcherAssert.assertThat(language, CoreMatchers.`is`(it.language))
            MatcherAssert.assertThat(title, CoreMatchers.`is`(it.name))
            MatcherAssert.assertThat(author, CoreMatchers.`is`(it.author))
            MatcherAssert.assertThat(expanded, CoreMatchers.`is`(false))
            MatcherAssert.assertThat(stars, CoreMatchers.`is`(NumberFormat.getNumberInstance(Locale.US).format(it.stars)))
            MatcherAssert.assertThat(forks, CoreMatchers.`is`(NumberFormat.getNumberInstance(Locale.US).format(it.forks)))
            MatcherAssert.assertThat(description, CoreMatchers.`is`("${it.description}(${it.url})"))
        }

        viewModel.setExpanded()
        expanded = viewModel.getExpanded().getOrAwaitValue()
        MatcherAssert.assertThat(expanded, CoreMatchers.`is`(true))
        MatcherAssert.assertThat(data.expanded, CoreMatchers.`is`(true))

    }


}