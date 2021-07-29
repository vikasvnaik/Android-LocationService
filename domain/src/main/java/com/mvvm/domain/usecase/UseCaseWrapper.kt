package com.mvvm.domain.usecase

import com.mvvm.domain.entity.response.base.Failure
import com.mvvm.domain.entity.wrapped.*
import com.mvvm.domain.entity.wrapped.EventResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.cert.CertificateException
import java.util.concurrent.TimeoutException


interface UseCase<P, R> : BaseUseCase<P, R> {
    override suspend fun onExecute(parameter: P?): Result<R>
}

interface BaseUseCase<P, R> {

    suspend fun onExecute(parameter: P?): Result<R>
    fun execute(
        scope: CoroutineScope,
        parameter: P? = null
    ): Flow<EventResult<R>> {
        return flow {
            emit(Event(ResultLoading))
            emit(Event(onExecute(parameter)))
        }.catch { e ->
            Timber.e(e)
            emit(Event(ResultError(Failure(e.getHTTPError()))))
        }
    }

    suspend fun executes(parameter: P? = null): Result<R> {
        return try {
            onExecute(parameter)
        } catch (e: Exception) {
            ResultError(Failure(e.getHTTPError()))
        }

    }

    private fun Throwable.getHTTPError(): String {
        return if (javaClass.name.contains(UnknownHostException::class.java.name)) {
            "Check your internet connection and try again"
        } else if (javaClass.name.contains(TimeoutException::class.java.name)
            || javaClass.name.contains(SocketTimeoutException::class.java.name)
            || javaClass.name.contains(ConnectException::class.java.name)
        ) {
            "Something went wrong"
        } else if (javaClass.name.contains(CertificateException::class.java.name)) {
            "Something went wrong"
        } else {
            toString()
        }
    }
}