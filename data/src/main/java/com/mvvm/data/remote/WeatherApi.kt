package com.mvvm.data.remote

import com.mvvm.domain.entity.response.WeatherUpdate
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

interface WeatherApi {
    @POST("/current?access_key=86502f661adbde8ae0a7ea108e0c5be6")
    suspend fun weatherUpdate(@Query("query") location: String): Response<WeatherUpdate>

    companion object {
        fun create(retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)
    }
}