package com.mperic.diabetesnotificaions.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.mperic.diabetesnotificaions.R;
import com.mperic.diabetesnotificaions.billing.BillingManager;
import com.mperic.diabetesnotificaions.util.PreferenceManager;
import com.mperic.diabetesnotificaions.model.NotificationMessage;

public class SettingsFragment extends Fragment {
    private PreferenceManager preferenceManager;
    private CheckBox categoryHealth, categoryRecreation, categoryFact, categoryScary;
    private CheckBox notificationSound, notificationVibrate, notificationBanner;
    private CheckBox swapNoteTime;
    private CheckBox categoryMotivation;
    private BillingManager billingManager;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        billingManager = new BillingManager(requireContext());
        
        // Observe premium status changes
        billingManager.isPremiumLiveData.observe(this, isPremium -> {
            updatePremiumUI(isPremium);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        preferenceManager = new PreferenceManager(requireContext());
        
        initializeViews(view);
        setupListeners();
        
        return view;
    }

    private void initializeViews(View view) {
        categoryFact = view.findViewById(R.id.categoryFact);
        categoryHealth = view.findViewById(R.id.categoryHealth);
        categoryScary = view.findViewById(R.id.categoryScary);
        categoryMotivation = view.findViewById(R.id.categoryMotivation);
        
        // Set initial states
        categoryFact.setChecked(true);
        categoryFact.setEnabled(true);
        
        categoryHealth.setEnabled(false);
        categoryScary.setEnabled(false);
        categoryMotivation.setEnabled(false);
        
        notificationSound = view.findViewById(R.id.notificationSound);
        notificationVibrate = view.findViewById(R.id.notificationVibrate);
        notificationBanner = view.findViewById(R.id.notificationBanner);
        
        swapNoteTime = view.findViewById(R.id.swapNoteTime);
        
        swapNoteTime.setChecked(preferenceManager.isNoteTimeSwapped());
    }

    private void setupListeners() {
        MaterialButton upgradeButton = view.findViewById(R.id.upgradeButton);
        upgradeButton.setOnClickListener(v -> {
            billingManager.launchBillingFlow(requireActivity());
        });

        // Category checkboxes
        setupCategoryCheckbox(categoryHealth, NotificationMessage.Category.HEALTH);
        setupCategoryCheckbox(categoryFact, NotificationMessage.Category.FACT);
        setupCategoryCheckbox(categoryScary, NotificationMessage.Category.SCARY);
        setupCategoryCheckbox(categoryMotivation, NotificationMessage.Category.MOTIVATION);

        // Notification settings
        notificationSound.setChecked(preferenceManager.isNotificationSoundEnabled());
        notificationSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setNotificationSoundEnabled(isChecked);
        });

        notificationVibrate.setChecked(preferenceManager.isNotificationVibrateEnabled());
        notificationVibrate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setNotificationVibrateEnabled(isChecked);
        });

        notificationBanner.setChecked(preferenceManager.isNotificationBannerEnabled());
        notificationBanner.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setNotificationBannerEnabled(isChecked);
        });

        swapNoteTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setNoteTimeSwapped(isChecked);
            // Notify RulesFragment to update its views
            requireActivity().getSupportFragmentManager()
                .setFragmentResult("SETTINGS_CHANGED", new Bundle());
        });
    }

    private void setupCategoryCheckbox(CheckBox checkbox, NotificationMessage.Category category) {
        if (category == NotificationMessage.Category.FACT) {
            // Fact category is always enabled and checked by default
            checkbox.setChecked(true);
            checkbox.setEnabled(true);
        } else {
            // Other categories depend on premium status and preferences
            checkbox.setChecked(preferenceManager.isCategoryEnabled(category));
            checkbox.setEnabled(preferenceManager.isPremium());
        }
        
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setCategoryEnabled(category, isChecked);
        });
    }

    private void updatePremiumUI(boolean isPremium) {
        MaterialButton upgradeButton = view.findViewById(R.id.upgradeButton);
        upgradeButton.setVisibility(isPremium ? View.GONE : View.VISIBLE);
        
        // Enable premium features
        categoryHealth.setEnabled(isPremium);
        categoryScary.setEnabled(isPremium);
        categoryMotivation.setEnabled(isPremium);
    }
} 