package com.gojekgithub.trending.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojekgithub.trending.data.model.GitRepositoryModel
import java.text.NumberFormat
import java.util.*

class TrendingItemViewModel constructor(
    private val trendingRepoData: GitRepositoryModel
) : ViewModel() {
    private val author: MutableLiveData<String> = MutableLiveData()
    private val title: MutableLiveData<String> = MutableLiveData()
    private val imageUrl: MutableLiveData<String> = MutableLiveData()
    private val description: MutableLiveData<String> = MutableLiveData()
    private val language: MutableLiveData<String> = MutableLiveData()
    private val languageColor: MutableLiveData<String> = MutableLiveData()
    private val stars: MutableLiveData<String> = MutableLiveData()
    private val forks: MutableLiveData<String> = MutableLiveData()
    private val expanded: MutableLiveData<Boolean> = MutableLiveData()

    init {
        trendingRepoData?.also {
            author.value = it.author
            title.value = it.name
            imageUrl.value = it.avatar
            description.value = "${it.description}(${it.url})"
            language.value = it.language
            languageColor.value = it.languageColor
            stars.value = NumberFormat.getNumberInstance(Locale.US).format(it.stars)
            forks.value = NumberFormat.getNumberInstance(Locale.US).format(it.forks)
            expanded.value = it.expanded?: false
        }
    }

    fun getLanguageColor(): LiveData<String> {
        return languageColor
    }

    fun getStars(): LiveData<String> {
        return stars
    }

    fun getForks(): LiveData<String> {
        return forks
    }

    fun getImageUrl(): LiveData<String> {
        return imageUrl
    }

    fun getDescription(): LiveData<String> {
        return description
    }

    fun getLanguage(): LiveData<String> {
        return language
    }

    fun getTitle(): LiveData<String> {
        return title
    }

    fun getAuthor(): LiveData<String> {
        return author
    }

    fun getExpanded(): LiveData<Boolean> {
        return expanded
    }

    fun setExpanded() {
        val expandedValue = !(this.expanded.value ?: false)
        trendingRepoData.expanded = expandedValue
        this.expanded.value = expandedValue
    }
}