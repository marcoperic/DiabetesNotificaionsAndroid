package com.mperic.diabetesnotificaions.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.mperic.diabetesnotificaions.model.NotificationMessage;
import java.util.ArrayList;
import java.util.List;

public class PreferenceManager {
    private static final String PREF_NAME = "DiabetesNotificationPrefs";
    private static final String KEY_PREMIUM = "is_premium";
    private static final String KEY_CATEGORY_FACT = "category_fact";
    private static final String KEY_CATEGORY_HEALTH = "category_health";
    private static final String KEY_CATEGORY_SCARY = "category_scary";
    private static final String KEY_CATEGORY_MOTIVATION = "category_motivation";
    private static final String KEY_CATEGORY_RECREATION = "category_recreation";
    private static final String KEY_NOTIFICATION_SOUND = "notification_sound";
    private static final String KEY_NOTIFICATION_VIBRATE = "notification_vibrate";
    private static final String KEY_NOTIFICATION_BANNER = "notification_banner";
    private static final String KEY_SWAP_NOTE_TIME = "swap_note_time";

    private final SharedPreferences prefs;

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isPremium() {
        return prefs.getBoolean(KEY_PREMIUM, false);
    }

    public void setPremium(boolean isPremium) {
        prefs.edit().putBoolean(KEY_PREMIUM, isPremium).apply();
    }

    public boolean isCategoryEnabled(NotificationMessage.Category category) {
        if (!isPremium()) {
            // For non-premium users, only FACT category can be enabled/disabled
            if (category == NotificationMessage.Category.FACT) {
                return prefs.getBoolean(getCategoryKey(category), true);
            }
            return false;
        }
        // For premium users, all categories can be enabled/disabled
        return prefs.getBoolean(getCategoryKey(category), category == NotificationMessage.Category.FACT);
    }

    public void setCategoryEnabled(NotificationMessage.Category category, boolean enabled) {
        String key = getCategoryKey(category);
        prefs.edit().putBoolean(key, enabled).apply();
    }

    public boolean isNotificationSoundEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATION_SOUND, true);
    }

    public boolean isNotificationVibrateEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATION_VIBRATE, true);
    }

    public boolean isNotificationBannerEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATION_BANNER, true);
    }

    public boolean isNoteTimeSwapped() {
        return prefs.getBoolean(KEY_SWAP_NOTE_TIME, false);
    }

    public void setNoteTimeSwapped(boolean swapped) {
        prefs.edit().putBoolean(KEY_SWAP_NOTE_TIME, swapped).apply();
    }

    public void setNotificationSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_SOUND, enabled).apply();
    }

    public void setNotificationVibrateEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_VIBRATE, enabled).apply();
    }

    public void setNotificationBannerEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_BANNER, enabled).apply();
    }

    private String getCategoryKey(NotificationMessage.Category category) {
        switch (category) {
            case FACT: return KEY_CATEGORY_FACT;
            case HEALTH: return KEY_CATEGORY_HEALTH;
            case SCARY: return KEY_CATEGORY_SCARY;
            case MOTIVATION: return KEY_CATEGORY_MOTIVATION;
            default: throw new IllegalArgumentException("Unknown category");
        }
    }

    public List<NotificationMessage.Category> getEnabledCategories() {
        List<NotificationMessage.Category> enabledCategories = new ArrayList<>();
        for (NotificationMessage.Category category : NotificationMessage.Category.values()) {
            if (isCategoryEnabled(category)) {
                enabledCategories.add(category);
            }
        }
        return enabledCategories;
    }
} 