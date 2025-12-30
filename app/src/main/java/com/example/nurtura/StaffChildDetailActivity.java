package com.example.nurtura;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nurtura.model.Child;
import com.example.nurtura.repository.ChildRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StaffChildDetailActivity extends AppCompatActivity {
    private LinearLayout toolbarArea;
    private ShapeableImageView imgChildAvatar;
    private TextView txtChildName;
    private TextView txtGender;
    private TextView txtDob;
    private TextView txtWeightValue;
    private TextView txtHeightValue;
    private TextView txtBloodType, txtBloodTypeIcon;
    private ChipGroup chipGroupAllergies;
    private ChildRepository childRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_child_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String childId = getIntent().getStringExtra("EXTRA_CHILD_ID");

        if (childId == null || childId.isEmpty()) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        childRepository = new ChildRepository();
        loadData(childId);
    }

    private void initViews() {
        toolbarArea = findViewById(R.id.toolbarArea);
        imgChildAvatar = findViewById(R.id.imgChildAvatar);
        txtChildName = findViewById(R.id.txtChildName);
        txtGender = findViewById(R.id.txtGender);
        txtDob = findViewById(R.id.txtDob);
        txtWeightValue = findViewById(R.id.txtWeightValue);
        txtHeightValue = findViewById(R.id.txtHeightValue);
        txtBloodType = findViewById(R.id.txtBloodType);
        txtBloodTypeIcon = findViewById(R.id.txtBloodTypeIcon);
        chipGroupAllergies = findViewById(R.id.chipGroupAllergies);
    }

    private void setupToolbar() {
        ImageView btnBack = (ImageView) toolbarArea.getChildAt(0);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadData(String childId) {
        childRepository.getChildById(childId, new ChildRepository.ChildCallback() {
            @Override
            public void onSuccess(Child child) {
                txtChildName.setText(child.getName());
                txtGender.setText(child.getGender());

                Date dob = child.getDateOfBirth();
                if (dob != null) {
                    SimpleDateFormat sdf =
                            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    txtDob.setText(sdf.format(dob));
                }

                txtWeightValue.setText(
                        String.format(Locale.getDefault(), "%.1f kg", child.getLatestWeight())
                );

                txtHeightValue.setText(
                        String.format(Locale.getDefault(), "%.1f cm", child.getLatestHeight())
                );

                txtBloodType.setText(child.getBloodType());
                txtBloodTypeIcon.setText(child.getBloodType());

                chipGroupAllergies.removeAllViews();
                List<String> allergies = child.getAllergies();
                if (allergies != null && !allergies.isEmpty()) {
                    for (String allergy : allergies) {
                        Chip chip = new Chip(StaffChildDetailActivity.this);
                        chip.setText(allergy);
                        chip.setCheckable(false);
                        chip.setClickable(false);
                        chip.setTextColor(getColor(R.color.text_primary));
                        chip.setChipBackgroundColorResource(R.color.surface_variant);
                        chip.setChipStrokeColorResource(R.color.accent_primary);
                        chip.setChipStrokeWidth(1f);
                        chipGroupAllergies.addView(chip);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(StaffChildDetailActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

    }
}