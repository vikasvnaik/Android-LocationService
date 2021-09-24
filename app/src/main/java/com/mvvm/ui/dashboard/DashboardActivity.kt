package com.mvvm.ui.dashboard

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.mvvm.R
import com.mvvm.databinding.ActivityDashboardBinding
import com.mvvm.extension.callBackgroundLocationPermission
import com.mvvm.extension.callLocationPermission
import com.mvvm.extension.viewBinding
import com.mvvm.service.LocationService
import com.mvvm.ui.base.BaseAppCompatActivity
import com.mvvm.vm.dashboard.DashboardVM
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardActivity : BaseAppCompatActivity() {
    private val binding by viewBinding(ActivityDashboardBinding::inflate)
    private val dashboardVM by viewModel<DashboardVM>()

    override fun layout() = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupLocation()
    }

    override fun onResume() {
        super.onResume()

        setupLocation()
    }

    private fun setupLocation() {
        if (isServiceRunning(LocationService::class.java)) {
            startLocationService()
            setTrackingStatus(R.string.tracking)
        } else {
            startLocationService()
            callLocationPermission(allow = { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                callBackgroundLocationPermission(allow = {checkGpsEnabled()})
            }  })
        }
    }

    private fun setTrackingStatus(status: Int) {
        val tracking = status == R.string.tracking
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun checkGpsEnabled() {
        val lm = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //reportGpsError()
        } else {
            checkBattery()
        }
    }

    private fun checkBattery() {
        if (isBatteryOptimized()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    startActivity(intent)
                } catch (e: Exception) {
                    showBatteryDialog()
                }
            } else {
                startLocationService()
            }
        } else {
            startLocationService()
        }
    }

    private fun isBatteryOptimized(): Boolean {
        val pwrm = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
        val name = applicationContext.packageName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !pwrm.isIgnoringBatteryOptimizations(name)
        }
        return false
    }

    @SuppressLint("BatteryLife")
    private fun startLocationService() { // Before we start the service, confirm that we have extra power usage privileges.
        ContextCompat.startForegroundService(this, Intent(this, LocationService::class.java))
    }

    private var builder: AlertDialog.Builder? = null
    @SuppressLint("BatteryLife")
    private fun showBatteryDialog() {
        builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        builder!!.setTitle(R.string.battery_title)
        builder!!.setMessage(R.string.battery_content)
        builder!!.setCancelable(false)

        builder!!.setPositiveButton(
            R.string.battery_ok
        ) { dialog, id ->
            dialog.dismiss()
            val intentBattery = Intent()
            val packageName: String = packageName
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    try {
                        intentBattery.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                        intentBattery.data = Uri.parse("package:$packageName")
                        startActivity(intentBattery)
                    } catch (e: Exception) {

                    }
                }
            }
        }

        val alert11 = builder!!.create()
        alert11.show()
    }
}