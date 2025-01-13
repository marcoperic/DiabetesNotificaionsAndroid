package com.mperic.diabetesnotificaions;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class DiabetesApp extends Application {
    public static final String CHANNEL_ID = "diabetes_notifications";
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Diabetes Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Daily diabetes reminders");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500}); // Vibration pattern
            channel.setLockscreenVisibility(NotificationManager.IMPORTANCE_HIGH); // Show on lock screen
            channel.enableLights(true);
            channel.setShowBadge(true);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
} 