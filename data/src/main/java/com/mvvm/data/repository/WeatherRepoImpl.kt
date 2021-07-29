package com.mvvm.data.repository

import com.mvvm.data.remote.WeatherApi
import com.mvvm.domain.entity.wrapped.toResponseBody
import com.mvvm.domain.repository.WeatherRepo

class WeatherRepoImpl(private val weatherApi: WeatherApi) : WeatherRepo {

    override suspend fun weatherUpdate(location: String) = weatherApi.weatherUpdate(location).toResponseBody()

}

