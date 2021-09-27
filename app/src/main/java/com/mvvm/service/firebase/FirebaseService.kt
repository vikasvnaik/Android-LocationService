package com.mvvm.service.firebase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mvvm.domain.manager.UserPrefDataManager
import org.koin.android.ext.android.inject
import timber.log.Timber
import com.mvvm.domain.entity.wrapped.Event
import com.mvvm.extension.*
import com.mvvm.ui.notifications.PushHandleActivity


class FirebaseService : FirebaseMessagingService() {

    private val userPrefDataManager by inject<UserPrefDataManager>()
    private val singleLiveData by inject<MutableLiveData<Event<Bundle>>>()
    private val notificationManager by inject<NotificationManager>()
    private val CHANNEL_ID = "Channel_Test"

    override fun onNewToken(token: String) {
        Timber.d("Refreshed token: $token")
        userPrefDataManager.fireBaseToken = token
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Timber.d(remoteMessage?.toJson())
        // Check if message contains a data payload.
        val intent = Intent(this, PushHandleActivity::class.java)
        val bundle = Bundle()
        remoteMessage.data.isNotEmpty().let {
            Timber.d("Message data payload: %s", remoteMessage.data)
            bundle.apply {
                remoteMessage.data.entries.forEach {
                    this.putString(it.key, it.value)
                }
            }
            intent.putExtras(bundle)
        }

        // Check if message contains a notification payload.
        var title = "Default"
        var body = "Default"
        var largeIcon: Uri? = null
        remoteMessage.notification?.let {
            Timber.d("Message Notification Body: ${it.body}")
            title = it.title ?: ""
            body = it.body ?: ""
            largeIcon = it.imageUrl
            intent.action = it.clickAction
            bundle.putString("action", it.clickAction)
        }
        singleLiveData.postValue(Event(bundle))
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(AppDrawable.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(resources, AppMipmap.ic_launcher_round))
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setColor(resColor(AppColor.colorAccent))
            .setAutoCancel(true)
        largeIcon?.let {
            load(it) {
                it?.let {
                    val bitmap = it.toBitmap()
                    builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                }
                notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
            }
        } ?: run {
            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        }

    }

}