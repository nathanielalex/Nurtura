package com.example.nurtura.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nurtura.R;
import com.example.nurtura.adapter.ChildAdapter;
import com.example.nurtura.auth.AuthRepository;
import com.example.nurtura.auth.UserRepository;
import com.example.nurtura.model.Child;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;


public class StaffProfileFragment extends Fragment {
    FirebaseFirestore db;
    AuthRepository authRepository;
    UserRepository userRepository;

    TextView txtStaffName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_profile, container, false);
        authRepository = new AuthRepository();

        Button signOutButton = view.findViewById(R.id.btnSignOut);
        signOutButton.setOnClickListener(v -> authRepository.signOut(requireContext()));

        txtStaffName = view.findViewById(R.id.txtStaffName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userRepository = new UserRepository();

        userRepository.getUserByUid(user.getUid(), new UserRepository.UserCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                String name = userData.get("name").toString();
                txtStaffName.setText(name);
            }

            @Override
            public void onNotFound() {
                txtStaffName.setText("User not found");
                Log.w("UserCallback", "User with UID " + user.getUid() + " not found.");
                Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                txtStaffName.setText("Error loading user");
                Log.e("UserCallback", "Failed to get user data", e);
                Toast.makeText(getContext(), "Error loading user data", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}