package com.example.nurtura.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.R;
import com.example.nurtura.adapter.VaccineScheduleAdapter;
import com.example.nurtura.model.Child;
import com.example.nurtura.model.Immunization;
import com.example.nurtura.repository.ChildRepository;
import com.example.nurtura.repository.ImmunizationRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MilestoneFragment extends Fragment {

    private RecyclerView recyclerView;
    private VaccineScheduleAdapter adapter;
    private List<Immunization> immunizationList = new ArrayList<>();
    private ChipGroup chipGroupChildren;
    private ChildRepository childRepository;
    private ImmunizationRepository immunizationRepository;
    private List<Child> childList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_milestone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerAllVaccines);
        chipGroupChildren = view.findViewById(R.id.chipGroupChildren);

        childRepository = new ChildRepository();
        immunizationRepository = new ImmunizationRepository();

        setupRecyclerView();
        loadChildren();

    }

    private void loadChildren() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        childRepository.getChildrenByParentId(uid, new ChildRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<Child> children) {
                childList = children;
                populateChips(children);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "no children found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateChips(List<Child> children) {
        chipGroupChildren.removeAllViews();

        for (Child child : children) {
            Chip chip = new Chip(getContext());
            chip.setText(child.getName());
            chip.setCheckable(true);
            chip.setClickable(true);

            chip.setChipBackgroundColorResource(R.color.bg_chip_state_list);
            chip.setTextColor(ContextCompat.getColor(getContext(), R.color.slate_navy));

            chipGroupChildren.addView(chip);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    loadDataFromFirebase(child.getId());
                }
            });
        }

        if (!children.isEmpty()) {
            ((Chip) chipGroupChildren.getChildAt(0)).setChecked(true);
        }
    }

    private void loadDataFromFirebase(String childId) {
        immunizationRepository.getImmunizations(childId, new ImmunizationRepository.ImmunizationCallback() {
            @Override
            public void onSuccess(List<Immunization> records) {
                updateData(records);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error loading schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new VaccineScheduleAdapter(getContext(), immunizationList);
        recyclerView.setAdapter(adapter);
    }

    private void updateData(List<Immunization> newData) {
        immunizationList.clear();
        immunizationList.addAll(newData);
        adapter.notifyDataSetChanged();
    }


}
