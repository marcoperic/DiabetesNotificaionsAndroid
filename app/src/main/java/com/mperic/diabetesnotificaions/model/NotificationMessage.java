package com.mperic.diabetesnotificaions.model;

public class NotificationMessage {
    public enum Category {
        HEALTH,
        RECREATION,
        FACT,
        SCARY,
        MOTIVATION
    }

    private String content;
    private Category category;
    private boolean isPremium;

    public NotificationMessage(String content, Category category, boolean isPremium) {
        this.content = content;
        this.category = category;
        this.isPremium = isPremium;
    }

    // Getters
    public String getContent() { return content; }
    public Category getCategory() { return category; }
    public boolean isPremium() { return isPremium; }
} 