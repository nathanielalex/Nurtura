package com.example.nurtura.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildRepository {
    public interface FirestoreCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void insertChildToFirestore(String name, String gender, Date dob,
                                       String bloodType, List<String> allergies,
                                       double weight, double height,
                                       ChildRepository.FirestoreCallback callback) {
        Map<String, Object> childData = new HashMap<>();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference parentRef = db.collection("users").document(uid);

        childData.put("name", name);
        childData.put("gender", gender);
        childData.put("dateOfBirth", dob);
        childData.put("bloodType", bloodType);
        childData.put("allergies", allergies);
        childData.put("latestHeight", height);
        childData.put("latestWeight", weight);
        childData.put("parentId", parentRef);

        db.collection("children")
                .add(childData)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }
}
