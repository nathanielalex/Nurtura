package com.example.nurtura.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nurtura.AddChildActivity;
import com.example.nurtura.EditProfile;
import com.example.nurtura.LoginActivity;
import com.example.nurtura.R;
import com.example.nurtura.auth.UserRepository;
import com.example.nurtura.model.Child;
import com.example.nurtura.repository.ChildRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView txtMotherName, tvKnownAllergies;
    private Button btnEditChildInfo;
    private ChipGroup chipGroupChildren;
    private ChildRepository childRepository;
    private Child currentSelectedChild;
    private UserRepository userRepository;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtMotherName = view.findViewById(R.id.txtMotherName);
        tvKnownAllergies = view.findViewById(R.id.tvKnownAllergies);
        btnEditChildInfo = view.findViewById(R.id.btnEditChildInfo);
        chipGroupChildren = view.findViewById(R.id.chipGroupChildren);

        Button btnEditMother = view.findViewById(R.id.btnEditMother);
        Button btnAddChild = view.findViewById(R.id.btnAddChild);
        Button btnSignOut = view.findViewById(R.id.btnSignOut);

        childRepository = new ChildRepository();
        userRepository = new UserRepository();

        btnEditMother.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfile.class)));
        btnAddChild.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddChildActivity.class)));

        btnSignOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            if (getActivity() != null) getActivity().finish();
        });

        btnEditChildInfo.setOnClickListener(v -> {
            if (currentSelectedChild != null) {
                Intent intent = new Intent(getActivity(), AddChildActivity.class);
                intent.putExtra("CHILD_ID", currentSelectedChild.getId()); // Pass ID to edit
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Please select a child first", Toast.LENGTH_SHORT).show();
            }
        });

        loadUserData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            txtMotherName.setText("User not logged in");
            return;
        }

        String userId = user.getUid();

        userRepository.getUserByUid(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                Object name = userData.get("name");
                txtMotherName.setText(name != null ? name.toString() : "No name");
            }

            @Override
            public void onNotFound() {
                txtMotherName.setText("User not found");
            }

            @Override
            public void onFailure(Exception e) {
                txtMotherName.setText("Error");
                Toast.makeText(getContext(), "Error loading user", Toast.LENGTH_SHORT).show();
            }
        });

        loadChildren(userId);
    }

    private void loadChildren(String parentId) {
        childRepository.getChildrenByParentId(parentId, new ChildRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<Child> children) {
                chipGroupChildren.removeAllViews();

                if (children.isEmpty()) {
                    tvKnownAllergies.setText("No children added yet.");
                    btnEditChildInfo.setEnabled(false);
                    return;
                }

                btnEditChildInfo.setEnabled(true);

                for (Child child : children) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(child.getName());
                    chip.setCheckable(true);
                    chip.setTag(child);

                    chip.setChipBackgroundColorResource(android.R.color.transparent);
                    chip.setChipStrokeColorResource(R.color.hot_pink);
                    chip.setChipStrokeWidth(3f);
                    chip.setTextColor(getResources().getColor(R.color.slate_navy));

                    chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            updateChildUI((Child) buttonView.getTag());
                            chip.setChipBackgroundColorResource(R.color.hot_pink);
                            chip.setTextColor(getResources().getColor(R.color.white));
                        } else {
                            chip.setChipBackgroundColorResource(android.R.color.transparent);
                            chip.setTextColor(getResources().getColor(R.color.slate_navy));
                        }
                    });

                    chipGroupChildren.addView(chip);
                }

                if (chipGroupChildren.getChildCount() > 0) {
                    ((Chip) chipGroupChildren.getChildAt(0)).setChecked(true);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load children", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateChildUI(Child child) {
        this.currentSelectedChild = child;

        List<String> allergies = child.getAllergies();
        if (allergies != null && !allergies.isEmpty()) {
            // Join list into a string like "Peanuts, Dust, Milk"
            tvKnownAllergies.setText(TextUtils.join(", ", allergies));
        } else {
            tvKnownAllergies.setText("None");
        }
    }
}
