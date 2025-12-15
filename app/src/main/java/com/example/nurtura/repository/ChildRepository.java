package com.example.nurtura.repository;

import com.example.nurtura.model.Child;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildRepository {
    public interface FirestoreCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
    public interface ChildrenCallback {
        void onSuccess(List<Child> children);
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

    public void getChildrenByParentId(String parentUid, ChildrenCallback callback) {
        DocumentReference parentRef =
                db.collection("users").document(parentUid);

        db.collection("children")
                .whereEqualTo("parentId", parentRef)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Child> children = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Child child = doc.toObject(Child.class);
                        children.add(child);
                    }

                    callback.onSuccess(children);
                })
                .addOnFailureListener(callback::onFailure);
    }
}
