package com.example.nurtura.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.ArticleActivity;
import com.example.nurtura.R;
import com.example.nurtura.adapter.ImmunizationAdapter;
import com.example.nurtura.model.Child;
import com.example.nurtura.model.Immunization;
import com.example.nurtura.model.User;
import com.example.nurtura.repository.ChildRepository;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private RecyclerView rvImmunizations;
    private ImmunizationAdapter adapter;
    private TextView tvWelcome, tvInitials;
    private ChildRepository childRepository;
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
        rvImmunizations = view.findViewById(R.id.rvImmunizations);

        rvImmunizations.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ImmunizationAdapter(getContext(), new ArrayList<>());
        rvImmunizations.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        childRepository = new ChildRepository();

        btnPanic.setOnClickListener(v -> makePanicCall());

        cardChatMedic.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ChatFragment())
                    .addToBackStack(null)
                    .commit();
        });

        cardHealthRubric.setOnClickListener(v -> startActivity(new Intent(getActivity(), ArticleActivity.class)));

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
        if (user == null) return;

        String name = user.getDisplayName();

        if (name != null && !name.trim().isEmpty()) {
            tvWelcome.setText("Hello, " + name);
            tvInitials.setText(name.substring(0, 1).toUpperCase());
        } else {
            // Fallback if displayName is empty
            tvWelcome.setText("Hello");
            tvInitials.setText("?");
        }


        childRepository.getChildrenByParentId(user.getUid(), new ChildRepository.ChildrenCallback() {
            @Override
            public void onSuccess(List<Child> children) {
                if (!children.isEmpty()) {
                    loadScheduleForChild(children.get(0).getId());
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
                List<Immunization> uiList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                Date today = new Date();

                for (Map<String, Object> item : scheduleData) {
                    Timestamp ts = (Timestamp) item.get("dueDate");
                    String name = (String) item.get("vaccineName");
                    if (ts == null || name == null) continue;

                    Date date = ts.toDate();
                    String status = "Upcoming";

                    long diff = date.getTime() - today.getTime();
                    long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                    if (days < 0) status = "Overdue";
                    else if (days == 0) status = "Due Today";
                    else if (days <= 14) status = "Due in " + days + " days";

                    uiList.add(new Immunization(name, "Scheduled", status, sdf.format(date)));
                }
                adapter = new ImmunizationAdapter(getContext(), uiList);
                rvImmunizations.setAdapter(adapter);
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
