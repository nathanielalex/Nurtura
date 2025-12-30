package com.example.nurtura;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nurtura.databinding.ActivityAddVaccineBinding;
import com.example.nurtura.model.Child;
import com.example.nurtura.model.Immunization;
import com.example.nurtura.model.Vaccine;
import com.example.nurtura.repository.ChildRepository;
import com.example.nurtura.repository.VaccineRepository;
import com.example.nurtura.utils.ImmunizationUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddVaccineActivity extends AppCompatActivity {
    private ActivityAddVaccineBinding binding;
    private VaccineRepository vaccineRepository;
    private ChildRepository childRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddVaccineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vaccineRepository = new VaccineRepository();
        childRepository = new ChildRepository();

        binding.btnSaveVaccine.setOnClickListener(v -> {
            String vaccineName = binding.etVaccineName.getText() != null
                    ? binding.etVaccineName.getText().toString().trim() : "";
            String recommendedAgeStr = binding.etRecommendedAge.getText() != null
                    ? binding.etRecommendedAge.getText().toString().trim() : "";
            String description = binding.etDescription.getText() != null
                    ? binding.etDescription.getText().toString().trim() : "";
            String sideEffects = binding.etSideEffects.getText() != null
                    ? binding.etSideEffects.getText().toString().trim() : "";
            boolean isMandatory = binding.swMandatory.isChecked();

            if (vaccineName.isEmpty()) {
                binding.etVaccineName.setError("Vaccine name is required");
                binding.etVaccineName.requestFocus();
                return;
            }

            if (recommendedAgeStr.isEmpty()) {
                binding.etRecommendedAge.setError("Recommended age is required");
                binding.etRecommendedAge.requestFocus();
                return;
            }

            int recommendedAge;
            try {
                recommendedAge = Integer.parseInt(recommendedAgeStr);
                if (recommendedAge < 0) {
                    binding.etRecommendedAge.setError("Age cannot be negative");
                    binding.etRecommendedAge.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                binding.etRecommendedAge.setError("Enter a valid number");
                binding.etRecommendedAge.requestFocus();
                return;
            }

            Vaccine vaccine = new Vaccine("0", vaccineName, description, recommendedAge,
                    isMandatory, sideEffects);

            vaccineRepository.insertVaccine(vaccine, new VaccineRepository.InsertCallback() {
                @Override
                public void onSuccess() {
                    childRepository.getAllChildren(new ChildRepository.ChildrenCallback() {
                        @Override
                        public void onSuccess(List<Child> children) {
                            for (Child child: children) {
                                Date dob = child.getDateOfBirth();
                                Immunization schedule = ImmunizationUtils.generateSingularSchedule(dob, vaccine);
                                Map<String, Object> map = new HashMap<>();
                                map.put("vaccineName", schedule.getName());
                                map.put("dueDate", schedule.getDate() != null ? parseDate(schedule.getDate()) : null);
                                map.put("status", "Scheduled");
                                childRepository.saveSingularSchedule(child.getId(), map);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(AddVaccineActivity.this, "Failed to update immunization schedules",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText(AddVaccineActivity.this, "Successfully inserted vaccine", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AddVaccineActivity.this, "Failed to insert vaccine", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(dateStr);
        } catch (Exception e) {
            return new Date();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}