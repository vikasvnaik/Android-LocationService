package com.mvvm.ui.splash

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import com.mvvm.databinding.ActivitySplashBinding
import com.mvvm.extension.*
import com.mvvm.ui.base.BaseAppCompatActivity
import com.mvvm.ui.dashboard.DashboardActivity

class SplashActivity : BaseAppCompatActivity() {

    private val binding by viewBinding(ActivitySplashBinding::inflate)

    override fun layout() = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionCheck()
    }

    override fun onResume() {
        super.onResume()
        permissionCheck()
    }

    private fun permissionCheck(){
        callLocationPermission(allow = { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            callBackgroundLocationPermission(allow = {checkGpsEnabled()})
        }  })
    }

    private fun checkGpsEnabled() {
        val lm = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alert(AppStyle.Dialog_Alert) {
                setMessage(AppString.enable_gps)
                setPositiveButton(AppString.label_ok) { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }
        } else {
            //checkBattery()
            startActivity<DashboardActivity>()
        }
    }
}