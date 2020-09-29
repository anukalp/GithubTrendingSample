package com.gojekgithub.trending.constants

import java.lang.Exception

sealed class FilterResponse<out T> {
    data class Success<out T>(val data: T) : FilterResponse<T>()
    data class Error(val data: Exception) : FilterResponse<Nothing>()
}

