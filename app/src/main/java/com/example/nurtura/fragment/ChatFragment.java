package com.example.nurtura.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {

    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatAdapter adapter;
    private List<Map<String, Object>> messages;
    private FirebaseFirestore db;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        rvChat = view.findViewById(R.id.rvChat); // Needs ID in xml
        etMessage = view.findViewById(R.id.etMessage); // Needs ID in xml
        btnSend = view.findViewById(R.id.btnSend); // Needs ID in xml

        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messages = new ArrayList<>();

        adapter = new ChatAdapter(messages, uid);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());
        listenForMessages();

        return view;
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        Map<String, Object> msg = new HashMap<>();
        msg.put("text", text);
        msg.put("senderId", uid);
        msg.put("timestamp", FieldValue.serverTimestamp());

        db.collection("chats").document(uid).collection("messages")
                .add(msg)
                .addOnSuccessListener(ref -> etMessage.setText(""));
    }

    private void listenForMessages() {
        db.collection("chats").document(uid).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    messages.clear();
                    value.forEach(doc -> messages.add(doc.getData()));
                    adapter.notifyDataSetChanged();
                    rvChat.scrollToPosition(messages.size() - 1);
                });
    }

    // Inner Class for Adapter
    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
        private List<Map<String, Object>> list;
        private String currentUid;

        public ChatAdapter(List<Map<String, Object>> list, String uid) {
            this.list = list;
            this.currentUid = uid;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Using built-in android layout for simplicity as per "minimal" constraints
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, Object> data = list.get(position);
            String text = (String) data.get("text");
            String sender = (String) data.get("senderId");

            String prefix = sender.equals(currentUid) ? "Me: " : "Midwife: ";
            holder.text.setText(prefix + text);
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;
            ViewHolder(View v) {
                super(v);
                text = v.findViewById(android.R.id.text1);
            }
        }
    }
}
