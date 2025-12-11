package com.example.nurtura.auth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    public interface FirestoreCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void saveUserToFirestore(FirebaseUser user, FirestoreCallback callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", user.getUid());
        userData.put("name", user.getDisplayName());
        userData.put("email", user.getEmail());
        userData.put("last_login", com.google.firebase.Timestamp.now());

        db.collection("users").document(user.getUid())
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void registerUserToFirestore(FirebaseUser user, String name, String phone, FirestoreCallback callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", user.getUid());
        userData.put("name", name);
        userData.put("email", user.getEmail());
        userData.put("phoneNumber", phone);
        userData.put("role", "patient");
        userData.put("last_login", com.google.firebase.Timestamp.now());

        db.collection("users").document(user.getUid())
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}
