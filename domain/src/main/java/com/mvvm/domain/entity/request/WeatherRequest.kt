package com.mvvm.domain.entity.request

data class WeatherRequest(
    var mobile: String,
    var otp: String,
    var deviceId: String
)
