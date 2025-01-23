package com.mperic.diabetesnotificaions.util;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FactManager {
    private static final List<String> GREETINGS = new ArrayList<>();
    private static final List<Fact> FACTS = new ArrayList<>();
    private static final Random random = new Random();

    static {
        // Initialize greetings
        GREETINGS.add("Good morning!");
        GREETINGS.add("Good afternoon!");
        GREETINGS.add("Good evening!");
        GREETINGS.add("Great to see you!");
        GREETINGS.add("Welcome back!");
        GREETINGS.add("Please continue taking care of yourself!");
        GREETINGS.add("Every day is a new opportunity!");
        GREETINGS.add("Your health matters!");

        // Initialize facts
        // Health Tips
        FACTS.add(new Fact("Exercising regularly can help improve insulin sensitivity.", "Health Tip"));
        FACTS.add(new Fact("Eating a high-fiber diet may slow the absorption of carbohydrates.", "Health Tip"));
        FACTS.add(new Fact("Getting 7-9 hours of quality sleep per night is important for diabetes management.", "Health Tip"));

        // Diabetes Facts
        FACTS.add(new Fact("Diabetes increases the risk of developing nerve damage (neuropathy).", "Diabetes Fact"));
        FACTS.add(new Fact("Proper foot care is crucial to prevent complications like foot ulcers.", "Diabetes Fact"));
        FACTS.add(new Fact("Maintaining healthy blood pressure levels is key for cardiovascular health.", "Diabetes Fact"));

        // Motivational Quotes
        FACTS.add(new Fact("Small steps lead to big victories in your health journey.", "Motivation"));
        FACTS.add(new Fact("Your dedication to wellness is an investment in your future.", "Motivation"));
        FACTS.add(new Fact("Your health is the foundation for everything else in life.", "Motivation"));

        // Trending Topics
        FACTS.add(new Fact("New research shows intermittent fasting may improve blood sugar control.", "Trending Topic"));
        FACTS.add(new Fact("The American Diabetes Association updated its nutrition recommendations.", "Trending Topic"));
        FACTS.add(new Fact("Innovative insulin delivery devices are improving quality of life for many.", "Trending Topic"));

        // Seasonal Reminders
        FACTS.add(new Fact("Stay hydrated during hot summer months to help regulate blood sugar.", "Seasonal Reminder"));
        FACTS.add(new Fact("Bundle up in winter to avoid blood sugar drops from cold temperatures.", "Seasonal Reminder"));
        FACTS.add(new Fact("Don't forget to get your annual flu vaccine to prevent illness complications.", "Seasonal Reminder"));
    }

    public static String getGreeting() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();

        if (hour >= 5 && hour < 12) {
            return "Good morning!";
        } else if (hour >= 12 && hour < 18) {
            return "Good afternoon!";
        } else {
            // Get a random evening greeting
            List<String> eveningGreetings = new ArrayList<>(GREETINGS);
            eveningGreetings.remove("Good morning!");
            eveningGreetings.remove("Good afternoon!");
            return eveningGreetings.get(random.nextInt(eveningGreetings.size()));
        }
    }

    public static Fact getRandomFact() {
        return FACTS.get(random.nextInt(FACTS.size()));
    }

    public static class Fact {
        private final String content;
        private final String category;

        public Fact(String content, String category) {
            this.content = content;
            this.category = category;
        }

        public String getContent() { return content; }
        public String getCategory() { return category; }
    }
} 