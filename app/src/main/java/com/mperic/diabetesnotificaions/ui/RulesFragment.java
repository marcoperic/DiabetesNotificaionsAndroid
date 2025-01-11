package com.mperic.diabetesnotificaions.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mperic.diabetesnotificaions.R;
import com.google.android.material.button.MaterialButton;

public class RulesFragment extends Fragment {
    private RecyclerView rulesRecyclerView;
    private MaterialButton addRuleButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rules, container, false);
        
        rulesRecyclerView = view.findViewById(R.id.rulesRecyclerView);
        rulesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        addRuleButton = view.findViewById(R.id.addRuleButton);
        addRuleButton.setOnClickListener(v -> showAddRuleDialog());
        
        return view;
    }

    private void showAddRuleDialog() {
        // TODO: Implement add rule dialog
    }
} 