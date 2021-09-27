package com.mvvm.ui.notifications


import android.content.Intent
import android.os.Bundle
import androidx.core.app.TaskStackBuilder
import com.mvvm.databinding.ActivityHandlePushBinding
import com.mvvm.extension.viewBinding
import com.mvvm.ui.base.BaseAppCompatActivity
import com.mvvm.ui.dashboard.DashboardActivity
import com.mvvm.ui.notification_screens.Screen1Activity
import com.mvvm.ui.notification_screens.Screen2Activity

class PushHandleActivity : BaseAppCompatActivity() {
    private val binding by viewBinding(ActivityHandlePushBinding::inflate)
    override fun layout() = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent.action) {
            "screen1" -> {
                val intentOnTop = Intent(this, Screen1Activity::class.java)
                val intentOnBottom = Intent(this, DashboardActivity::class.java)
                TaskStackBuilder.create(this)
                    .addNextIntent(intentOnBottom)
                    // use this method if you want "intentOnTop" to have it's parent chain of activities added to the stack. Otherwise, more "addNextIntent" calls will do.
                    .addNextIntentWithParentStack(intentOnTop)
                    .startActivities()
            }
            "screen2" -> {
                val intentOnTop = Intent(this, Screen2Activity::class.java)
                val intentOnBottom = Intent(this, DashboardActivity::class.java)
                TaskStackBuilder.create(this)
                    .addNextIntent(intentOnBottom)
                    // use this method if you want "intentOnTop" to have it's parent chain of activities added to the stack. Otherwise, more "addNextIntent" calls will do.
                    .addNextIntentWithParentStack(intentOnTop)
                    .startActivities()
            }
            "general" -> {
                val intentOnBottom = Intent(this, DashboardActivity::class.java)
                TaskStackBuilder.create(this)
                    .addNextIntent(intentOnBottom)
                    .startActivities()
            }

        }
    }
}