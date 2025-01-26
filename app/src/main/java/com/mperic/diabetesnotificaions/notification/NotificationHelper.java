package com.mperic.diabetesnotificaions.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import com.mperic.diabetesnotificaions.DiabetesApp;
import com.mperic.diabetesnotificaions.R;
import com.mperic.diabetesnotificaions.data.MessageRepository;
import com.mperic.diabetesnotificaions.model.NotificationMessage;
import com.mperic.diabetesnotificaions.model.NotificationRule;
import com.mperic.diabetesnotificaions.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotificationHelper {
    private final Context context;
    private final NotificationManager notificationManager;
    private final AlarmManager alarmManager;
    private final PreferenceManager preferenceManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.preferenceManager = new PreferenceManager(context);
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
                .setSmallIcon(R.drawable.ic_logo)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo))
                .setContentTitle("Diabetes Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);

        // Apply user preferences
        int defaults = 0;
        if (preferenceManager.isNotificationSoundEnabled()) {
            defaults |= NotificationCompat.DEFAULT_SOUND;
        }
        if (preferenceManager.isNotificationVibrateEnabled()) {
            defaults |= NotificationCompat.DEFAULT_VIBRATE;
        }
        builder.setDefaults(defaults);

        if (preferenceManager.isNotificationBannerEnabled()) {
            notificationManager.notify(notificationId, builder.build());
        }
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

    public String getMessageForRule(NotificationRule rule) {
        if (preferenceManager.isPremium() && rule.isUseNoteAsNotification() && 
            rule.getNote() != null && !rule.getNote().isEmpty()) {
            return rule.getNote();
        }

        List<NotificationMessage.Category> enabledCategories;
        if (rule.hasCustomCategories()) {
            enabledCategories = new ArrayList<>(rule.getEnabledCategories());
        } else {
            enabledCategories = preferenceManager.getEnabledCategories();
        }

        if (enabledCategories.isEmpty()) {
            return "Time to check your diabetes!";
        }

        Random random = new Random();
        NotificationMessage.Category selectedCategory = 
            enabledCategories.get(random.nextInt(enabledCategories.size()));
        
        String message = MessageRepository.getInstance(context)
            .getRandomMessage(selectedCategory, preferenceManager.isPremium());
        
        return message != null ? message : "Time to check your diabetes!";
    }
} 