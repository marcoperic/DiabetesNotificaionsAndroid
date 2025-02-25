package com.mperic.diabetesnotificaions.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
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
import com.mperic.diabetesnotificaions.model.NotificationMessage;
import com.mperic.diabetesnotificaions.util.FactManager;

import java.time.LocalTime;
import java.util.Random;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

public class RulesFragment extends Fragment {
    private RecyclerView rulesRecyclerView;
    private MaterialButton addRuleButton;
    private SchedulingManager schedulingManager;
    private PreferenceManager preferenceManager;
    private View dialogView;
    private RulesAdapter rulesAdapter;
    private TextView greetingText;

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
        greetingText = view.findViewById(R.id.greetingText);

        // Set up RecyclerView
        rulesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rulesAdapter = new RulesAdapter(
            schedulingManager.getRules(), 
            new RulesAdapter.OnRuleActionListener() {
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

                @Override
                public void onRuleClicked(NotificationRule rule) {
                    showEditRuleDialog(rule);
                }
            },
            requireContext()
        );
        
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
        // Set greeting

        greetingText.setText(FactManager.getGreeting());
        
        // Set random fact
        FactManager.Fact fact = FactManager.getRandomFact();
        factText.setText(fact.getContent());
        factCategory.setText(fact.getCategory());
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
        CheckBox categoryFact = dialogView.findViewById(R.id.categoryFact);
        CheckBox categoryScary = dialogView.findViewById(R.id.categoryScary);
        CheckBox categoryMotivation = dialogView.findViewById(R.id.categoryMotivation);
        
        // Set initial states based on settings
        boolean isPremium = preferenceManager.isPremium();

        // For non-premium users, only FACT is available
        categoryFact.setEnabled(true);
        categoryFact.setChecked(preferenceManager.isCategoryEnabled(NotificationMessage.Category.FACT));

        // Other categories depend on premium status
        categoryHealth.setEnabled(isPremium);
        categoryHealth.setChecked(preferenceManager.isCategoryEnabled(NotificationMessage.Category.HEALTH));

        categoryScary.setEnabled(isPremium);
        categoryScary.setChecked(preferenceManager.isCategoryEnabled(NotificationMessage.Category.SCARY));

        categoryMotivation.setEnabled(isPremium);
        categoryMotivation.setChecked(preferenceManager.isCategoryEnabled(NotificationMessage.Category.MOTIVATION));

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

        CheckBox useNoteAsNotification = dialogView.findViewById(R.id.useNoteAsNotification);
        
        useNoteAsNotification.setChecked(false);
        
        useNoteAsNotification.setOnClickListener(v -> {
            if (!isPremium && ((CheckBox)v).isChecked()) {
                ((CheckBox)v).setChecked(false);
                Toast.makeText(requireContext(), 
                    R.string.premium_required_note, 
                    Toast.LENGTH_SHORT).show();
            }
        });

        CheckBox showNoteAsPrimary = dialogView.findViewById(R.id.showNoteAsPrimary);
        TextView showNoteHint = dialogView.findViewById(R.id.showNoteHint);
        
        boolean globalPreference = preferenceManager.isNoteTimeSwapped();
        showNoteAsPrimary.setChecked(globalPreference);

        if (globalPreference) {
            showNoteHint.setText("Note display is enabled globally in settings");
            showNoteHint.setVisibility(View.VISIBLE);
        } else {
            showNoteHint.setVisibility(View.GONE);
        }

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
                rule.setUseNoteAsNotification(useNoteAsNotification.isChecked(), isPremium);
                rule.setShowNoteAsPrimary(showNoteAsPrimary.isChecked());
                
                Set<NotificationMessage.Category> selectedCategories = new HashSet<>();
                if (categoryHealth.isChecked()) selectedCategories.add(NotificationMessage.Category.HEALTH);
                if (categoryFact.isChecked()) selectedCategories.add(NotificationMessage.Category.FACT);
                if (categoryScary.isChecked()) selectedCategories.add(NotificationMessage.Category.SCARY);
                if (categoryMotivation.isChecked()) selectedCategories.add(NotificationMessage.Category.MOTIVATION);

                rule.setEnabledCategories(selectedCategories);
                
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

    private void showEditRuleDialog(NotificationRule rule) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_rule, null);
        TextInputEditText noteInput = dialogView.findViewById(R.id.noteInput);
        RadioGroup colorRadioGroup = dialogView.findViewById(R.id.colorRadioGroup);
        View colorSection = dialogView.findViewById(R.id.colorSection);
        TextView colorPremiumText = dialogView.findViewById(R.id.colorPremiumText);
        
        // Set current values
        noteInput.setText(rule.getNote());
        
        boolean isPremium = preferenceManager.isPremium();
        
        // Handle color selection visibility
        colorSection.setVisibility(View.VISIBLE); // Always show colors
        colorPremiumText.setVisibility(isPremium ? View.GONE : View.VISIBLE);
        
        // Disable radio buttons for non-premium users
        for (int i = 0; i < colorRadioGroup.getChildCount(); i++) {
            colorRadioGroup.getChildAt(i).setEnabled(isPremium);
        }

        CheckBox showNoteAsPrimary = dialogView.findViewById(R.id.showNoteAsPrimary);
        TextView showNoteHint = dialogView.findViewById(R.id.showNoteHint);
        boolean globalPreference = preferenceManager.isNoteTimeSwapped();
        showNoteAsPrimary.setChecked(rule.isShowNoteAsPrimary() || globalPreference);

        if (globalPreference) {
            showNoteHint.setText("Note display is enabled globally in settings");
            showNoteHint.setVisibility(View.VISIBLE);
        } else {
            showNoteHint.setVisibility(View.GONE);
        }
        
        // Get color resources
        int blueColor = requireContext().getColor(R.color.rule_color_blue);
        int greenColor = requireContext().getColor(R.color.rule_color_green);
        int purpleColor = requireContext().getColor(R.color.rule_color_purple);
        int orangeColor = requireContext().getColor(R.color.rule_color_orange);
        
        // Set current color
        if (isPremium) {
            int currentColor = rule.getColor();
            if (currentColor == blueColor) {
                colorRadioGroup.check(R.id.colorBlue);
            } else if (currentColor == greenColor) {
                colorRadioGroup.check(R.id.colorGreen);
            } else if (currentColor == purpleColor) {
                colorRadioGroup.check(R.id.colorPurple);
            } else if (currentColor == orangeColor) {
                colorRadioGroup.check(R.id.colorOrange);
            } else {
                colorRadioGroup.check(R.id.colorDefault);
            }
        }
        
        CheckBox useNoteAsNotification = dialogView.findViewById(R.id.useNoteAsNotification);
        
        useNoteAsNotification.setChecked(rule.isUseNoteAsNotification() && isPremium);
        
        useNoteAsNotification.setOnClickListener(v -> {
            if (!isPremium && ((CheckBox)v).isChecked()) {
                ((CheckBox)v).setChecked(false);
                Toast.makeText(requireContext(), 
                    R.string.premium_required_note, 
                    Toast.LENGTH_SHORT).show();
            }
        });
        
        // Initialize category views
        View categoriesHeader = dialogView.findViewById(R.id.categoriesHeader);
        View categoriesContent = dialogView.findViewById(R.id.categoriesContent);
        ImageView expandIcon = dialogView.findViewById(R.id.expandIcon);
        
        // Category checkboxes
        CheckBox categoryHealth = dialogView.findViewById(R.id.categoryHealth);
        CheckBox categoryFact = dialogView.findViewById(R.id.categoryFact);
        CheckBox categoryScary = dialogView.findViewById(R.id.categoryScary);
        CheckBox categoryMotivation = dialogView.findViewById(R.id.categoryMotivation);
        
        // Set initial states based on rule's categories
        Set<NotificationMessage.Category> ruleCategories = rule.getEnabledCategories();
        
        categoryFact.setChecked(ruleCategories.contains(NotificationMessage.Category.FACT));
        categoryHealth.setChecked(ruleCategories.contains(NotificationMessage.Category.HEALTH));
        categoryScary.setChecked(ruleCategories.contains(NotificationMessage.Category.SCARY));
        categoryMotivation.setChecked(ruleCategories.contains(NotificationMessage.Category.MOTIVATION));
        
        // Handle premium status
        categoryFact.setEnabled(true);
        categoryHealth.setEnabled(isPremium);
        categoryScary.setEnabled(isPremium);
        categoryMotivation.setEnabled(isPremium);
        
        // Setup categories expansion
        categoriesHeader.setOnClickListener(v -> {
            boolean isExpanded = categoriesContent.getVisibility() == View.VISIBLE;
            categoriesContent.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            expandIcon.setRotation(isExpanded ? 0 : 180);
        });

        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.RoundedDialog)
                .setTitle("Edit Rule")
                .setView(dialogView)
                .setPositiveButton("Save", (dialogInterface, i) -> {
                    // Save note
                    rule.setNote(noteInput.getText() != null ? 
                        noteInput.getText().toString() : "");

                    // Save color if premium
                    if (isPremium) {
                        int selectedColor;
                        int checkedId = colorRadioGroup.getCheckedRadioButtonId();
                        if (checkedId == R.id.colorBlue) {
                            selectedColor = blueColor;
                        } else if (checkedId == R.id.colorGreen) {
                            selectedColor = greenColor;
                        } else if (checkedId == R.id.colorPurple) {
                            selectedColor = purpleColor;
                        } else if (checkedId == R.id.colorOrange) {
                            selectedColor = orangeColor;
                        } else {
                            selectedColor = Color.WHITE;
                        }
                        rule.setColor(selectedColor);
                    }

                    rule.setUseNoteAsNotification(useNoteAsNotification.isChecked(), isPremium);
                    rule.setShowNoteAsPrimary(showNoteAsPrimary.isChecked());

                    // Save categories
                    Set<NotificationMessage.Category> selectedCategories = new HashSet<>();
                    if (categoryHealth.isChecked()) selectedCategories.add(NotificationMessage.Category.HEALTH);
                    if (categoryFact.isChecked()) selectedCategories.add(NotificationMessage.Category.FACT);
                    if (categoryScary.isChecked()) selectedCategories.add(NotificationMessage.Category.SCARY);
                    if (categoryMotivation.isChecked()) selectedCategories.add(NotificationMessage.Category.MOTIVATION);
                    
                    rule.setEnabledCategories(selectedCategories);

                    schedulingManager.saveRule(rule);
                    updateRulesList();
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }
} 