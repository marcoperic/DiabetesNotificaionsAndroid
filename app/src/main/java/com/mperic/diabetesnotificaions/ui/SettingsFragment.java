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
        categoryHealth = view.findViewById(R.id.categoryHealth);
        categoryRecreation = view.findViewById(R.id.categoryRecreation);
        categoryFact = view.findViewById(R.id.categoryFact);
        categoryScary = view.findViewById(R.id.categoryScary);
        
        notificationSound = view.findViewById(R.id.notificationSound);
        notificationVibrate = view.findViewById(R.id.notificationVibrate);
        notificationBanner = view.findViewById(R.id.notificationBanner);
        
        swapNoteTime = view.findViewById(R.id.swapNoteTime);
        
        swapNoteTime.setChecked(preferenceManager.isNoteTimeSwapped());
        
        categoryMotivation = view.findViewById(R.id.categoryMotivation);
    }

    private void setupListeners() {
        MaterialButton upgradeButton = view.findViewById(R.id.upgradeButton);
        upgradeButton.setOnClickListener(v -> {
            billingManager.launchBillingFlow(requireActivity());
        });

        // TODO: Implement checkbox listeners
        swapNoteTime.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setNoteTimeSwapped(isChecked);
            // Notify RulesFragment to update its views
            requireActivity().getSupportFragmentManager()
                .findFragmentByTag("f0")  // Tag for first fragment in ViewPager
                .onResume();
        });
    }

    private void updatePremiumUI(boolean isPremium) {
        MaterialButton upgradeButton = view.findViewById(R.id.upgradeButton);
        upgradeButton.setVisibility(isPremium ? View.GONE : View.VISIBLE);
        
        // Enable premium features
        categoryHealth.setEnabled(isPremium);
        categoryRecreation.setEnabled(isPremium);
        categoryScary.setEnabled(isPremium);
        categoryMotivation.setEnabled(isPremium);
    }
} 