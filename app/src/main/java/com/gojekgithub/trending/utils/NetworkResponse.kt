package com.gojekgithub.trending.utils

import java.lang.Exception

sealed class NetworkResponse<out T> {
    data class Success<out T>(val data: T) : NetworkResponse<T>()
    data class Error(val data: Exception) : NetworkResponse<Nothing>()
    object Loading : NetworkResponse<Nothing>()
}

