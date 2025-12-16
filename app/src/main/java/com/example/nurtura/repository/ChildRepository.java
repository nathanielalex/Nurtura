package com.example.nurtura.repository;

import com.example.nurtura.model.Child;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface FirestoreCallback {
        void onSuccess(String childId); // Changed to return ID
        void onFailure(Exception e);
    }

    public interface ChildrenCallback {
        void onSuccess(List<Child> children);
        void onFailure(Exception e);
    }

    public interface ScheduleCallback {
        void onSuccess(List<Map<String, Object>> schedule);
        void onFailure(Exception e);
    }

    public void insertChildToFirestore(String name, String gender, Date dob,
                                       String bloodType, List<String> allergies,
                                       double weight, double height,
                                       FirestoreCallback callback) {
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
                .addOnSuccessListener(documentReference -> callback.onSuccess(documentReference.getId()))
                .addOnFailureListener(callback::onFailure);
    }

    // New: Save generated schedule to Firestore
    public void saveSchedule(String childId, List<Map<String, Object>> schedule) {
        for (Map<String, Object> item : schedule) {
            db.collection("children").document(childId)
                    .collection("immunizations")
                    .add(item);
        }
    }

    // New: Fetch stored schedule
    public void getImmunizationSchedule(String childId, ScheduleCallback callback) {
        db.collection("children").document(childId)
                .collection("immunizations")
                .orderBy("dueDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Map<String, Object>> schedule = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Map<String, Object> data = doc.getData();
                        data.put("id", doc.getId()); // Store doc ID for updates
                        schedule.add(data);
                    }
                    callback.onSuccess(schedule);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getChildrenByParentId(String parentUid, ChildrenCallback callback) {
        DocumentReference parentRef = db.collection("users").document(parentUid);
        db.collection("children")
                .whereEqualTo("parentId", parentRef)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Child> children = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Child child = doc.toObject(Child.class);
                        child.setId(doc.getId()); // Ensure ID is set
                        children.add(child);
                    }
                    callback.onSuccess(children);
                })
                .addOnFailureListener(callback::onFailure);
    }
}
