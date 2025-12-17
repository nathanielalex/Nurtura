package com.example.nurtura.auth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    public interface FirestoreCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
    public interface UserCallback {
        void onSuccess(Map<String, Object> userData);
        void onNotFound();
        void onFailure(Exception e);
    }
    public interface RoleCallback {
        void onRoleReceived(String role);
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

    public void getUserByEmail(String email, UserCallback callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                        callback.onSuccess(document.getData());
                    } else {
                        callback.onNotFound();
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getUserByUid(String uid, UserCallback callback) {

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot.getData());
                    } else {
                        callback.onNotFound();
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getRole(String userId, RoleCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        callback.onRoleReceived(role);
                    } else {
                        callback.onRoleReceived(null);
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onRoleReceived(null);
                });
    }
}
