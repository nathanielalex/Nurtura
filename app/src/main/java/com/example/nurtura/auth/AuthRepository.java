package com.example.nurtura.auth;

import android.content.Context;
import android.content.Intent;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;

import com.example.nurtura.LoginActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executors;

public class AuthRepository {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String TAG = "GoogleSignIn";
    private UserRepository userRepository = new UserRepository();

    public interface LoginCallback {
        void onComplete(boolean success, String userId);
    }

    public interface RegistrationCallback {
        void onComplete(boolean success);
    }

    public interface FirebaseAuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception e);
    }

    public void firebaseAuthWithGoogle(String idToken, FirebaseAuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AuthResult authResult = task.getResult();
                        FirebaseUser user = mAuth.getCurrentUser();

                        boolean isNewUser = authResult.getAdditionalUserInfo() != null
                                && authResult.getAdditionalUserInfo().isNewUser();

                        if (user != null && isNewUser) {
                            Log.d(TAG, "New user detected. Saving to Firestore...");
                            userRepository.saveUserToFirestore(user, new UserRepository.FirestoreCallback() {
                                @Override
                                public void onSuccess() {
                                    if (callback != null) callback.onSuccess(user);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e(TAG, "Failed to save user data", e);
                                    if (callback != null) callback.onSuccess(user);
                                }
                            });
                        } else {
                            Log.d(TAG, "Existing user. Skipping Firestore save.");
                            if (callback != null) {
                                callback.onSuccess(user);
                            }
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (callback != null) {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public void signOut(Context context) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        CredentialManager credentialManager = CredentialManager.create(context);

        mAuth.signOut();
        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<Void, ClearCredentialException>() {
                    @Override
                    public void onResult(Void unused) {
                        Log.d(TAG, "Credential state cleared");
                    }

                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        Log.e(TAG, "Couldn't clear user credentials: " + e.getLocalizedMessage());
                    }
                }
        );
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }

    public void loginUser(Context context, String email, String password, LoginCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            if (callback != null) {
                callback.onComplete(false, null);
            }
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            if (callback != null) {
                                callback.onComplete(true, userId);
                            }
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        if (callback != null) {
                            callback.onComplete(false, null);
                        }
                    }
                });
    }
    public void registerUser(Context context, String email, String password, String name, String phone, RegistrationCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            callback.onComplete(false);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null) {
                            userRepository.registerUserToFirestore(user, name, phone, new UserRepository.FirestoreCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show();
                                    callback.onComplete(true);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(context, "Failed to save user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("FirestoreError", "Error saving user", e);
                                    callback.onComplete(false);
                                }
                            });
                        }
                        callback.onComplete(true);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(context, "Authentication Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        callback.onComplete(false);
                    }
                });
    }
}
