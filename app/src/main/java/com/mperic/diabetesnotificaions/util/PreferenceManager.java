package com.mperic.diabetesnotificaions.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.mperic.diabetesnotificaions.model.NotificationMessage;

public class PreferenceManager {
    private static final String PREF_NAME = "DiabetesNotificationPrefs";
    private static final String KEY_PREMIUM = "is_premium";
    private static final String KEY_CATEGORY_HEALTH = "category_health";
    private static final String KEY_CATEGORY_RECREATION = "category_recreation";
    private static final String KEY_CATEGORY_FACT = "category_fact";
    private static final String KEY_CATEGORY_SCARY = "category_scary";
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
        String key = getCategoryKey(category);
        return prefs.getBoolean(key, true);
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

    private String getCategoryKey(NotificationMessage.Category category) {
        switch (category) {
            case HEALTH: return KEY_CATEGORY_HEALTH;
            case RECREATION: return KEY_CATEGORY_RECREATION;
            case FACT: return KEY_CATEGORY_FACT;
            case SCARY: return KEY_CATEGORY_SCARY;
            default: throw new IllegalArgumentException("Unknown category");
        }
    }
} 