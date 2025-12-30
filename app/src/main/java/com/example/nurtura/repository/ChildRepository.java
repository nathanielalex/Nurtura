package com.example.nurtura.repository;

import androidx.annotation.NonNull;

import com.example.nurtura.model.Child;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface FirestoreCallback {
        void onSuccess(String result);
        void onFailure(Exception e);
    }

    public interface ChildrenCallback {
        void onSuccess(List<Child> children);
        void onFailure(Exception e);
    }

    public interface ChildCallback {
        void onSuccess(Child child);
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

    public void saveSchedule(String childId, List<Map<String, Object>> schedule) {
        for (Map<String, Object> item : schedule) {
            item.put("isCompleted", false);
            db.collection("children").document(childId)
                    .collection("immunizations")
                    .add(item);
        }
    }

    public void saveSingularSchedule(String childId, Map<String, Object> schedule) {
        if (childId == null || schedule == null) {
            return;
        }

        schedule.put("isCompleted", false);

        db.collection("children").document(childId)
                .collection("immunizations")
                .add(schedule);
    }

    public void getImmunizationSchedule(String childId, final ScheduleCallback callback) {
        db.collection("children")
                .document(childId)
                .collection("immunizations")
                .orderBy("dueDate", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> scheduleData = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            data.put("id", document.getId());
                            scheduleData.add(data);
                        }
                        callback.onSuccess(scheduleData);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
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
                        child.setId(doc.getId());
                        children.add(child);
                    }
                    callback.onSuccess(children);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void updateImmunizationStatus(String childId, String immunizationId, boolean isCompleted, FirestoreCallback callback) {
        db.collection("children").document(childId)
                .collection("immunizations").document(immunizationId)
                .update("isCompleted", isCompleted)
                .addOnSuccessListener(aVoid -> callback.onSuccess("Updated"))
                .addOnFailureListener(callback::onFailure);
    }

    public void getChildById(String childId, ChildCallback callback) {
        db.collection("children").document(childId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Child child = documentSnapshot.toObject(Child.class);
                        if (child != null) {
                            child.setId(documentSnapshot.getId());
                            callback.onSuccess(child);
                        }
                    } else {
                        callback.onFailure(new Exception("Child not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void updateChild(String childId, String name, String gender, Date dob,
                            String bloodType, List<String> allergies,
                            double weight, double height, FirestoreCallback callback) {

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("gender", gender);
        updates.put("dateOfBirth", dob);
        updates.put("bloodType", bloodType);
        updates.put("allergies", allergies);
        updates.put("latestHeight", height);
        updates.put("latestWeight", weight);

        db.collection("children").document(childId)
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess("Updated"))
                .addOnFailureListener(callback::onFailure);
    }

    public void getAllChildren(ChildrenCallback callback) {
        db.collection("children")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Child> children = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Child child = doc.toObject(Child.class);
                        child.setId(doc.getId());
                        children.add(child);
                    }
                    callback.onSuccess(children);
                })
                .addOnFailureListener(callback::onFailure);
    }
}
