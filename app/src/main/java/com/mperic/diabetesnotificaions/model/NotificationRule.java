package com.mperic.diabetesnotificaions.model;

import java.time.LocalTime;
import android.graphics.Color;

public class NotificationRule {
    private int id;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isWindowBased;
    private boolean isEnabled;
    private String note;
    private int color;
    private boolean useNoteAsNotification;
    
    public NotificationRule(int id, LocalTime startTime, LocalTime endTime, boolean isWindowBased) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isWindowBased = isWindowBased;
        this.isEnabled = true;
        this.note = "";
        this.color = Color.WHITE; // Default color
    }

    // Getters and setters
    public int getId() { return id; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public boolean isWindowBased() { return isWindowBased; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public boolean isUseNoteAsNotification() { return useNoteAsNotification; }
    public void setUseNoteAsNotification(boolean useNoteAsNotification) { 
        this.useNoteAsNotification = useNoteAsNotification; 
    }
} 