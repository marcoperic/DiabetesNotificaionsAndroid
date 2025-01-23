package com.mperic.diabetesnotificaions.data;

import android.content.Context;
import android.content.res.Resources;
import com.mperic.diabetesnotificaions.R;
import com.mperic.diabetesnotificaions.model.NotificationMessage;
import java.util.Random;

public class MessageRepository {
    private static MessageRepository instance;
    private final Context context;
    private final Random random;
    
    private MessageRepository(Context context) {
        this.context = context.getApplicationContext();
        this.random = new Random();
    }
    
    public static MessageRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MessageRepository(context);
        }
        return instance;
    }
    
    public String getRandomMessage(NotificationMessage.Category category, boolean isPremium) {
        String[] messages = getMessagesForCategory(category);
        if (messages == null || messages.length == 0) {
            return null;
        }
        
        // For non-premium users, only use the first 3 messages
        int maxIndex = isPremium ? messages.length : Math.min(3, messages.length);
        return messages[random.nextInt(maxIndex)];
    }
    
    private String[] getMessagesForCategory(NotificationMessage.Category category) {
        Resources res = context.getResources();
        switch (category) {
            case HEALTH:
                return res.getStringArray(R.array.health_messages);
            case FACT:
                return res.getStringArray(R.array.fact_messages);
            case SCARY:
                return res.getStringArray(R.array.scary_messages);
            case MOTIVATION:
                return res.getStringArray(R.array.motivation_messages);
            default:
                return null;
        }
    }
}
