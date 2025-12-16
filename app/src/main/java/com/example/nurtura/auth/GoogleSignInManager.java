package com.example.nurtura.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.nurtura.BuildConfig;
import com.example.nurtura.MainActivity;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executors;

public class GoogleSignInManager {
    private Context context;
    private static final String TAG = "GoogleSignIn";
    private CredentialManager credentialManager;

    private AuthRepository authRepository;

    public GoogleSignInManager(Context context) {
        this.context = context;
        this.credentialManager = CredentialManager.create(context);
        this.authRepository = new AuthRepository();
    }

    public void signInWithGoogle() {
        String key = BuildConfig.WEB_CLIENT_KEY;
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(key)
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                context,
                request,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        handleSignIn(getCredentialResponse);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(TAG, "GetCredential failed", e);
                        ((Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Sign In Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                }
        );
    }

    public void handleSignIn(GetCredentialResponse result) {
        var credential = result.getCredential();
        if (credential instanceof CustomCredential customCredential && credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            try {
                Bundle credentialData = customCredential.getData();
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credentialData);

                authRepository.firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken(), new AuthRepository.FirebaseAuthCallback() {
                    @Override
                    public void onSuccess(FirebaseUser user) {
                        Log.d(TAG, "Google Auth Success: " + user.getEmail());
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Firebase Auth Failed", e);
                        ((Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                        );
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error parsing credential", e);
            }
        } else {
            Log.w(TAG, "Credential is not of type Google ID!");
        }
    }
}
