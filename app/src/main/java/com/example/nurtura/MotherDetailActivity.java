package com.example.nurtura;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.adapter.StaffChildAdapter;
import com.example.nurtura.auth.UserRepository;
import com.example.nurtura.model.Child;
import com.example.nurtura.model.User;
import com.example.nurtura.repository.ChildRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MotherDetailActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Child> children;
    StaffChildAdapter adapter;
    ChildRepository childRepository;
    UserRepository userRepository;
    User mother;
    TextView txtMotherName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mother_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        childRepository = new ChildRepository();
        recyclerView = findViewById(R.id.rvChildren);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        children = new ArrayList<>();
        userRepository = new UserRepository();
        txtMotherName = findViewById(R.id.txtMotherName);

        String userId = getIntent().getStringExtra("uid");

        userRepository.getUserByUid(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                String name = (String) userData.get("name");
                txtMotherName.setText(name);
            }

            @Override
            public void onNotFound() {
                Toast.makeText(getApplicationContext(),
                        "User not found", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Error loading user", Toast.LENGTH_SHORT).show();
            }
        });

        childRepository.getChildrenByParentId(userId, new ChildRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<Child> children) {
                adapter = new StaffChildAdapter(children);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Error loading children", Toast.LENGTH_SHORT).show();
            }
        });

    }
}