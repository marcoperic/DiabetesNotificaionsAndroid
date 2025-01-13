package com.mperic.diabetesnotificaions.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.mperic.diabetesnotificaions.DiabetesApp;
import com.mperic.diabetesnotificaions.R;

public class NotificationHelper {
    private final Context context;
    private final NotificationManager notificationManager;
    private final AlarmManager alarmManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void scheduleNotification(long triggerTime, String message, int notificationId) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("message", message);
        intent.putExtra("notification_id", notificationId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
        );
    }

    public void showNotification(String message, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DiabetesApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background) // TODO: update this with the application icon NOTIFICATION notif
                .setContentTitle("Diabetes Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        notificationManager.notify(notificationId, builder.build());
    }

    public void cancelNotification(int notificationId) {
        // Cancel the pending notification
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
        
        // Remove any existing notification
        notificationManager.cancel(notificationId);
    }
} 