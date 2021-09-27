package com.mvvm.ui.notification_screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mvvm.databinding.ActivityNotificationTestBinding
import com.mvvm.databinding.ActivityScreen1Binding
import com.mvvm.extension.viewBinding
import com.mvvm.ui.base.BaseAppCompatActivity
import android.app.NotificationManager

import android.app.Notification

import android.app.PendingIntent

import android.content.Intent

import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.mvvm.R


class NotificationTestActivity : BaseAppCompatActivity() {
    private val binding by viewBinding(ActivityNotificationTestBinding::inflate)

    override fun layout() = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.screen1.setOnClickListener {
            //CustomNotification("test","test")
        }
    }

    override fun onResume() {
        super.onResume()
    }

    /*fun CustomNotification(title: String, body: String) {
        // Using RemoteViews to bind custom layouts into Notification
        val remoteViews = RemoteViews(
            packageName,
            R.layout.customnotification
        )

        // Open NotificationView Class on Notification Click
        val intent = Intent(this, NotificationView::class.java)
        // Send data to NotificationView Class
        intent.putExtra("title", title)
        intent.putExtra("text", body)
        // Open NotificationView.java Activity
        val pIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this) // Set Icon
            .setSmallIcon(R.drawable.ic_back) // Set Ticker Message
            .setTicker("Test123") // Dismiss Notification
            .setAutoCancel(true) // Set PendingIntent into Notification
            .setContentIntent(pIntent) // Set RemoteViews into Notification
            .setContent(remoteViews)

        // Locate and set the Image into customnotificationtext.xml ImageViews
        remoteViews.setImageViewResource(R.id.imagenotileft, R.drawable.ic_launcher_foreground)
        remoteViews.setImageViewResource(R.id.imagenotiright, R.drawable.ic_launcher_foreground)

        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.title,title)
        remoteViews.setTextViewText(R.id.text, body)

        // Create Notification Manager
        val notificationmanager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build())
    }*/
}