package com.mvvm.domain.entity.response

import kotlinx.serialization.Serializable

/*****************/
@Serializable
data class WeatherUpdate(
    val request: Request,
    val location: Location,
    val current: Current
)

@Serializable
data class Request (

    val type : String,
    val query : String,
    val language : String,
    val unit : String
)

@Serializable
data class Location (

    val name : String,
    val country : String,
    val region : String,
    val lat : Double,
    val lon : Double,
    val timezone_id : String,
    val localtime : String,
    val localtime_epoch : Int,
    val utc_offset : Double
)

@Serializable
data class Current (

    val observation_time : String,
    val temperature : Int,
    val weather_code : Int,
    val weather_icons : List<String>,
    val weather_descriptions : List<String>,
    val wind_speed : Int,
    val wind_degree : Int,
    val wind_dir : String,
    val pressure : Int,
    val precip : Double,
    val humidity : Int,
    val cloudcover : Int,
    val feelslike : Int,
    val uv_index : Int,
    val visibility : Int,
    val is_day : String
)