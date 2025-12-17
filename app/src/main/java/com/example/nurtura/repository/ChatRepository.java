package com.example.nurtura.repository;

import com.example.nurtura.model.ChatMessage;
import com.example.nurtura.model.ChatRoom;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRepository {
    private final FirebaseFirestore db;
    private final CollectionReference chatsRef;

    public ChatRepository() {
        db = FirebaseFirestore.getInstance();
        chatsRef = db.collection("chats");
    }

    public String generateChatId(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public void sendMessage(String chatId, String senderId, String text, String type) {
        DocumentReference chatRoomRef = chatsRef.document(chatId);
        DocumentReference newMessageRef = chatRoomRef.collection("messages").document();

        WriteBatch batch = db.batch();

        ChatMessage newMessage = new ChatMessage(senderId, text, type);

        ChatRoom.LastMessage lastMessage = new ChatRoom.LastMessage(
                text,
                senderId,
                newMessage.getTimestamp()
        );

        batch.set(newMessageRef, newMessage);
        batch.update(chatRoomRef, "lastMessage", lastMessage);

        batch.commit().addOnFailureListener(e -> {
        });
    }

    public void initChatRoom(String userId1, String userId2,
                             String name1, String name2) {
        //userId1 is staff
        //userId2 is the patient
        String chatId = generateChatId(userId1, userId2);
        DocumentReference docRef = chatsRef.document(chatId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                List<String> ids = Arrays.asList(userId1, userId2);

                Map<String, ChatRoom.ParticipantInfo> partData = new HashMap<>();
                partData.put(userId1, new ChatRoom.ParticipantInfo(name1));
                partData.put(userId2, new ChatRoom.ParticipantInfo(name2));

                ChatRoom.LastMessage lastMessage = new ChatRoom.LastMessage("Hello, I will be assisting you as of today.",
                        userId1, Timestamp.now());


                Map<String, Object> newChat = new HashMap<>();
                newChat.put("participantIds", ids);
                newChat.put("participantData", partData);
                newChat.put("createdAt", Timestamp.now());
                newChat.put("lastMessage", lastMessage);
                docRef.set(newChat);
            }
        });

    }

    public Query getChatRoomsQuery(String currentUserId) {
        return chatsRef
                .whereArrayContains("participantIds", currentUserId)
                .orderBy("lastMessage.timestamp", Query.Direction.DESCENDING);
    }

    public Query getMessagesQuery(String chatId) {
        return chatsRef.document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING);
    }
}
