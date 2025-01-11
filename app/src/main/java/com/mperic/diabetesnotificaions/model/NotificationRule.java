package com.mperic.diabetesnotificaions.model;

import java.time.LocalTime;

public class NotificationRule {
    private int id;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isWindowBased;
    private boolean isEnabled;
    
    public NotificationRule(int id, LocalTime startTime, LocalTime endTime, boolean isWindowBased) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isWindowBased = isWindowBased;
        this.isEnabled = true;
    }

    // Getters and setters
    public int getId() { return id; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public boolean isWindowBased() { return isWindowBased; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
} 