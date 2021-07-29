package com.mvvm.domain.mapper

import com.mvvm.domain.entity.response.WeatherUpdate
import com.mvvm.domain.entity.wrapped.Response
import com.mvvm.domain.entity.wrapped.success
import com.mvvm.domain.repository.UserDataRepo

fun Response<WeatherUpdate>.storeUserInfo(userDataRepo: UserDataRepo): Response<WeatherUpdate> {
    return success {
        //userDataRepo.token = it.token
    }
}