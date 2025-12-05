package com.example.nurtura.fragment;

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

import com.example.nurtura.LoginActivity;
import com.example.nurtura.R;
import com.example.nurtura.adapter.ChildAdapter;
import com.example.nurtura.model.Child;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    FirebaseFirestore db;
    RecyclerView recyclerView;
    ArrayList<Child> children;
    ChildAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button signOutButton = view.findViewById(R.id.btnSignOut);
        signOutButton.setOnClickListener(v -> LoginActivity.signOut(requireContext()));

        TextView txtMotherName = view.findViewById(R.id.txtMotherName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.rvChildren);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        children = new ArrayList<>();

        db = FirebaseFirestore.getInstance();


        if (user != null) {
            txtMotherName.setText(user.getDisplayName());
            DocumentReference parentRef = db.collection("users").document(user.getUid());
            db.collection("children")
                    .whereEqualTo("parentId", parentRef)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Child child = document.toObject(Child.class);
                                    children.add(child);
                                }
                                adapter = new ChildAdapter(children);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    });
        }


        return view;
    }
}
