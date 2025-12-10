package com.example.nurtura.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.nurtura.ScheduleActivity;
import com.example.nurtura.adapter.ImmunizationAdapter;
import com.example.nurtura.model.Immunization;
import com.example.nurtura.model.User;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvImmunizations;
    private ImmunizationAdapter adapter;
    private List<Immunization> immunizationList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MaterialCardView btnPanic = view.findViewById(R.id.btnPanic);
        MaterialCardView cardChatMedic = view.findViewById(R.id.cardChatMedic);
        MaterialCardView cardHealthRubric = view.findViewById(R.id.cardHealthRubric);
        TextView tvWelcome = view.findViewById(R.id.tvWelcome);
        TextView tvInitials = view.findViewById(R.id.tvInitials);
        rvImmunizations = view.findViewById(R.id.rvImmunizations);

        db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
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

        setupImmunizationList();

        btnPanic.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Panic Call Initiated! (Demo)", Toast.LENGTH_LONG).show();
        });

        cardChatMedic.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening Chat Medic...", Toast.LENGTH_SHORT).show();
        });

        cardHealthRubric.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ArticleActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void setupImmunizationList() {
        immunizationList = new ArrayList<>();
        immunizationList.add(new Immunization(
                "DTP/HB/HiB",
                "Scheduled for Month 6",
                "Due in 5 Days",
                "31 December 2025"
        ));

        immunizationList.add(new Immunization(
                "Polio (IPV)",
                "Scheduled for Month 9",
                "Due in 3 Months",
                "15 March 2026"
        ));

        adapter = new ImmunizationAdapter(getContext(), immunizationList);
        rvImmunizations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvImmunizations.setAdapter(adapter);
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "";

        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            if (parts[0].length() > 1) {
                return parts[0].substring(0, 2).toUpperCase();
            } else {
                return parts[0].toUpperCase();
            }
        } else {
            String firstInitial = String.valueOf(parts[0].charAt(0));
            String lastInitial = String.valueOf(parts[1].charAt(0));
            return (firstInitial + lastInitial).toUpperCase();
        }
    }
}
