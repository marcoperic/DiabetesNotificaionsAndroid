package com.mperic.diabetesnotificaions.notification;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.reflect.TypeToken;
import com.mperic.diabetesnotificaions.model.NotificationRule;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SchedulingManager {
    private static final String PREF_NAME = "notification_rules";
    private static final String KEY_RULES = "rules";
    private final Context context;
    private final SharedPreferences prefs;
    private final NotificationHelper notificationHelper;
    private final Gson gson;

    public SchedulingManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.notificationHelper = new NotificationHelper(context);
        
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalTime.class, new TypeAdapter<LocalTime>() {
                @Override
                public void write(JsonWriter out, LocalTime value) throws IOException {
                    out.value(value != null ? value.format(DateTimeFormatter.ISO_LOCAL_TIME) : null);
                }

                @Override
                public LocalTime read(JsonReader in) throws IOException {
                    String time = in.nextString();
                    return time != null ? LocalTime.parse(time) : null;
                }
            })
            .create();
    }

    public void saveRule(NotificationRule rule) {
        List<NotificationRule> rules = getRules();
        // Check if rule already exists and update it
        boolean updated = false;
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).getId() == rule.getId()) {
                rules.set(i, rule);
                updated = true;
                break;
            }
        }
        // If rule doesn't exist, add it
        if (!updated) {
            rules.add(rule);
        }
        saveRules(rules);
        scheduleRule(rule);
    }

    public void removeRule(NotificationRule rule) {
        List<NotificationRule> rules = getRules();
        rules.removeIf(r -> r.getId() == rule.getId());
        saveRules(rules);
        // Cancel the alarm for this rule
        notificationHelper.cancelNotification(rule.getId());
    }

    public List<NotificationRule> getRules() {
        String rulesJson = prefs.getString(KEY_RULES, "[]");
        Type type = new TypeToken<ArrayList<NotificationRule>>(){}.getType();
        List<NotificationRule> rules = gson.fromJson(rulesJson, type);
        return new ArrayList<>(rules != null ? rules : new ArrayList<>());
    }

    private void saveRules(List<NotificationRule> rules) {
        String rulesJson = gson.toJson(rules);
        prefs.edit().putString(KEY_RULES, rulesJson).commit(); // Using commit() instead of apply() for immediate write
    }

    private void scheduleRule(NotificationRule rule) {
        if (!rule.isEnabled()) return;

        long triggerTime;
        if (rule.isWindowBased()) {
            // Calculate random time within window
            LocalTime startTime = rule.getStartTime();
            LocalTime endTime = rule.getEndTime();
            int startMinutes = startTime.getHour() * 60 + startTime.getMinute();
            int endMinutes = endTime.getHour() * 60 + endTime.getMinute();
            int randomMinutes = new Random().nextInt(endMinutes - startMinutes) + startMinutes;
            triggerTime = calculateNextTriggerTime(LocalTime.of(randomMinutes / 60, randomMinutes % 60));
        } else {
            triggerTime = calculateNextTriggerTime(rule.getStartTime());
        }

        notificationHelper.scheduleNotification(triggerTime, "Time to check your diabetes!", rule.getId());
    }

    private long calculateNextTriggerTime(LocalTime time) {
        // TODO: Implement proper time calculation logic
        return System.currentTimeMillis() + 60000; // For testing: triggers after 1 minute
    }

    public void rescheduleAllRules() {
        List<NotificationRule> rules = getRules();
        for (NotificationRule rule : rules) {
            scheduleRule(rule);
        }
    }
} 