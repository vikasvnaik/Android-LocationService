package com.mvvm.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus


abstract class BaseVM : ViewModel() {
    private val channel = Channel<UserEvent>()
    val viewModelIOScope = viewModelScope.plus(Dispatchers.IO)

    abstract fun onAction(event: UserEvent)

    init {
        viewModelScope.launch {
            channel.consumeAsFlow().collect {
                onAction(it)
            }
        }
    }

    fun post(event: UserEvent) {
        viewModelScope.launch {
            channel.send(event)
        }
    }

    fun <T> Flow<T>.toLiveData() = asLiveData(viewModelIOScope.coroutineContext)

}