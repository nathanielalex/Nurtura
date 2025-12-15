package com.example.nurtura;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.nurtura.fragment.SearchFragment;
import com.example.nurtura.fragment.StaffChatFragment;
import com.example.nurtura.fragment.StaffProfileFragment;
import com.example.nurtura.fragment.VaccineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class StaffActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        bottomNavigationView = findViewById(R.id.staff_bottom_navigation);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.staff_fragment_container, new SearchFragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_search) {
                    selectedFragment = new SearchFragment();
                } else if (itemId == R.id.nav_vaccine) {
                    selectedFragment = new VaccineFragment();
                } else if (itemId == R.id.nav_chat_staff) {
                    selectedFragment = new StaffChatFragment();
                } else if (itemId == R.id.nav_profile_staff) {
                    selectedFragment = new StaffProfileFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.staff_fragment_container, selectedFragment)
                            .commit();
                }

                return true;
            }
        });
    }
}