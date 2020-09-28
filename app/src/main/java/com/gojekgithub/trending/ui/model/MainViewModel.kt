package com.gojekgithub.trending.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojekgithub.trending.data.model.GitRepositoryModel
import com.gojekgithub.trending.data.repo.TrendingRepository
import com.gojekgithub.trending.utils.NetworkHelper
import com.gojekgithub.trending.utils.Resource
import kotlinx.coroutines.launch

class MainViewModel constructor(
    private val mainRepository: TrendingRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val gitRepos = MutableLiveData<Resource<List<GitRepositoryModel>>>()
    val repos: LiveData<Resource<List<GitRepositoryModel>>>
        get() = gitRepos

    init {
        fetchGitRepos()
    }

    public fun fetchGitRepos() {
        viewModelScope.launch {
            gitRepos.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                mainRepository.getRepositories().let {
                    if (it.isSuccessful) {
                        gitRepos.postValue(Resource.success(it.body()))
                    } else gitRepos.postValue(Resource.error(it.errorBody().toString(), null))
                }
            } else gitRepos.postValue(Resource.error("No internet connection", null))
        }
    }
}