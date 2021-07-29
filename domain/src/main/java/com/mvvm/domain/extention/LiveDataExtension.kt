package com.mvvm.domain.extention

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import com.mvvm.domain.entity.response.base.Failure
import com.mvvm.domain.entity.wrapped.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

fun <T> LiveData<EventResult<T>>.unwrap(): Result<T>? {
    return this.value?.peekContent()
}

fun <T> Result<T>.unwrap(): T? {
    return success()
}

fun <T> Result<T>.fold() {
     when(this){
         is ResultSuccess -> this.data
         is ResultError -> this.error
     }
}

fun <T> LiveData<EventResult<T>>.eventSuccessValue(): T? {
    return this.value?.peekContent()?.success()
}

fun <T> LiveData<Event<T>>.eventValue(): T? {
    return this.value?.peekContent()
}

fun <T> Flow<Event<T>>.collectEvent(action: (value: T) -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        collect {
            it.getContentIfNotHandled()?.let {
                action(it)
            }
        }
    }
}

fun <T> Flow<EventResult<T>>.collectEventSuccess(action: (value: T) -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        collect {
            it.getContentIfNotHandled()?.let {
                it.success()?.let(action)
            }
        }
    }
}

fun <T> Flow<EventResult<T>>.collectEvent(
    loader: ((Boolean) -> Unit)? = null,
    success: ((T) -> Unit)? = null,
    error: ((Failure) -> Unit)? = null
) {
    CoroutineScope(Dispatchers.Main).launch {
        collect {
            it.mapInto(loader, success, error)
        }
    }
}

fun <T> EventResult<T>.mapInto(
    loader: ((Boolean) -> Unit)? = null,
    success: ((T) -> Unit)? = null,
    error: ((Failure) -> Unit)? = null
) {
    getContentIfNotHandled()?.let {
        when (it) {
            is ResultLoading -> {
                loader?.invoke(true)
            }
            is ResultSuccess -> {
                Timber.d(it.data.toString())
                success?.invoke(it.data)
            }
            is Result.Error -> {
                Timber.e(it.error.toString())
                error?.invoke(it.error)
            }
        }
    }
}

/**
 * Combine 2 LiveData into a Pair, emitting only when both sources are non-null
 */
fun <A, B> combine(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        fun combine() {
            val aValue = a.value
            val bValue = b.value
            if (aValue != null && bValue != null) {
                postValue(Pair(aValue, bValue))
            }
        }

        addSource(a) { combine() }
        addSource(b) { combine() }

        combine()
    }
}

/**
 * Combine 3 LiveData into a Triple, emitting only when all three sources are non-null
 */
fun <A, B, C> combine(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>): LiveData<Triple<A, B, C>> {
    return MediatorLiveData<Triple<A, B, C>>().apply {
        fun combine() {
            val aValue = a.value
            val bValue = b.value
            val cValue = c.value
            if (aValue != null && bValue != null && cValue != null) {
                postValue(Triple(aValue, bValue, cValue))
            }
        }

        addSource(a) { combine() }
        addSource(b) { combine() }
        addSource(c) { combine() }

        combine()
    }
}

/** Extension on LiveData to combine it with another LiveData. See #combine **/
fun <A, B> LiveData<A>.combineWith(other: LiveData<B>): LiveData<Pair<A, B>> =
    combine(this, other)

/** Extension on LiveData to combine it with another LiveData. See #combine **/
fun <A, B, C> LiveData<A>.combineWith(other: LiveData<B>, secondOther: LiveData<C>):
        LiveData<Triple<A, B, C>> =
    combine(this, other, secondOther)

fun <T> LiveData<T>.distinctUntilChanged(): LiveData<T> = distinctUntilChanged()

fun <T> Channel<T>.post(t: T) {
    ioScope.launch {
        send(t)
    }
}

fun <T> Flow<T>.toLiveData(scope: CoroutineScope = GlobalScope): LiveData<T> {
    return asLiveData(scope.coroutineContext)
}

fun <T> Flow<T>.collect(scope: CoroutineScope = GlobalScope, block: (T) -> Unit) {
    scope.launch {
        collect {
            block.invoke(it)
        }
    }
}

