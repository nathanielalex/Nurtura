package com.example.nurtura.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.nurtura.ArticleActivity;
import com.example.nurtura.R;
import com.example.nurtura.ScheduleActivity;
import com.example.nurtura.auth.UserRepository;
import com.example.nurtura.model.Child;
import com.example.nurtura.repository.ChildRepository;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private TextView tvWelcome, tvInitials;
    private TextView tvNextVaccineName, tvNextVaccineDate, tvNextVaccineStatus;
    private ChildRepository childRepository;
    private UserRepository userRepository;
    private FirebaseFirestore db;
    private static final int REQUEST_CALL_PERMISSION = 1;
    private static final String MIDWIFE_NUMBER = "08123456789";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MaterialCardView btnPanic = view.findViewById(R.id.btnPanic);
        MaterialCardView cardChatMedic = view.findViewById(R.id.cardChatMedic);
        MaterialCardView cardHealthRubric = view.findViewById(R.id.cardHealthRubric);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvInitials = view.findViewById(R.id.tvInitials);
        Button btnViewSchedule = view.findViewById(R.id.btnViewSchedule);

        tvNextVaccineName = view.findViewById(R.id.tvNextVaccineName);
        tvNextVaccineDate = view.findViewById(R.id.tvNextVaccineDate);
        tvNextVaccineStatus = view.findViewById(R.id.tvNextVaccineStatus);

        db = FirebaseFirestore.getInstance();
        childRepository = new ChildRepository();
        userRepository = new UserRepository();

        btnPanic.setOnClickListener(v -> makePanicCall());

        cardChatMedic.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ChatFragment())
                    .addToBackStack(null)
                    .commit();
        });

        cardHealthRubric.setOnClickListener(v -> startActivity(new Intent(getActivity(), ArticleActivity.class)));

        btnViewSchedule.setOnClickListener(v -> startActivity(new Intent(getActivity(), ScheduleActivity.class)));

        return view;
    }

    private void makePanicCall() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + MIDWIFE_NUMBER));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePanicCall();
            } else {
                Toast.makeText(getContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            tvWelcome.setText("Hello");
            return;
        }

        String userId = user.getUid();

        userRepository.getUserByUid(userId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(Map<String, Object> userData) {
                String name = userData.get("name").toString();
                tvWelcome.setText("Hello, " + name);
                tvInitials.setText(name.substring(0, 1).toUpperCase());
            }

            @Override
            public void onNotFound() {
                tvWelcome.setText("Hello");
                tvInitials.setText("NA");
            }

            @Override
            public void onFailure(Exception e) {
                tvWelcome.setText("Hello");
                tvInitials.setText("NA");
                Toast.makeText(getContext(), "Error loading user", Toast.LENGTH_SHORT).show();
            }
        });


        childRepository.getChildrenByParentId(user.getUid(), new ChildRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<Child> children) {
                if (!children.isEmpty()) {
                    loadScheduleForChild(children.get(0).getId());
                } else {
                    tvNextVaccineName.setText("No child profile");
                    tvNextVaccineDate.setText("Please add a child");
                    tvNextVaccineStatus.setText("");
                }
            }
            @Override
            public void onFailure(Exception e) {}
        });
    }

    private void loadScheduleForChild(String childId) {
        childRepository.getImmunizationSchedule(childId, new ChildRepository.ScheduleCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> scheduleData) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                Date today = new Date();
                Map<String, Object> nearestItem = null;
                long smallestDiff = Long.MAX_VALUE;

                for (Map<String, Object> item : scheduleData) {
                    Boolean isCompleted = (Boolean) item.get("isCompleted");
                    if (isCompleted != null && isCompleted) continue; // Skip completed

                    Timestamp ts = (Timestamp) item.get("dueDate");
                    String name = (String) item.get("vaccineName");
                    if (ts == null || name == null) continue;

                    Date date = ts.toDate();
                    long diff = date.getTime() - today.getTime();

                    if (diff >= -TimeUnit.DAYS.toMillis(30) && diff < smallestDiff) {
                        smallestDiff = diff;
                        nearestItem = item;
                    }
                }

                if (nearestItem == null && !scheduleData.isEmpty()) {
                    // Check if all are done
                    boolean allDone = true;
                    for(Map<String, Object> i : scheduleData) {
                        if(i.get("isCompleted") == null || !(boolean)i.get("isCompleted")) {
                            allDone = false; break;
                        }
                    }
                    if(allDone) {
                        tvNextVaccineName.setText("All Caught Up!");
                        tvNextVaccineDate.setText("Great job, Mom!");
                        tvNextVaccineStatus.setText("Completed");
                        tvNextVaccineStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.slate_navy));
                        return;
                    }
                }

                if (nearestItem != null) {
                    Timestamp ts = (Timestamp) nearestItem.get("dueDate");
                    String name = (String) nearestItem.get("vaccineName");
                    Date date = ts.toDate();

                    long diff = date.getTime() - today.getTime();
                    long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                    String status;
                    if (days < 0) status = "Overdue by " + Math.abs(days) + " days";
                    else if (days == 0) status = "Due Today";
                    else status = "Due in " + days + " days";

                    tvNextVaccineName.setText(name);
                    tvNextVaccineDate.setText("Scheduled: " + sdf.format(date));
                    tvNextVaccineStatus.setText(status);

                    if (days < 0) tvNextVaccineStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.panic_red));
                    else tvNextVaccineStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.hot_pink));

                } else if (tvNextVaccineName.getText().toString().equals("Loading...")) {
                    tvNextVaccineName.setText("No upcoming vaccines");
                    tvNextVaccineDate.setText("");
                    tvNextVaccineStatus.setText("");
                }
            }
            @Override
            public void onFailure(Exception e) {
                tvNextVaccineName.setText("Error");
                tvNextVaccineDate.setText("Could not load schedule");
            }
        });
    }
}
