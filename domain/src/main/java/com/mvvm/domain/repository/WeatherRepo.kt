package com.mvvm.domain.repository

import com.mvvm.domain.entity.response.WeatherUpdate
import com.mvvm.domain.entity.wrapped.Response


interface WeatherRepo {
    suspend fun weatherUpdate(location: String): Response<WeatherUpdate>
}