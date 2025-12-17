package com.example.nurtura.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nurtura.MotherDetailActivity;
import com.example.nurtura.R;
import com.example.nurtura.auth.UserRepository;

import java.util.Map;

public class SearchFragment extends Fragment {
    EditText etEmail;
    Button btnSearch;
    UserRepository userRepository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        userRepository = new UserRepository();

        etEmail = view.findViewById(R.id.etSearchEmail);
        btnSearch = view.findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Please enter an email", Toast.LENGTH_SHORT).show();
                return;
            }
            userRepository.getUserByEmail(email, new UserRepository.UserCallback() {
                @Override
                public void onSuccess(Map<String, Object> userData) {
                    Toast.makeText(requireContext(),
                            "User found", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(requireContext(), MotherDetailActivity.class);
                    intent.putExtra("uid", (String) userData.get("uid"));
                    startActivity(intent);
                }

                @Override
                public void onNotFound() {
                    Toast.makeText(requireContext(),
                            "User not found", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(requireContext(),
                            "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Firestore", "Error fetching user", e);
                }
            });
        });

        return view;
    }
}