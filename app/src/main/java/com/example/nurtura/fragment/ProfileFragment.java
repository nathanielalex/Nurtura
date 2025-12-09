package com.example.nurtura.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.AddChildActivity;
import com.example.nurtura.R;
import com.example.nurtura.adapter.ChildAdapter;
import com.example.nurtura.auth.AuthRepository;
import com.example.nurtura.model.Child;
import com.example.nurtura.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    FirebaseFirestore db;
    RecyclerView recyclerView;
    ArrayList<Child> children;
    ChildAdapter adapter;

    AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        authRepository = new AuthRepository();

        Button signOutButton = view.findViewById(R.id.btnSignOut);
        signOutButton.setOnClickListener(v -> authRepository.signOut(requireContext()));

        Button btnAddChild = view.findViewById(R.id.btnAddChild);

        btnAddChild.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddChildActivity.class);
            startActivity(intent);
        });

        TextView txtMotherName = view.findViewById(R.id.txtMotherName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.rvChildren);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        children = new ArrayList<>();

        db = FirebaseFirestore.getInstance();


        if (user != null) {
            db.collection("users")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {
                            User parent = documentSnapshot.toObject(User.class);
                            txtMotherName.setText(parent.getName());
                        }

                        DocumentReference parentRef = db.collection("users")
                                .document(user.getUid());

                        db.collection("children")
                                .whereEqualTo("parentId", parentRef)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {

                                    for (QueryDocumentSnapshot doc : querySnapshot) {
                                        Child child = doc.toObject(Child.class);
                                        children.add(child);
                                    }

                                    adapter = new ChildAdapter(children);
                                    recyclerView.setAdapter(adapter);

                                });
                    });
        }


        return view;
    }
}
