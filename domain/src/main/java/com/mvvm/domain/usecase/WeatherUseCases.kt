package com.mvvm.domain.usecase

import com.mvvm.domain.entity.response.WeatherUpdate
import com.mvvm.domain.entity.wrapped.toResult
import com.mvvm.domain.manager.WeatherUpdateManager


class WeatherUpdateUseCase(private val weatherUpdateManager: WeatherUpdateManager) : UseCase<String, WeatherUpdate> {
    override suspend fun onExecute(parameter: String?) =
        weatherUpdateManager.weatherUpdate(parameter!!).toResult()
}
