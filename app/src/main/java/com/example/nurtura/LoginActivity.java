package com.example.nurtura;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nurtura.auth.AuthRepository;
import com.example.nurtura.auth.GoogleSignInManager;
import com.example.nurtura.auth.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "GoogleSignIn";
    private AuthRepository authRepository;
    private FirebaseAuth mAuth;
    private GoogleSignInManager googleSignInManager;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        googleSignInManager = new GoogleSignInManager(this);

        mAuth = FirebaseAuth.getInstance();

        EditText emailField = findViewById(R.id.etEmail);
        EditText passwordField = findViewById(R.id.etPassword);
        Button loginBtn = findViewById(R.id.btnLogin);

        loginBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            authRepository.loginUser(this, email, password, (success, userId) -> {
                if(success) {
                    Log.d(TAG, "signInWithEmail:success");
                    authorizeRole(userId);
                } else {
                    Log.w(TAG, "signInWithEmail:failure");
                    Toast.makeText(LoginActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        Button signInButton = findViewById(R.id.btnGoogleSignIn);
        signInButton.setOnClickListener(v -> googleSignInManager.signInWithGoogle());

        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void authorizeRole(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");

                        if("staff".equals(role)){
                            Intent intent = new Intent(this, StaffActivity.class);
                            startActivity(intent);
                        } else if("patient".equals(role)) {
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Error: Unknown role", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d(TAG, "Logged in as: " + user.getEmail());

            userRepository.getRole(user.getUid(), new UserRepository.RoleCallback() {
                @Override
                public void onRoleReceived(String role) {
                    if(role.equals("staff")) {
                        startActivity(new Intent(LoginActivity.this, StaffActivity.class));
                    }
                    else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                }
            });

            finish();
        } else {
            Log.d(TAG, "User not logged in");
        }
    }

}