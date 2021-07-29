package com.mvvm.domain.entity.response.base

import kotlinx.serialization.Serializable

@Serializable
data class Failure(
    var message: String = "",
    var statusCode: Int = 0
)

@Serializable
open class Success {
    var message: String = ""
    var statusCode: Int = 0
}