package com.example.nurtura;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nurtura.model.Child;
import com.example.nurtura.model.Immunization;
import com.example.nurtura.model.Vaccine;
import com.example.nurtura.repository.ChildRepository;
import com.example.nurtura.repository.VaccineRepository;
import com.example.nurtura.utils.ImmunizationUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddChildActivity extends AppCompatActivity {

    private EditText etName, etDob, etWeight, etHeight, etAllergies;
    private AutoCompleteTextView etBloodType;
    private RadioGroup rgGender;
    private RadioButton rbBoy, rbGirl;
    private Button btnSave;
    private ImageButton btnClose;
    private TextView tvPageTitle;

    private Calendar calendar;
    private ChildRepository childRepository;
    private VaccineRepository vaccineRepository;

    private String editChildId = null;

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

        childRepository = new ChildRepository();
        vaccineRepository = new VaccineRepository();
        calendar = Calendar.getInstance();

        // Bind Views
        etName = findViewById(R.id.etChildName);
        etDob = findViewById(R.id.etDob);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etAllergies = findViewById(R.id.etAllergies);

        etBloodType = findViewById(R.id.etBloodType);
        rgGender = findViewById(R.id.rgGender);
        rbBoy = findViewById(R.id.rbBoy);
        rbGirl = findViewById(R.id.rbGirl);

        btnSave = findViewById(R.id.btnSaveChild);
        btnClose = findViewById(R.id.btnClose);
        tvPageTitle = findViewById(R.id.tvPageTitle);

        setupDropdowns();
        etDob.setOnClickListener(v -> showDatePicker());
        btnClose.setOnClickListener(v -> finish());

        if (getIntent().hasExtra("CHILD_ID")) {
            editChildId = getIntent().getStringExtra("CHILD_ID");
            setupEditMode(editChildId);
        }

        btnSave.setOnClickListener(v -> {
            if (editChildId != null) {
                updateChild();
            } else {
                saveChild();
            }
        });
    }

    private void setupDropdowns() {
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodTypes);
        etBloodType.setAdapter(adapter);
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etDob.setText(sdf.format(calendar.getTime()));
    }

    private String getSelectedGender() {
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId == R.id.rbBoy) return "Boy";
        if (selectedId == R.id.rbGirl) return "Girl";
        return null;
    }

    private void setupEditMode(String childId) {
        tvPageTitle.setText("Edit Child");
        btnSave.setText("Update Information");
        btnSave.setEnabled(false);

        childRepository.getChildById(childId, new ChildRepository.ChildCallback() {
            @Override
            public void onSuccess(Child child) {
                etName.setText(child.getName());
                etWeight.setText(String.valueOf(child.getLatestWeight()));
                etHeight.setText(String.valueOf(child.getLatestHeight()));

                if (child.getAllergies() != null) {
                    etAllergies.setText(TextUtils.join(", ", child.getAllergies()));
                }

                if (child.getDateOfBirth() != null) {
                    calendar.setTime(child.getDateOfBirth());
                    updateLabel();
                }

                if ("Boy".equalsIgnoreCase(child.getGender())) {
                    rbBoy.setChecked(true);
                } else if ("Girl".equalsIgnoreCase(child.getGender())) {
                    rbGirl.setChecked(true);
                }

                etBloodType.setText(child.getBloodType(), false);
                btnSave.setEnabled(true);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AddChildActivity.this, "Failed to load child data", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateChild() {
        if (!validateForm()) return;

        String name = etName.getText().toString().trim();
        String gender = getSelectedGender();
        String bloodType = etBloodType.getText().toString();
        Date dob = calendar.getTime();
        double weight = Double.parseDouble(etWeight.getText().toString());
        double height = Double.parseDouble(etHeight.getText().toString());

        List<String> allergies = new ArrayList<>();
        String allergyInput = etAllergies.getText().toString().trim();
        if (!TextUtils.isEmpty(allergyInput)) {
            String[] items = allergyInput.split(",");
            for (String item : items) {
                allergies.add(item.trim());
            }
        }

        btnSave.setEnabled(false);
        childRepository.updateChild(editChildId, name, gender, dob, bloodType, allergies, weight, height, new ChildRepository.FirestoreCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(AddChildActivity.this, "Child updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                btnSave.setEnabled(true);
                Toast.makeText(AddChildActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveChild() {
        if (!validateForm()) return;

        String name = etName.getText().toString().trim();
        String gender = getSelectedGender();
        String bloodType = etBloodType.getText().toString();
        Date dob = calendar.getTime();
        double weight = Double.parseDouble(etWeight.getText().toString());
        double height = Double.parseDouble(etHeight.getText().toString());

        List<String> allergies = new ArrayList<>();
        String allergyInput = etAllergies.getText().toString().trim();
        if (!TextUtils.isEmpty(allergyInput)) {
            String[] items = allergyInput.split(",");
            for (String item : items) {
                allergies.add(item.trim());
            }
        }

        btnSave.setEnabled(false);

        childRepository.insertChildToFirestore(name, gender, dob, bloodType, allergies, weight, height, new ChildRepository.FirestoreCallback() {
            @Override
            public void onSuccess(String childId) {
                vaccineRepository.getVaccines(new VaccineRepository.VaccineCallback() {
                    @Override
                    public void onSuccess(List<Vaccine> vaccines) {
                        List<Immunization> schedule = ImmunizationUtils.generateSchedule(dob, vaccines);
                        List<Map<String, Object>> scheduleData = new ArrayList<>();
                        for (Immunization item : schedule) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("vaccineName", item.getName());
                            map.put("dueDate", item.getDate() != null ? parseDate(item.getDate()) : null);
                            map.put("status", "Scheduled");
                            scheduleData.add(map);
                        }
                        childRepository.saveSchedule(childId, scheduleData);
                        Toast.makeText(AddChildActivity.this, "Child added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AddChildActivity.this, "Child added, but schedule failed.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                btnSave.setEnabled(true);
                Toast.makeText(AddChildActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(etName.getText())) {
            etName.setError("Name is required");
            return false;
        }
        if (TextUtils.isEmpty(etDob.getText())) {
            etDob.setError("Date of Birth is required");
            return false;
        }
        if (TextUtils.isEmpty(etWeight.getText())) {
            etWeight.setError("Weight is required");
            return false;
        }
        if (TextUtils.isEmpty(etHeight.getText())) {
            etHeight.setError("Height is required");
            return false;
        }
        if (getSelectedGender() == null) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etBloodType.getText())) {
            Toast.makeText(this, "Please select a blood type", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(dateStr);
        } catch (Exception e) {
            return new Date();
        }
    }
}
