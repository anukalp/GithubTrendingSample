package com.gojekgithub.trending.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojekgithub.trending.R
import com.gojekgithub.trending.data.model.GitRepositoryModel
import com.gojekgithub.trending.data.repo.TrendingRepository
import com.gojekgithub.trending.utils.NetworkHelper
import com.gojekgithub.trending.utils.Resource
import com.gojekgithub.trending.utils.Status
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

    fun sortData(itemId: Int) {
        if (gitRepos.value == null) {
            return
        }
        if (gitRepos.value!!.status != Status.Success) {
            return
        }
        when (itemId) {
            R.id.action_stars -> {
                gitRepos.value!!.data?.let {
                    val data = arrayListOf<GitRepositoryModel>()
                    data.addAll(it)
                    data.sortByDescending { repoModel ->
                        repoModel.stars
                    }
                    gitRepos.value = Resource.success(data)
                }
            }
            R.id.action_name -> {
                gitRepos.value!!.data?.let {
                    val data = arrayListOf<GitRepositoryModel>()
                    data.addAll(it)
                    data.sortBy { repoModel ->
                        repoModel.name
                    }
                    gitRepos.value = Resource.success(data)
                }
            }
        }
    }

    fun fetchGitRepos() {
        viewModelScope.launch {
            gitRepos.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                mainRepository.getRepositories().let {
                    if (it.isSuccessful) {
                        gitRepos.postValue(Resource.success(it.body()))
                    } else gitRepos.postValue(Resource.error(it.errorBody().toString(), null))
                }
            } else gitRepos.postValue(Resource.error(ERROR_MSG, null))
        }
    }

    companion object {
        private const val ERROR_MSG = "No Internet Connection"
    }
}