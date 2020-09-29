package com.gojekgithub.trending.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gojekgithub.trending.R
import com.gojekgithub.trending.data.model.GitRepositoryModel
import com.gojekgithub.trending.data.repo.TrendingRepository
import com.gojekgithub.trending.utils.NetworkHelper
import com.gojekgithub.trending.constants.NetworkResponse
import com.gojekgithub.trending.constants.Resource
import com.gojekgithub.trending.constants.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.TestOnly

class MainViewModel constructor(
    private val mainRepository: TrendingRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val gitRepos = MutableLiveData<Resource<List<GitRepositoryModel>>>()
    var repos: LiveData<Resource<List<GitRepositoryModel>>>
        get() = gitRepos
        @TestOnly
        set(value) {
            repos = value
        }

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
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (itemId) {
                    R.id.action_stars -> {
                        gitRepos.value!!.data?.let {
                            val data = arrayListOf<GitRepositoryModel>()
                            data.addAll(it)
                            data.sortByDescending { repoModel ->
                                repoModel.stars
                            }
                            gitRepos.postValue(Resource.success(data))
                        }
                    }
                    R.id.action_name -> {
                        gitRepos.value!!.data?.let {

                            val data = arrayListOf<GitRepositoryModel>()
                            data.addAll(it)
                            data.sortBy { repoModel ->
                                repoModel.name
                            }
                            gitRepos.postValue(Resource.success(data))
                        }
                    }
                }
            }
        }
    }

    fun fetchGitRepos() {
        gitRepos.postValue(Resource.loading(null))
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                mainRepository.getRepositories().onStart {
                    gitRepos.postValue(Resource.loading(null))
                }.catch { e -> gitRepos.postValue(Resource.error(e.toString(), null))
                }.collect {
                    when (it) {
                        is NetworkResponse.Success -> gitRepos.postValue(Resource.success(it.data))
                        is NetworkResponse.Error -> gitRepos.postValue(
                            Resource.error(
                                it.data.toString(),
                                null
                            )
                        )
                        else -> gitRepos.postValue(Resource.loading(null))
                    }
                }
            } else gitRepos.postValue(Resource.error(ERROR_MSG, null))
        }
    }

    companion object {
        const val ERROR_MSG = "No Internet Connection"
    }
}