package com.mvvm.ui.splash

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.mvvm.databinding.FragSplashBinding
import com.mvvm.extension.AppLayout
import com.mvvm.extension.callBackgroundLocationPermission
import com.mvvm.extension.callLocationPermission
import com.mvvm.extension.startActivity
import com.mvvm.ui.base.BaseFragment
import com.mvvm.ui.dashboard.DashboardActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

class SplashFragment : BaseFragment(AppLayout.frag_splash) {

    private var _binding: FragSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(view: View) {



        /*FirebaseInstallations.getInstance().getToken(false).addOnCompleteListener {
            if (it.isSuccessful) {
                //userDataManager.fireBaseToken = it.result.token
                //Timber.d("Installation ID: %s", userDataManager.fireBaseToken)
            } else {
                Timber.d("Unable to get Installation ID")
            }
        }
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            Timber.d("FirebaseMessaging Token ${it.result}")
        }*/

        /*lifecycleScope.launch {
            flow {
                delay(2000)
                emit(true)
            }.collect {
                startActivity<DashboardActivity>()
                finish()
            }
        }*/
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = _binding ?: FragSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}