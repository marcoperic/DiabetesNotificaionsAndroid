package com.mperic.diabetesnotificaions.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mperic.diabetesnotificaions.R;
import com.mperic.diabetesnotificaions.model.NotificationRule;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class RulesAdapter extends RecyclerView.Adapter<RulesAdapter.RuleViewHolder> {
    private List<NotificationRule> rules;
    private final OnRuleActionListener listener;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

    public interface OnRuleActionListener {
        void onRuleEnabled(NotificationRule rule, boolean enabled);
        void onRuleDeleted(NotificationRule rule);
        void onRuleClicked(NotificationRule rule);
    }

    public RulesAdapter(List<NotificationRule> rules, OnRuleActionListener listener) {
        this.rules = new ArrayList<>(rules);
        this.listener = listener;
    }

    public void updateRules(List<NotificationRule> newRules) {
        this.rules = new ArrayList<>(newRules);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification_rule, parent, false);
        return new RuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RuleViewHolder holder, int position) {
        NotificationRule rule = rules.get(position);
        
        // Set card background color on the MaterialCardView
        ((MaterialCardView) holder.itemView).setCardBackgroundColor(rule.getColor());
        
        if (rule.isWindowBased()) {
            holder.timeText.setText(String.format("%s - %s",
                    rule.getStartTime().format(TIME_FORMATTER),
                    rule.getEndTime().format(TIME_FORMATTER)));
            holder.typeText.setText("Time Window");
        } else {
            holder.timeText.setText(rule.getStartTime().format(TIME_FORMATTER));
            holder.typeText.setText("Exact Time");
        }

        // Handle note visibility and text
        if (rule.getNote() != null && !rule.getNote().isEmpty()) {
            holder.noteText.setVisibility(View.VISIBLE);
            holder.noteText.setText(rule.getNote());
        } else {
            holder.noteText.setVisibility(View.GONE);
        }

        holder.enableSwitch.setChecked(rule.isEnabled());
        holder.enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            listener.onRuleEnabled(rule, isChecked));
        holder.deleteButton.setOnClickListener(v -> listener.onRuleDeleted(rule));

        // Make the whole card clickable
        holder.itemView.setOnClickListener(v -> listener.onRuleClicked(rule));
    }

    @Override
    public int getItemCount() {
        return rules.size();
    }

    static class RuleViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        TextView typeText;
        TextView noteText;
        SwitchMaterial enableSwitch;
        ImageButton deleteButton;

        RuleViewHolder(View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.timeText);
            typeText = itemView.findViewById(R.id.typeText);
            noteText = itemView.findViewById(R.id.noteText);
            enableSwitch = itemView.findViewById(R.id.enableSwitch);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 