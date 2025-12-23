package com.example.nurtura;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nurtura.databinding.ActivityAddVaccineBinding;
import com.example.nurtura.model.Vaccine;
import com.example.nurtura.repository.VaccineRepository;

public class AddVaccineActivity extends AppCompatActivity {
    private ActivityAddVaccineBinding binding;
    VaccineRepository vaccineRepository;

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
}