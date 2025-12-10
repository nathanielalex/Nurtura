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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "GoogleSignIn";
    private AuthRepository authRepository;
    private FirebaseAuth mAuth;
    private GoogleSignInManager googleSignInManager;

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
        googleSignInManager = new GoogleSignInManager(this);

        mAuth = FirebaseAuth.getInstance();

        EditText emailField = findViewById(R.id.etEmail);
        EditText passwordField = findViewById(R.id.etPassword);
        Button loginBtn = findViewById(R.id.btnLogin);

        loginBtn.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            authRepository.loginUser(this, email, password, success -> {
                if(success) {
                    Log.d(TAG, "signInWithEmail:success");
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
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
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d(TAG, "Logged in as: " + user.getEmail());
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Log.d(TAG, "User not logged in");
        }
    }

}