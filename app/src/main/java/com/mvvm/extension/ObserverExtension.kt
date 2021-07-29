package com.mvvm.extension

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.mvvm.domain.entity.response.base.Failure
import com.mvvm.domain.entity.wrapped.*
import com.mvvm.ui.base.hideLoader
import com.mvvm.ui.base.showLoader
import com.mvvm.ui.base.snackBar

class EventObserver<T>(
    private val activity: AppCompatActivity? = null,
    private val loader: Boolean = true,
    private val notify: Boolean = true,
    private val success: (T) -> Unit
) : Observer<EventResult<T>> {
    override fun onChanged(event: EventResult<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            when (value) {
                is ResultLoading -> {
                    if (loader)
                        activity?.showLoader()
                }

                is ResultSuccess -> {
                    if (loader)
                        activity?.hideLoader()
                    success(value.data)
                }

                is ResultError -> {
                    if (loader)
                        activity?.hideLoader()
                    if (notify)
                        activity?.snackBar(value.error.message.toString())
                }
            }
        }
    }
}

class EventResultObserver<T>(
    private val loading: () -> Unit,
    private val success: (T) -> Unit,
    private val error: (Failure) -> Unit
) : Observer<EventResult<T>> {
    override fun onChanged(event: EventResult<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            when (value) {
                is ResultLoading -> {
                    loading()
                }

                is ResultSuccess -> {
                    success(value.data)
                }

                is ResultError -> {
                    error(value.error)
                }
            }
        }
    }
}

class EventUnWrapObserver<T>(private val success: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            success(value)
        }
    }
}