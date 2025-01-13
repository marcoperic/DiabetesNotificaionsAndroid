package com.mperic.diabetesnotificaions.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mperic.diabetesnotificaions.model.NotificationRule;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        int notificationId = intent.getIntExtra("notification_id", 0);

        // Show the notification
        NotificationHelper helper = new NotificationHelper(context);
        helper.showNotification(message, notificationId);

        // Reschedule for tomorrow
        SchedulingManager schedulingManager = new SchedulingManager(context);
        NotificationRule rule = schedulingManager.getRuleById(notificationId);
        if (rule != null && rule.isEnabled()) {
            schedulingManager.scheduleRule(rule);
        }
    }
} 