package com.mvvm.ui.notification_screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.MutableLiveData
import com.mvvm.databinding.ActivityDashboardBinding
import com.mvvm.databinding.ActivityScreen1Binding
import com.mvvm.domain.entity.wrapped.Event
import com.mvvm.extension.*
import com.mvvm.ui.base.BaseAppCompatActivity
import org.koin.android.ext.android.inject

class Screen1Activity : BaseAppCompatActivity() {
    private val singleLiveData by inject<MutableLiveData<Event<Bundle>>>()
    private val binding by viewBinding(ActivityScreen1Binding::inflate)

    override fun layout() = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        singleLiveData.observe(this, EventUnWrapObserver {
            alert(AppStyle.Dialog_Alert) {
                setMessage(AppString.screen1)
                setPositiveButton(AppString.battery_ok) { _, _ ->

                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }
}