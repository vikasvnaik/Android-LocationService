package com.mvvm.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.mvvm.service.LocationService
import com.mvvm.service.TrackingWorkManager
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class CommonUtils {
    companion object {
        /**
         * Refresh Service
         * */
        @SuppressLint("BatteryLife")
        fun refreshLocationService(context: Context) {

            try {
                if (!isServiceRunning(LocationService::class.java, context)) {
                    ContextCompat.startForegroundService(
                        context,
                        Intent(context, LocationService::class.java)
                    )
                } else {
                    context.stopService(Intent(context, LocationService::class.java))
                    ContextCompat.startForegroundService(
                        context,
                        Intent(context, LocationService::class.java)
                    )
                }
            } catch (e: Exception) {
            }
        }

        @SuppressLint("BatteryLife")
        fun startLocationService(context: Context) {
            if (!isServiceRunning(LocationService::class.java, context)) {
                ContextCompat.startForegroundService(
                    context,
                    Intent(context, LocationService::class.java)
                )
            }
        }

        fun stopLocationService(context: Context) {
            if (isServiceRunning(LocationService::class.java, context)) {
                context.stopService(Intent(context, LocationService::class.java))
            }
        }

        fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
            val manager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
            return false
        }

        fun startWorker(context: Context) {
            try {
                val workRequest: PeriodicWorkRequest =
                    PeriodicWorkRequest.Builder(
                        TrackingWorkManager::class.java,
                        15,
                        TimeUnit.MINUTES
                    ).addTag("locationWorkManager").build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "TrackingWorkManager",
                    ExistingPeriodicWorkPolicy.REPLACE, workRequest
                )
            } catch (e: Exception) {
            }
        }


        fun stopWorker(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag("locationWorkManager")
        }
    }
}