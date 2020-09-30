package com.gojekgithub.trending.data.repo

import com.gojekgithub.trending.R
import com.gojekgithub.trending.constants.FilterResponse
import com.gojekgithub.trending.constants.NetworkResponse
import com.gojekgithub.trending.data.api.TrendingApiService
import com.gojekgithub.trending.data.model.GitRepositoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TrendingRepository @Inject constructor(private val apiService: TrendingApiService) {

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun getRepositories(forcedRemote : Boolean = false) = flow {
        return@flow apiService.getRepositories("$forcedRemote").let {
            if (it.isSuccessful) {
                emit(NetworkResponse.Success(it.body()))
            } else {
                emit(NetworkResponse.Error(Exception(it.errorBody().toString())))
            }
        }
    }.flowOn(Dispatchers.IO)

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    fun filterRepos(itemId: Int, repoData: List<GitRepositoryModel>?) = flow {
        repoData?.let{
            when (itemId) {
                R.id.action_stars -> {
                    val data = arrayListOf<GitRepositoryModel>()
                    data.addAll(it)
                    data.sortByDescending { repoModel ->
                        repoModel.stars
                    }
                    emit(FilterResponse.Success(data))
                }
                R.id.action_name -> {
                    val data = arrayListOf<GitRepositoryModel>()
                    data.addAll(it)
                    data.sortBy { repoModel ->
                        repoModel.name
                    }
                    emit(FilterResponse.Success(data))
                }
                else -> emit(FilterResponse.Error(Exception("Item not supported $itemId")))
            }
        }?: emit(FilterResponse.Error(Exception("Null RepoData")))

    }.flowOn(Dispatchers.Default)

}