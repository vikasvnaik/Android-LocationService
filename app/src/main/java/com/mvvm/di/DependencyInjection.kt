package com.mvvm.di


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import coil.ImageLoaderBuilder
import coil.util.Logger
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.mvvm.BuildConfig
import com.mvvm.data.remote.WeatherApi
import com.mvvm.data.remote.RetrofitManager
import com.mvvm.data.repository.WeatherRepoImpl
import com.mvvm.domain.entity.wrapped.Event
import com.mvvm.domain.manager.WeatherUpdateManager
import com.mvvm.domain.repository.WeatherRepo
import com.mvvm.domain.usecase.WeatherUpdateUseCase
import com.mvvm.extension.P
import com.mvvm.vm.SharedVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

import com.mvvm.vm.dashboard.DashboardVM
import kotlinx.serialization.json.Json
import timber.log.Timber

fun dependency() = listOf(vm, repository, manager, service, useCases, singleInstance)
val vm = module {
    viewModel { DashboardVM(get()) }
    single { SharedVM() }
}
val useCases = module {
    factory { WeatherUpdateUseCase(get()) }
}
val manager = module {
    single { WeatherUpdateManager(get()) }
}
val repository = module {
    single { WeatherRepoImpl(get()) as WeatherRepo }
}
val service = module {
    single { WeatherApi.create(get()) }
}

val singleInstance = module {
    single {
        ImageLoaderBuilder(get()).apply {
            availableMemoryPercentage(0.25)
            crossfade(true)
            logger(object : Logger {
                override var level: Int
                    get() = 1
                    set(value) {
                    }

                override fun log(
                    tag: String,
                    priority: Int,
                    message: String?,
                    throwable: Throwable?
                ) {
                    Timber.d(message)
                }

            })
        }.build()
    }
    single {
        val manager = RetrofitManager(get())
        manager.retrofit(BuildConfig.BASE_URL)
        manager
    }
    single { get<RetrofitManager>().retrofit(BuildConfig.BASE_URL) }
    single { P.customPrefs(get(), BuildConfig.APPLICATION_ID) }
    single {
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        }
    }
    single { AppUpdateManagerFactory.create(get()) }
    single { MutableLiveData<Event<Bundle>>() }
    single {
        val notificationManager: NotificationManager =
            get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel_IPLT"
            val descriptionText = "Channel_IPLT_Desc"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Channel_IPLT", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system

            notificationManager.createNotificationChannel(channel)
        }
        notificationManager
    }
}
