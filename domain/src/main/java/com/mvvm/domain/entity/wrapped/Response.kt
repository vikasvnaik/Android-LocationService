package com.mvvm.domain.entity.wrapped

import com.mvvm.domain.entity.response.base.Failure
import com.mvvm.domain.extention.G


typealias ResponseSuccess<T> = Response.Success<T>
typealias ResponseError = Response.Error

sealed class Response<out T> {

    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val error: Failure) : Response<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$error]"
        }
    }
}

/**
 * `true` if [State] is of episodeType [Success] & holds non-null [Success.data].
 */
val Response<*>.succeeded
    get() = this is ResponseSuccess && data != null


fun <T> Response<T>.success(): T? {
    return when (this) {
        is ResponseSuccess -> this.data
        else -> null
    }
}

fun <T : Any> Response<T>.success(action: (T) -> Unit): Response<T> {
    if (this is ResponseSuccess) data.let(action)
    return this
}

fun <T> retrofit2.Response<T>.toResponseBody(): Response<T> {
    return if (isSuccessful) {
        body()?.let {
            ResponseSuccess(it)
        } ?: ResponseError(Failure("Something went wrong", code()))
    } else {
        val failure = G.json.decodeFromString(Failure.serializer(), errorBody()?.string()!!)
        ResponseError(failure)
    }
}