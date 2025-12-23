package com.example.nurtura.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nurtura.R;
import com.example.nurtura.databinding.FragmentVaccineBinding;
import com.example.nurtura.model.Vaccine;
import com.example.nurtura.repository.VaccineRepository;

public class VaccineFragment extends Fragment {
    private FragmentVaccineBinding binding;
    VaccineRepository vaccineRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVaccineBinding.inflate(inflater, container, false);
        vaccineRepository = new VaccineRepository();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                    Toast.makeText(requireContext(), "Successfully inserted vaccine", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(requireContext(), "Failed to insert vaccine", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}