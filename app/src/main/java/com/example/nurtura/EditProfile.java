package com.example.nurtura;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfile extends AppCompatActivity {

    private EditText etFullName, etEmail;
    private Button btnSave;
    private ImageView btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            etFullName.setText(user.getDisplayName());
            etEmail.setText(user.getEmail());
        }

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String newName = etFullName.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            etFullName.setError("Name is required");
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateFirestoreUser(user.getUid(), newName);
                    } else {
                        btnSave.setEnabled(true);
                        btnSave.setText("Save Changes");
                        Toast.makeText(EditProfile.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateFirestoreUser(String uid, String newName) {
        db.collection("users").document(uid)
                .update("name", newName)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfile.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Even if Firestore fails, Auth might have succeeded, but let's warn
                    Toast.makeText(EditProfile.this, "Updated Auth, but failed to sync DB.", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
