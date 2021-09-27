package com.mvvm.extension

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import java.lang.Exception


fun setDefaultMap(mMap: GoogleMap?) {
    try {
        if (mMap != null) {
            val boundsIndia = LatLngBounds(LatLng(23.63936, 68.14712), LatLng(28.20453, 97.34466))
            val padding = 0 // offset from edges of the map in pixels
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(boundsIndia, padding)
            mMap.animateCamera(cameraUpdate)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun zoomRoute(googleMap: GoogleMap?, lstLatLngRoute: List<LatLng?>?) {
    try {
        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return
        val boundsBuilder = LatLngBounds.Builder()
        for (latLngPoint in lstLatLngRoute) boundsBuilder.include(latLngPoint)
        val routePadding = 100
        val latLngBounds = boundsBuilder.build()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding))
    } catch (e: Exception) {
    }
}
