package com.gojekgithub.trending.utils

import androidx.annotation.IntDef

@IntDef(
    Status.Success,
    Status.Error,
    Status.Loading,
)
annotation class Status {
    companion object {
        const val Success = 0
        const val Error = 1
        const val Loading = 2
    }
}