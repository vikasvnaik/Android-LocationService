package com.mvvm.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.mvvm.R;
import com.mvvm.ui.dashboard.DashboardActivity;

public class NotificationHelper extends ContextWrapper {

    private NotificationManager manager;

    public NotificationHelper(Context context) {
        super(context);
    }

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param context The application context
     */
    public NotificationHelper(Context context, String channelId, String channelName) {
        super(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel chanl = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW);
            chanl.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            chanl.setDescription("no sound");
            chanl.setSound(null, null);
            chanl.enableLights(false);
            chanl.setLightColor(Color.BLUE);
            chanl.enableVibration(false);
            getManager().createNotificationChannel(chanl);


        }
    }

    /**
     * Get a notification for Tracking service
     * <p>
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    public NotificationCompat.Builder getTrackingNotification(Context context, String CHANNEL_ID) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(getSmallIcon());
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setShowWhen(true);
        builder.setCategory(Notification.CATEGORY_SERVICE);

        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        builder.setContentTitle(context.getString(R.string.settings_status_on_summary));
        builder.setTicker(context.getString(R.string.settings_status_on_summary));
        builder.setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));

        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
        return builder;
    }


    /**
     * Send a notification.
     *
     * @param id           The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Notification notification) {
        getManager().notify(id, notification);
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private int getSmallIcon() {
        return R.drawable.ic_launcher_foreground;
    }

    /**
     * Get the notification manager.
     * <p>
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public void cancel(int notificationId) {
        getManager().cancel(notificationId);
    }
}
