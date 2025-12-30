package com.example.nurtura;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nurtura.auth.UserRepository;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Map;

public class StaffMotherProfileActivity extends AppCompatActivity {
    private ShapeableImageView imgMotherAvatar;
    private TextView txtMotherName;
    private TextView txtEmail;
    private TextView txtPhone;
    private ImageView btnBack;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_mother_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        userRepository = new UserRepository();
        String motherId = getIntent().getStringExtra("EXTRA_MOTHER_ID");
        if (motherId != null) {
            setData(motherId);
        } else {
            Toast.makeText(this, "Mother ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
        setData(motherId);
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        imgMotherAvatar = findViewById(R.id.imgMotherAvatar);
        txtMotherName = findViewById(R.id.txtMotherName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);

        btnBack = findViewById(R.id.btnBack);

    }

    private void setData(String motherId) {
        userRepository.getUserByUid(motherId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                txtMotherName.setText(userData.get("name").toString());
                txtEmail.setText(userData.get("email").toString());
                txtPhone.setText(userData.get("phoneNumber").toString());
            }

            @Override
            public void onNotFound() {
                Toast.makeText(StaffMotherProfileActivity.this,
                        "Mother not found", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(StaffMotherProfileActivity.this,
                        "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

}