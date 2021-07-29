package com.mvvm.vm.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mvvm.domain.usecase.WeatherUpdateUseCase
import com.mvvm.vm.BaseVM
import com.mvvm.vm.OnWeatherUpdateEvent
import com.mvvm.vm.UserEvent

class DashboardVM(
    private val weatherUpdateUseCase: WeatherUpdateUseCase
) : BaseVM() {

    override fun onAction(event: UserEvent) {
        when (event) {
            is OnWeatherUpdateEvent.UpdateWeather -> _weather.value = event.location
        }
    }


    private val _weather = MutableLiveData<String>()
    val weatherLiveData = Transformations.switchMap(_weather) {
        weatherUpdateUseCase.execute(viewModelIOScope,it).toLiveData()
    }
}