package com.mvvm.domain.extention

import kotlinx.coroutines.*
import timber.log.Timber

val ioDispatcher = Dispatchers.IO
val ioScope = CoroutineScope(ioDispatcher)

val mainDispatcher = Dispatchers.Main
val mainScope = CoroutineScope(mainDispatcher)

fun mainLaunch(block: () -> Unit) {
    mainScope.launch(CoroutineExceptionHandler { _, t ->
        Timber.e(t)
    }) {
        block()
    }
}

private var debounceScope: CoroutineScope? = null

fun debounceLaunch(debounceMilli: Long = 300L, block: () -> Unit) {
    debounceScope?.cancel()
    debounceScope = CoroutineScope(mainDispatcher)
    debounceScope?.launch {
        delay(debounceMilli)
        block()
    }
}

