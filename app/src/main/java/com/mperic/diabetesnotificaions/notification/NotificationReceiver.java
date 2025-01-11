package com.mperic.diabetesnotificaions.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        int notificationId = intent.getIntExtra("notification_id", 0);

        NotificationHelper helper = new NotificationHelper(context);
        helper.showNotification(message, notificationId);
    }
} 