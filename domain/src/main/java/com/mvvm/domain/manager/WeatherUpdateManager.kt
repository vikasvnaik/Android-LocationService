package com.mvvm.domain.manager

import com.mvvm.domain.repository.WeatherRepo

class WeatherUpdateManager(
    private val weatherRepo: WeatherRepo,
) : WeatherRepo {
    override suspend fun weatherUpdate(location: String) =
        weatherRepo.weatherUpdate(location)
}