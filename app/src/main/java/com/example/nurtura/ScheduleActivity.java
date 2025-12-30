package com.example.nurtura;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.adapter.ImmunizationAdapter;
import com.example.nurtura.model.Child;
import com.example.nurtura.model.Immunization;
import com.example.nurtura.repository.ChildRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView rvScheduleList;
    private ChipGroup chipGroupChildren, chipGroupFilter;
    private LinearLayout layoutEmptyState;
    private TextView tvEmptyMessage;
    private ImmunizationAdapter adapter;
    private ChildRepository childRepository;

    private List<Immunization> allImmunizations = new ArrayList<>();
    private String currentChildId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvScheduleList = findViewById(R.id.rvScheduleList);
        chipGroupChildren = findViewById(R.id.chipGroupChildren);
        chipGroupFilter = findViewById(R.id.chipGroupFilter);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        rvScheduleList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ImmunizationAdapter(this, new ArrayList<>(), (immunization, isChecked) -> {
            if (currentChildId != null) {
                updateImmunizationStatus(immunization, isChecked);
            }
        });
        rvScheduleList.setAdapter(adapter);

        childRepository = new ChildRepository();

        setupFilters();
    }

    private void setupFilters() {
        updateFilterVisuals(chipGroupFilter.getCheckedChipId());

        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                updateFilterVisuals(checkedId);
                applyFilter();
            }
        });
    }

    private void updateFilterVisuals(int checkedId) {
        for (int i = 0; i < chipGroupFilter.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupFilter.getChildAt(i);

            if (chip.getId() == checkedId) {
                chip.setChipBackgroundColorResource(R.color.accent_primary);
                chip.setTextColor(getResources().getColor(R.color.white));
                chip.setChipStrokeWidth(0f);
            } else {
                chip.setChipBackgroundColorResource(R.color.white);
                chip.setTextColor(getResources().getColor(R.color.text_primary));
                chip.setChipStrokeColorResource(R.color.text_primary);
                chip.setChipStrokeWidth(3f);
            }
        }
    }

    private void applyFilter() {
        int checkedId = chipGroupFilter.getCheckedChipId();
        List<Immunization> filteredList = new ArrayList<>();

        String emptyText = "No vaccines found";

        if (checkedId == R.id.chipFilterAll) {
            filteredList.addAll(allImmunizations);
            emptyText = "No vaccine schedule available";
        } else if (checkedId == R.id.chipFilterDone) {
            for (Immunization item : allImmunizations) {
                if (item.isCompleted()) filteredList.add(item);
            }
            emptyText = "No completed vaccines yet";
        } else { // R.id.chipFilterToDo (Default)
            for (Immunization item : allImmunizations) {
                if (!item.isCompleted()) filteredList.add(item);
            }
            emptyText = "Hooray! All caught up.";
        }

        adapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            rvScheduleList.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            tvEmptyMessage.setText(emptyText);
        } else {
            rvScheduleList.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void updateImmunizationStatus(Immunization item, boolean isChecked) {
        childRepository.updateImmunizationStatus(currentChildId, item.getId(), isChecked, new ChildRepository.FirestoreCallback() {
            @Override
            public void onSuccess(String result) {
                item.setCompleted(isChecked);
                applyFilter();
                Toast.makeText(ScheduleActivity.this, isChecked ? "Marked as Done" : "Marked as Not Done", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ScheduleActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChildren();
    }

    private void loadChildren() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        childRepository.getChildrenByParentId(user.getUid(), new ChildRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<Child> children) {
                chipGroupChildren.removeAllViews();

                if (children.isEmpty()) {
                    Toast.makeText(ScheduleActivity.this, "No children found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Child child : children) {
                    Chip chip = new Chip(ScheduleActivity.this);
                    chip.setText(child.getName());
                    chip.setCheckable(true);
                    chip.setTag(child.getId());

                    chip.setChipBackgroundColorResource(android.R.color.transparent);
                    chip.setChipStrokeWidth(3f);
                    chip.setChipStrokeColorResource(R.color.accent_primary);
                    chip.setTextColor(getResources().getColor(R.color.text_primary));

                    chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            currentChildId = (String) buttonView.getTag();
                            loadScheduleForChild(currentChildId);
                            chip.setChipBackgroundColorResource(R.color.accent_primary);
                            chip.setTextColor(getResources().getColor(R.color.white));
                        } else {
                            chip.setChipBackgroundColorResource(android.R.color.transparent);
                            chip.setTextColor(getResources().getColor(R.color.text_primary));
                        }
                    });

                    chipGroupChildren.addView(chip);
                }

                if (chipGroupChildren.getChildCount() > 0) {
                    ((Chip) chipGroupChildren.getChildAt(0)).setChecked(true);
                }
            }
            @Override
            public void onFailure(Exception e) {}
        });
    }

    private void loadScheduleForChild(String childId) {
        childRepository.getImmunizationSchedule(childId, new ChildRepository.ScheduleCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> scheduleData) {
                allImmunizations.clear();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                Date today = new Date();

                for (Map<String, Object> item : scheduleData) {
                    String id = (String) item.get("id");
                    Timestamp ts = (Timestamp) item.get("dueDate");
                    String name = (String) item.get("vaccineName");
                    Boolean isCompleted = (Boolean) item.get("isCompleted");
                    if (isCompleted == null) isCompleted = false;

                    if (ts == null || name == null) continue;

                    Date date = ts.toDate();
                    String status = "Upcoming";

                    long diff = date.getTime() - today.getTime();
                    long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                    if (days < 0) status = "Overdue";
                    else if (days == 0) status = "Due Today";
                    else if (days <= 14) status = "Due in " + days + " days";

                    if (isCompleted) status = "Completed";

                    allImmunizations.add(new Immunization(id, name, "Scheduled", status, sdf.format(date), isCompleted));
                }

                applyFilter();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ScheduleActivity.this, "Failed to load schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
