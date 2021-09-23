package com.mvvm.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import com.google.android.gms.location.*
import timber.log.Timber
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mvvm.notifications.NotificationHelper
import com.mvvm.ui.Notification


class LocationService : Service() {

    companion object {
        //region data
        private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 3000
        private var mFusedLocationClient: FusedLocationProviderClient? = null
        private var locationRequest: LocationRequest? = null
        private val locationSettingsRequest: LocationSettingsRequest? = null

        private val CHANNEL_ID = "My Foreground Service"
        private val CHANNEL_NAME: String = Notification.TRACKING_SERVICE_NOTIFICATION
        private var notificationHelper: NotificationHelper? = null
        private var mNotificationBuilder: NotificationCompat.Builder? = null
        private const val FOREGROUND_SERVICE_ID = 1
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        buildNotification()
        startLocationUpdates()
        return START_STICKY
    }

    private fun buildNotification() {

        notificationHelper = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper(this, CHANNEL_ID, CHANNEL_NAME)
        } else {
            NotificationHelper(this)
        }

        mNotificationBuilder = notificationHelper!!.getTrackingNotification(this, CHANNEL_ID)
        startForeground(FOREGROUND_SERVICE_ID, mNotificationBuilder!!.build())
    }

    //onCreate
    override fun onCreate() {
        super.onCreate()
        initData()
    }

    private fun initData() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
    }

    //Location Callback
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val currentLocation: Location = locationResult.lastLocation
            Timber.d("Response : ${currentLocation.latitude} ${currentLocation.longitude}")
        }
    }

    private fun startLocationUpdates() {
        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback, Looper.myLooper()
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


}