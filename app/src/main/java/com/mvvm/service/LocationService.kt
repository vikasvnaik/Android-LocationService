package com.mvvm.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import com.google.android.gms.location.*
import timber.log.Timber
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mvvm.domain.entity.request.WeatherRequest
import com.mvvm.notifications.NotificationHelper
import com.mvvm.ui.Notification
import com.mvvm.domain.entity.wrapped.Event
import org.koin.android.ext.android.inject
import java.util.HashMap

class LocationService : Service() {
    private val singleLiveData by inject<MutableLiveData<Event<Bundle>>>()
    companion object {

        //region data
        private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
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

    //onCreate
    override fun onCreate() {
        super.onCreate()
        setupFirebase()
        initData()
    }

    private fun initData() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
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
    //Location Callback
    val bundle = Bundle()
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val currentLocation: Location = locationResult.lastLocation
            Timber.d("Response : ${currentLocation.latitude} ${currentLocation.longitude}")
            bundle.putDouble("lat", currentLocation.latitude)
            bundle.putDouble("lng", currentLocation.longitude)
            val locationData: MutableMap<String, Any?> = HashMap()
            locationData["latitude"] = currentLocation.latitude
            locationData["longitude"] = currentLocation.longitude

            pushToFirebase(locationData)
            singleLiveData.postValue(Event(bundle))
        }
    }


    private var mFirebaseTransportRef: DatabaseReference? = null
    private var locationRef: DatabaseReference? = null
    private fun setupFirebase(){
        val firebaseDataBase = FirebaseDatabase.getInstance()
        val path = "locationData/currentLocation"
        mFirebaseTransportRef = firebaseDataBase.reference
        locationRef = mFirebaseTransportRef!!.child("location")
    }

    private fun pushToFirebase(locationData: MutableMap<String, Any?>){
        Timber.d("Firebase test: $locationRef")
        locationRef!!.setValue(locationData).addOnSuccessListener {
            Timber.d("Added SuccessFully : $locationData")
        }.addOnFailureListener{
            Timber.d("Firebse Error $it")

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