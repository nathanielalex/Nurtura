package com.example.nurtura.repository;

import com.example.nurtura.model.Immunization;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ImmunizationRepository {

    private final FirebaseFirestore db;
    private final String COLLECTION_CHILDREN = "children";
    private final String SUB_COLLECTION_IMMUNIZATIONS = "immunizations";

    public ImmunizationRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface ImmunizationCallback {
        void onSuccess(List<Immunization> records);
        void onError(Exception e);
    }

    public interface UpdateCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public void getImmunizations(String childId, ImmunizationCallback callback) {
        CollectionReference recordsRef = db.collection(COLLECTION_CHILDREN)
                .document(childId)
                .collection(SUB_COLLECTION_IMMUNIZATIONS);

        recordsRef.orderBy("dueDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Immunization> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Immunization record = document.toObject(Immunization.class);
                        list.add(record);
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    public void updateStatus(String childId, String recordId, String newStatus, boolean isCompleted, UpdateCallback callback) {
        db.collection(COLLECTION_CHILDREN)
                .document(childId)
                .collection(SUB_COLLECTION_IMMUNIZATIONS)
                .document(recordId)
                .update(
                        "status", newStatus,
                        "isCompleted", isCompleted
                )
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }
}