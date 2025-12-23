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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
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

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            authRepository.loginUser(this, email, password, (success, userId) -> {
                if(success) {
                    Log.d(TAG, "signInWithEmail:success");
                    fetchRoleAndNavigate(userId);
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            fetchRoleAndNavigate(currentUser.getUid());
        }
    }

    private void fetchRoleAndNavigate(String userId) {
        userRepository.getRole(userId, new UserRepository.RoleCallback() {
            @Override
            public void onRoleReceived(String role) {
                if (role != null) {
                    navigateToDashboard(role);
                } else {
                    Log.e(TAG, "Role is null for user: " + userId);
                    Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        if ("staff".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, StaffActivity.class);
        } else if ("patient".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        } else {
            Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
            return;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}