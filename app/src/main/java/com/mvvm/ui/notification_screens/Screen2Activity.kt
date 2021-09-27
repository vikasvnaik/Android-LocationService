package com.mvvm.ui.notification_screens

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.mvvm.databinding.ActivityScreen2Binding
import com.mvvm.domain.entity.wrapped.Event
import com.mvvm.extension.*
import com.mvvm.ui.base.BaseAppCompatActivity
import org.koin.android.ext.android.inject

class Screen2Activity : BaseAppCompatActivity() {
    private val singleLiveData by inject<MutableLiveData<Event<Bundle>>>()
    private val binding by viewBinding(ActivityScreen2Binding::inflate)

    override fun layout() = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        singleLiveData.observe(this, EventUnWrapObserver {
            alert(AppStyle.Dialog_Alert) {
                setMessage(AppString.screen2)
                setPositiveButton(AppString.battery_ok) { _, _ ->

                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }
}