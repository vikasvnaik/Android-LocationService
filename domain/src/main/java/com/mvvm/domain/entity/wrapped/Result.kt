package com.mvvm.domain.entity.wrapped

import com.mvvm.domain.entity.response.base.Failure


typealias ResultLoading = Result.Loading
typealias ResultSuccess<T> = Result.Success<T>
typealias ResultError = Result.Error

sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: Failure) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$error]"
            is Loading -> "Loading"
        }
    }
}

/**
 * `true` if [State] is of episodeType [Success] & holds non-null [Success.data].
 */
val Result<*>.succeeded
    get() = this is ResultSuccess && data != null


fun <T> Result<T>.success(): T? {
    return when (this) {
        is ResultSuccess -> this.data
        else -> null
    }
}

fun <T : Any> Result<T>.success(action: (T) -> Unit): Result<T> {
    if (this is ResultSuccess) data.let(action)
    return this
}


fun <T> Response<T>.toResult(): Result<T> {
    return when (this) {
        is ResponseSuccess -> {
            ResultSuccess(data)
        }
        is ResponseError -> {
            ResultError(error)
        }
    }
}