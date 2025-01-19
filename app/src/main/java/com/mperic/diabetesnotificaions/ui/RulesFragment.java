package com.mperic.diabetesnotificaions.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.mperic.diabetesnotificaions.R;
import com.google.android.material.button.MaterialButton;
import com.mperic.diabetesnotificaions.model.NotificationRule;
import com.mperic.diabetesnotificaions.notification.SchedulingManager;
import com.mperic.diabetesnotificaions.util.PreferenceManager;
import com.mperic.diabetesnotificaions.ui.adapter.RulesAdapter;

import java.time.LocalTime;
import java.util.Random;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class RulesFragment extends Fragment {
    private RecyclerView rulesRecyclerView;
    private MaterialButton addRuleButton;
    private SchedulingManager schedulingManager;
    private PreferenceManager preferenceManager;
    private View dialogView;
    private RulesAdapter rulesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rules, container, false);

        schedulingManager = new SchedulingManager(requireContext());
        preferenceManager = new PreferenceManager(requireContext());

        // Initialize views
        rulesRecyclerView = view.findViewById(R.id.rulesRecyclerView);
        addRuleButton = view.findViewById(R.id.addRuleButton);
        TextView emptyStateText = view.findViewById(R.id.emptyStateText);
        View factCard = view.findViewById(R.id.factCard);
        TextView factText = factCard.findViewById(R.id.factText);
        TextView factCategory = factCard.findViewById(R.id.factCategory);

        // Set up RecyclerView
        rulesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rulesAdapter = new RulesAdapter(schedulingManager.getRules(), new RulesAdapter.OnRuleActionListener() {
            @Override
            public void onRuleEnabled(NotificationRule rule, boolean enabled) {
                rule.setEnabled(enabled);
                schedulingManager.saveRule(rule);
            }

            @Override
            public void onRuleDeleted(NotificationRule rule) {
                schedulingManager.removeRule(rule);
                updateRulesList();
            }
        });
        
        rulesRecyclerView.setAdapter(rulesAdapter);

        // Update empty state visibility
        updateEmptyState(emptyStateText);

        // Set up random fact
        updateFactCard(factText, factCategory);

        addRuleButton.setOnClickListener(v -> showAddRuleDialog());

        return view;
    }

    private void updateEmptyState(TextView emptyStateText) {
        boolean isEmpty = schedulingManager.getRules().isEmpty();
        emptyStateText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rulesRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void updateFactCard(TextView factText, TextView factCategory) {
        // TODO: Replace with actual fact loading logic
        factText.setText("Regular exercise can help regulate blood sugar levels and improve insulin sensitivity.");
        factCategory.setText("Health Tip");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRulesList();
    }

    private void updateRulesList() {
        rulesAdapter.updateRules(schedulingManager.getRules());
        updateEmptyState(requireView().findViewById(R.id.emptyStateText));
    }

    private void showAddRuleDialog() {
        dialogView = getLayoutInflater().inflate(R.layout.dialog_add_rule, null);
        
        RadioGroup timeTypeRadioGroup = dialogView.findViewById(R.id.timeTypeRadioGroup);
        LinearLayout exactTimeLayout = dialogView.findViewById(R.id.exactTimeLayout);
        LinearLayout timeWindowLayout = dialogView.findViewById(R.id.timeWindowLayout);
        
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        TextInputEditText startTimeInput = dialogView.findViewById(R.id.startTimeInput);
        TextInputEditText endTimeInput = dialogView.findViewById(R.id.endTimeInput);
        
        // Set 24-hour format for TimePicker
        timePicker.setIs24HourView(true);
        
        // Setup time input clicks
        startTimeInput.setOnClickListener(v -> showTimePickerDialog(startTimeInput));
        endTimeInput.setOnClickListener(v -> showTimePickerDialog(endTimeInput));
        
        View categoriesHeader = dialogView.findViewById(R.id.categoriesHeader);
        View categoriesContent = dialogView.findViewById(R.id.categoriesContent);
        ImageView expandIcon = dialogView.findViewById(R.id.expandIcon);
        
        // Category checkboxes
        CheckBox categoryHealth = dialogView.findViewById(R.id.categoryHealth);
        CheckBox categoryRecreation = dialogView.findViewById(R.id.categoryRecreation);
        CheckBox categoryFact = dialogView.findViewById(R.id.categoryFact);
        CheckBox categoryScary = dialogView.findViewById(R.id.categoryScary);
        
        // Enable premium features if user is premium
        boolean isPremium = preferenceManager.isPremium();
        categoryHealth.setEnabled(isPremium);
        categoryRecreation.setEnabled(isPremium);
        categoryScary.setEnabled(isPremium);

        categoriesHeader.setOnClickListener(v -> {
            boolean isExpanded = categoriesContent.getVisibility() == View.VISIBLE;
            categoriesContent.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            expandIcon.setRotation(isExpanded ? 0 : 180);
        });

        timeTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.exactTimeRadio) {
                exactTimeLayout.setVisibility(View.VISIBLE);
                timeWindowLayout.setVisibility(View.GONE);
            } else {
                exactTimeLayout.setVisibility(View.GONE);
                timeWindowLayout.setVisibility(View.VISIBLE);
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.RoundedDialog)
                .setTitle(R.string.add_notification_rule)
                .setView(dialogView)
                .setPositiveButton(R.string.add, null)
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                boolean isWindowBased = timeTypeRadioGroup.getCheckedRadioButtonId() == R.id.timeWindowRadio;
                
                LocalTime startTime, endTime;
                if (isWindowBased) {
                    if (startTimeInput.getText() == null || endTimeInput.getText() == null ||
                        startTimeInput.getText().toString().isEmpty() || 
                        endTimeInput.getText().toString().isEmpty()) {
                        Toast.makeText(requireContext(), 
                            "Please select both start and end times", 
                            Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    startTime = LocalTime.parse(startTimeInput.getText().toString(), 
                        DateTimeFormatter.ofPattern("HH:mm"));
                    endTime = LocalTime.parse(endTimeInput.getText().toString(), 
                        DateTimeFormatter.ofPattern("HH:mm"));
                    
                    // Validate time window
                    long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
                    if (minutes <= 0) {
                        minutes += 24 * 60; // Handle overnight windows
                        if (minutes <= 0) {
                            Toast.makeText(requireContext(), 
                                "End time must be after start time", 
                                Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else {
                    startTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                    endTime = startTime;
                }

                // Check if user has reached rule limit
                if (!isPremium && schedulingManager.getRules().size() >= 2) {
                    Toast.makeText(requireContext(), 
                        "Upgrade to Premium for unlimited rules", 
                        Toast.LENGTH_LONG).show();
                    return;
                }
                
                TextInputEditText noteInput = dialogView.findViewById(R.id.noteInput);
                String note = noteInput.getText() != null ? noteInput.getText().toString().trim() : "";

                NotificationRule rule = new NotificationRule(
                        new Random().nextInt(10000),
                        startTime,
                        endTime,
                        isWindowBased
                );
                rule.setNote(note);
                
                schedulingManager.saveRule(rule);
                updateRulesList();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void showTimePickerDialog(TextInputEditText timeInput) {
        // Get current time from input if it exists
        LocalTime currentTime = LocalTime.of(12, 0);
        if (timeInput.getText() != null && !timeInput.getText().toString().isEmpty()) {
            try {
                currentTime = LocalTime.parse(timeInput.getText().toString(), 
                    DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception e) {
                // Use default time if parsing fails
            }
        }

        MaterialTimePicker picker = new MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)  // Always use 12-hour format
            .setHour(currentTime.getHour())
            .setMinute(currentTime.getMinute())
            .setTitleText("Select time")
            .build();

        picker.addOnPositiveButtonClickListener(v -> {
            LocalTime selectedTime = LocalTime.of(picker.getHour(), picker.getMinute());
            String formattedTime = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            timeInput.setText(formattedTime);
            
            updateWindowDuration();
        });

        picker.show(getChildFragmentManager(), "time_picker");
    }

    private void updateWindowDuration() {
        TextInputEditText startInput = dialogView.findViewById(R.id.startTimeInput);
        TextInputEditText endInput = dialogView.findViewById(R.id.endTimeInput);
        TextView durationText = dialogView.findViewById(R.id.windowDurationText);

        if (startInput.getText() != null && endInput.getText() != null &&
            startInput.getText().length() > 0 && endInput.getText().length() > 0) {
            LocalTime startTime = LocalTime.parse(startInput.getText().toString(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(endInput.getText().toString(), DateTimeFormatter.ofPattern("HH:mm"));
            
            long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
            if (minutes < 0) minutes += 24 * 60; // Handle overnight windows
            
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            
            String duration = String.format("Window duration: %d hours%s",
                hours,
                remainingMinutes > 0 ? " " + remainingMinutes + " minutes" : "");
            durationText.setText(duration);
        }
    }
} 