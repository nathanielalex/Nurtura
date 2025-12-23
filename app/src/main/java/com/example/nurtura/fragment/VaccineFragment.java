package com.example.nurtura.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nurtura.AddVaccineActivity;
import com.example.nurtura.adapter.VaccineAdapter;
import com.example.nurtura.databinding.FragmentVaccineBinding;
import com.example.nurtura.model.Vaccine;
import com.example.nurtura.repository.VaccineRepository;

import java.util.ArrayList;
import java.util.List;

public class VaccineFragment extends Fragment {
    private FragmentVaccineBinding binding;
    private VaccineAdapter vaccineAdapter;
    private VaccineRepository vaccineRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVaccineBinding.inflate(inflater, container, false);

        vaccineRepository = new VaccineRepository();

        setupRecyclerView();
        setupFab();
        loadVaccines();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerVaccineList.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        vaccineAdapter = new VaccineAdapter(new ArrayList<>(), requireContext());
        binding.recyclerVaccineList.setAdapter(vaccineAdapter);
    }
    private void setupFab() {
        binding.fabAddVaccine.setOnClickListener(v -> {
            startActivity(
                    new Intent(requireContext(), AddVaccineActivity.class)
            );
        });
    }
    private void loadVaccines() {
        vaccineRepository.getVaccines(new VaccineRepository.VaccineCallback() {
            @Override
            public void onSuccess(List<Vaccine> vaccines) {
                vaccineAdapter.updateList(vaccines);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(
                        requireContext(),
                        "Failed to load vaccines",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadVaccines();
    }
}