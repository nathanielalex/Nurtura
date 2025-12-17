package com.example.nurtura.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nurtura.R;
import com.example.nurtura.model.ChatMessage;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatMessageAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatMessageAdapter.MessageViewHolder> {

    private final String currentUserId;
    private final SimpleDateFormat timeFormat;

    public ChatMessageAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options, String currentUserId) {
        super(options);
        this.currentUserId = currentUserId;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull ChatMessage chatMessage) {
        boolean isSentByMe = chatMessage.getSenderId().equals(currentUserId);

        if (isSentByMe) {
            messageViewHolder.layoutSent.setVisibility(View.VISIBLE);
            messageViewHolder.layoutReceived.setVisibility(View.GONE);
            messageViewHolder.txtMessageSent.setText(chatMessage.getText());

            if (chatMessage.getTimestamp() != null) {
                messageViewHolder.txtTimeSent.setText(
                        timeFormat.format(chatMessage.getTimestamp().toDate())
                );
            }
        } else {
            messageViewHolder.layoutReceived.setVisibility(View.VISIBLE);
            messageViewHolder.layoutSent.setVisibility(View.GONE);
            messageViewHolder.txtMessageReceived.setText(chatMessage.getText());

            if (chatMessage.getTimestamp() != null) {
                messageViewHolder.txtTimeReceived.setText(
                        timeFormat.format(chatMessage.getTimestamp().toDate())
                );
            }
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutReceived, layoutSent;
        TextView txtMessageReceived, txtTimeReceived;
        TextView txtMessageSent, txtTimeSent;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutReceived = itemView.findViewById(R.id.layoutReceived);
            txtMessageReceived = itemView.findViewById(R.id.txtMessageReceived);
            txtTimeReceived = itemView.findViewById(R.id.txtTimeReceived);
            layoutSent = itemView.findViewById(R.id.layoutSent);
            txtMessageSent = itemView.findViewById(R.id.txtMessageSent);
            txtTimeSent = itemView.findViewById(R.id.txtTimeSent);
        }
    }
}
