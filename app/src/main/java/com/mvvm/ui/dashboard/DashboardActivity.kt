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
import com.mvvm.extension.*
import com.mvvm.service.LocationService
import com.mvvm.ui.base.BaseAppCompatActivity
import com.mvvm.utils.CommonUtils
import com.mvvm.vm.dashboard.DashboardVM
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardActivity : BaseAppCompatActivity() {
    private val binding by viewBinding(ActivityDashboardBinding::inflate)
    private val dashboardVM by viewModel<DashboardVM>()

    override fun layout() = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkBattery()
        //setupLocation()
    }

    override fun onResume() {
        super.onResume()

        //setupLocation()
    }

    private fun setupLocation() {
        if (CommonUtils.isServiceRunning(LocationService::class.java, this)) {
            setTrackingStatus(R.string.tracking)
        }
    }

    private fun setTrackingStatus(status: Int) {
        val tracking = status == R.string.tracking
    }

    private fun checkBattery() {
        if (isBatteryOptimized()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    alert(AppStyle.Dialog_Alert) {
                        setMessage(AppString.battery_content)
                        setPositiveButton(AppString.battery_ok) { _, _ ->
                            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                            startActivity(intent)
                        }
                    }
                } catch (e: Exception) {
                    alert(AppStyle.Dialog_Alert) {
                        setMessage(AppString.battery_content)
                        setPositiveButton(AppString.battery_ok) { _, _ ->
                    val intentBattery = Intent()
                    val packageName: String = packageName
                    intentBattery.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intentBattery.data = Uri.parse("package:$packageName")
                    startActivity(intentBattery)
                        }
                    }
                }
            } else {
                //CommonUtils.startLocationService(this)
            }
        } else {
            //CommonUtils.startLocationService(this)
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
}