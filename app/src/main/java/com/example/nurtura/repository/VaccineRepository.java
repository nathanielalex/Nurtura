package com.example.nurtura.repository;

import android.util.Log;
import com.example.nurtura.model.Vaccine;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VaccineRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "VaccineRepository";

    public interface VaccineCallback {
        void onSuccess(List<Vaccine> vaccines);
        void onFailure(Exception e);
    }

    public void getVaccines(VaccineCallback callback) {
        db.collection("vaccines")
                .orderBy("recommendedAgeInMonths", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Vaccine> vaccines = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Vaccine vaccine = doc.toObject(Vaccine.class);
                            vaccine.setId(doc.getId());
                            vaccines.add(vaccine);
                        } catch (Exception e) {
                            Log.e(TAG, "Error mapping document: " + doc.getId(), e);
                        }
                    }
                    callback.onSuccess(vaccines);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching vaccines", e);
                    callback.onFailure(e);
                });
    }
}
