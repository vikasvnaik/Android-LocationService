package com.mvvm.utils

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.mvvm.interfaces.UpdateLatLng
//import com.tecorb.hrmovecarmarkeranimation.Utils.Utilities
import java.lang.Exception


class MarkerAnimation(googleMap: GoogleMap?, duration: Long, updateLatLng: UpdateLatLng) {
    private val updateLatLng: UpdateLatLng = updateLatLng
    private var valueAnimator: ValueAnimator? = null
    private val googleMap: GoogleMap? = googleMap
    private val animationDuration: Long = duration
    fun animateMarker(destination: LatLng, marker: Marker?) {
        if (marker != null) {
            val startPosition = marker.position
            val endPosition = LatLng(destination.latitude, destination.longitude)
            if (valueAnimator != null) valueAnimator!!.end()
            val latLngInterpolator: Utilities.LatLngInterpolator =
                Utilities.LatLngInterpolator.LinearFixed()
            valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator?.duration = animationDuration // duration 1 second
            valueAnimator?.interpolator = LinearInterpolator()
            valueAnimator?.addUpdateListener(AnimatorUpdateListener { animation ->
                try {
                    val v = animation.animatedFraction
                    val newPosition: LatLng? =
                        latLngInterpolator.interpolate(v, startPosition, endPosition)
                    if (newPosition != null) {
                        marker.position = newPosition

                        marker.rotation = Utilities.computeRotation(
                            v, marker.rotation,
                            Utilities.bearingBetweenLocations(startPosition, newPosition).toFloat()
                        )
                        marker.setAnchor(0.5f, 0.5f)
                        marker.isFlat = true
                    }
                    //marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));


                    // add new location into old location
                    updateLatLng.onUpdatedLatLng(destination)

                    //when marker goes out from screen it automatically move into center
                    if (googleMap != null) {
                        if (!Utilities.isMarkerVisible(googleMap, newPosition)) {
                            googleMap.animateCamera(
                                CameraUpdateFactory
                                    .newCameraPosition(
                                        CameraPosition.Builder()
                                            .target(newPosition)
                                            .zoom(googleMap.cameraPosition.zoom)
                                            .build()
                                    )
                            )
                        } else {
                            try {
                                googleMap.animateCamera(
                                    CameraUpdateFactory
                                        .newCameraPosition(
                                            CameraPosition.Builder()
                                                .target(newPosition)
                                                .tilt(0f)
                                                .zoom(googleMap.cameraPosition.zoom)
                                                .build()
                                        )
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    // handle exception here
                }
            })
            valueAnimator?.start()
        }
    }

}
