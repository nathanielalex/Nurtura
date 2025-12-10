package com.example.nurtura;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nurtura.databinding.ActivityAddChildBinding;
import com.example.nurtura.repository.ChildRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddChildActivity extends AppCompatActivity {
    private ActivityAddChildBinding binding;
    private ChildRepository childRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_child);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityAddChildBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        childRepository = new ChildRepository();

        binding.etDob.setOnClickListener(v -> showDatePicker());
        binding.tiDob.setEndIconOnClickListener(v -> showDatePicker());

        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                bloodTypes
        );
        binding.etBloodType.setAdapter(adapter);

        binding.btnSaveChild.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        String name = binding.etChildName.getText().toString();
        String dobStr = binding.etDob.getText().toString();
        String gender = binding.rbBoy.isChecked() ? "Boy" : "Girl";
        Log.d("ChildActivity", "gender: " + gender);
        String blood = binding.etBloodType.getText().toString();
        String weightStr = binding.etWeight.getText().toString();
        String heightStr = binding.etHeight.getText().toString();
        String allergiesStr = binding.etAllergies.getText().toString();

        if (name.isEmpty() || dobStr.isEmpty() || blood.isEmpty() ||
                weightStr.isEmpty() || heightStr.isEmpty()) {

            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double weight, height;
        try {
            weight = Double.parseDouble(weightStr);
            height = Double.parseDouble(heightStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Height or weight is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        Date dob;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false); // prevent invalid dates like 32/15/2023
        try {
            dob = sdf.parse(dobStr);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format (use DD/MM/YYYY)", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> allergiesList = new ArrayList<>();

        if (!allergiesStr.isEmpty()) {
            allergiesList = Arrays.asList(allergiesStr.split("\\s*,\\s*"));
        }

        childRepository.insertChildToFirestore(name, gender, dob, blood, allergiesList,
                weight, height, new ChildRepository.FirestoreCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AddChildActivity.this,
                                "Child added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AddChildActivity.this,
                                "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("Firestore", "Error adding child", e);
                    }
                });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);

                    binding.etDob.setText(date);
                },
                year, month, day
        );

        datePickerDialog.show();
    }
}