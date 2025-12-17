package com.example.nurtura.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nurtura.ChatActivity;
import com.example.nurtura.R;
import com.example.nurtura.adapter.ChatListAdapter;
import com.example.nurtura.model.ChatRoom;
import com.example.nurtura.repository.ChatRepository;
import com.example.nurtura.utils.WrapContentLinearLayoutManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;


public class StaffChatFragment extends Fragment {

    private ChatRepository chatRepository;
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_chat, container, false);
        recyclerView = view.findViewById(R.id.recyclerChatList);
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        chatRepository = new ChatRepository();
        currentUserId = FirebaseAuth.getInstance().getUid();
        Log.d("STAFF_CHAT", "Current User ID: " + currentUserId); // <-- ADD THIS LOG
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        Query query = chatRepository.getChatRoomsQuery(currentUserId);
        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Log.d("STAFF_CHAT", doc.getId() + " => " + doc.getData());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("STAFF_CHAT", "Error fetching chat rooms", e);
                });

        FirestoreRecyclerOptions<ChatRoom> options = new FirestoreRecyclerOptions.Builder<ChatRoom>()
                .setQuery(query, ChatRoom.class)
                .build();

        adapter = new ChatListAdapter(options, currentUserId, (chatId, otherUserId) -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("CHAT_ID", chatId);
            intent.putExtra("OTHER_USER_ID", otherUserId);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
    }
}