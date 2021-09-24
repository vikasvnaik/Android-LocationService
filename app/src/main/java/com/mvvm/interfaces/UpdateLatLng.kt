package com.mvvm.interfaces

import com.google.android.gms.maps.model.LatLng

interface UpdateLatLng {
    fun onUpdatedLatLng(updatedLatLng: LatLng?)
}