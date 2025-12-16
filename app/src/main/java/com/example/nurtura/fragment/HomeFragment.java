package com.example.nurtura.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.ArticleActivity;
import com.example.nurtura.R;
import com.example.nurtura.adapter.ImmunizationAdapter;
import com.example.nurtura.model.Child;
import com.example.nurtura.model.Immunization;
import com.example.nurtura.model.User;
import com.example.nurtura.model.Vaccine;
import com.example.nurtura.repository.ChildRepository;
import com.example.nurtura.repository.VaccineRepository;
import com.example.nurtura.utils.ImmunizationUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private RecyclerView rvImmunizations;
    private ImmunizationAdapter adapter;
    private TextView tvWelcome, tvInitials;

    private VaccineRepository vaccineRepository;
    private ChildRepository childRepository;
    private FirebaseFirestore db;

    private List<Vaccine> loadedVaccines = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MaterialCardView btnPanic = view.findViewById(R.id.btnPanic);
        MaterialCardView cardChatMedic = view.findViewById(R.id.cardChatMedic);
        MaterialCardView cardHealthRubric = view.findViewById(R.id.cardHealthRubric);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvInitials = view.findViewById(R.id.tvInitials);

        rvImmunizations = view.findViewById(R.id.rvImmunizations);
        rvImmunizations.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ImmunizationAdapter(getContext(), new ArrayList<>());
        rvImmunizations.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        vaccineRepository = new VaccineRepository();
        childRepository = new ChildRepository();

        btnPanic.setOnClickListener(v -> {
            String emergencyNumber = "112";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + emergencyNumber));
            startActivity(intent);
        });

        cardChatMedic.setOnClickListener(v ->
                Toast.makeText(getContext(), "Chat feature not available.", Toast.LENGTH_SHORT).show()
        );

        cardHealthRubric.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ArticleActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;

        loadUserProfile(firebaseUser);

        if (loadedVaccines.isEmpty()) {
            vaccineRepository.getVaccines(new VaccineRepository.VaccineCallback() {
                @Override
                public void onSuccess(List<Vaccine> vaccines) {
                    if (vaccines == null || vaccines.isEmpty()) {
                        Log.e(TAG, "Critical: No vaccines found in Firestore.");
                        return;
                    }
                    loadedVaccines = vaccines;
                    fetchChildAndCalculate(firebaseUser.getUid());
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to fetch vaccines", e);
                    Toast.makeText(getContext(), "Error loading schedule configuration.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            fetchChildAndCalculate(firebaseUser.getUid());
        }
    }

    private void fetchChildAndCalculate(String parentUid) {
        childRepository.getChildrenByParentId(parentUid, new ChildRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<Child> children) {
                if (children.isEmpty()) {
                    adapter = new ImmunizationAdapter(getContext(), new ArrayList<>());
                    rvImmunizations.setAdapter(adapter);
                    return;
                }

                Child currentChild = children.get(0);

                if (currentChild.getDateOfBirth() == null) {
                    Log.e(TAG, "Child data corrupt: Missing Date of Birth.");
                    return;
                }

                List<Immunization> schedule = ImmunizationUtils.generateSchedule(
                        currentChild.getDateOfBirth(),
                        loadedVaccines
                );

                adapter = new ImmunizationAdapter(getContext(), schedule);
                rvImmunizations.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Failed to load child data", e);
            }
        });
    }

    private void loadUserProfile(FirebaseUser firebaseUser) {
        db.collection("users").document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null && user.getName() != null) {
                            String fullName = user.getName();
                            String firstName = fullName.split(" ")[0];
                            tvWelcome.setText("Hello, " + firstName + "!");
                            tvInitials.setText(getInitials(fullName));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (firebaseUser.getDisplayName() != null) {
                        tvWelcome.setText("Hello, " + firebaseUser.getDisplayName());
                        tvInitials.setText(getInitials(firebaseUser.getDisplayName()));
                    }
                });
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].length() > 1 ? parts[0].substring(0, 2).toUpperCase() : parts[0].toUpperCase();
        } else {
            String firstInitial = String.valueOf(parts[0].charAt(0));
            String lastInitial = String.valueOf(parts[1].charAt(0));
            return (firstInitial + lastInitial).toUpperCase();
        }
    }
}
