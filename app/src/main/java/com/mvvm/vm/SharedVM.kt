package com.mvvm.vm

class SharedVM : BaseVM() {
    override fun onAction(event: UserEvent) {

    }

    var weather: OnWeatherUpdateEvent.UpdateWeather? = null

    var materialId : Int? = null



}