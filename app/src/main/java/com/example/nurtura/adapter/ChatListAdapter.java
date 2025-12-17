package com.example.nurtura.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.R;
import com.example.nurtura.model.ChatRoom;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatListAdapter extends FirestoreRecyclerAdapter<ChatRoom, ChatListAdapter.ChatViewHolder> {
    private String currentUserId;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(String chatId, String otherUserId);
    }

    public ChatListAdapter(@NonNull FirestoreRecyclerOptions<ChatRoom> options, String currentUserId, OnChatClickListener listener) {
        super(options);
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i, @NonNull ChatRoom chatRoom) {
        String otherUserId = null;
        for (String id : chatRoom.getParticipantIds()) {
            if (!id.equals(currentUserId)) {
                otherUserId = id;
                break;
            }
        }
        if (otherUserId == null) return;

        ChatRoom.ParticipantInfo otherUser = chatRoom.getParticipantData().get(otherUserId);
        if (otherUser != null) {
            chatViewHolder.txtUserName.setText(otherUser.name);
        }
        if (chatRoom.getLastMessage() != null) {
            chatViewHolder.txtLastMessage.setText(chatRoom.getLastMessage().text);

            if (chatRoom.getLastMessage().timestamp != null) {
                Date date = chatRoom.getLastMessage().timestamp.toDate();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                chatViewHolder.txtTimestamp.setText(sdf.format(date));
            }
            if (chatRoom.getLastMessage().isRead) {
                chatViewHolder.viewIsRead.setVisibility(View.GONE);
            } else {
                chatViewHolder.viewIsRead.setVisibility(View.VISIBLE);
            }
        } else {
            chatViewHolder.txtLastMessage.setText("Start a conversation");
            chatViewHolder.txtTimestamp.setText("");
        }

        String finalOtherUserId = otherUserId;
        chatViewHolder.itemView.setOnClickListener(v -> {
            int position = chatViewHolder.getBindingAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return; // guard against invalid position

            String chatId = getSnapshots().getSnapshot(position).getId();
            listener.onChatClick(chatId, finalOtherUserId);
        });
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item_row_layout, parent, false);
        return new ChatViewHolder(view);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtLastMessage, txtTimestamp;
        View viewIsRead;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtTimestamp = itemView.findViewById(R.id.txtTimestamp);
            viewIsRead = itemView.findViewById(R.id.viewIsRead);
        }
    }
}
