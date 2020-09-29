package com.gojekgithub.trending.constants

sealed class NetworkResponse<out T> {
    data class Success<out T>(val data: T) : NetworkResponse<T>()
    data class Error(val data: Exception) : NetworkResponse<Nothing>()
}

