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

    /**
     * 2. Send Message
     * Uses a BATCH write to ensure that adding the message to the sub-collection
     * and updating the 'lastMessage' in the main document happen together.
     */
    public void sendMessage(String chatId, String senderId, String text, String type) {
        DocumentReference chatRoomRef = chatsRef.document(chatId);
        DocumentReference newMessageRef = chatRoomRef.collection("messages").document(); // Auto-ID

        WriteBatch batch = db.batch();

        // A. Create the message object
        ChatMessage newMessage = new ChatMessage(senderId, text, type);

        // B. Create the LastMessage summary object
        ChatRoom.LastMessage lastMessage = new ChatRoom.LastMessage(
                text,
                senderId,
                newMessage.getTimestamp() // Use same timestamp
        );

        // C. Queue the operations
        batch.set(newMessageRef, newMessage);
        batch.update(chatRoomRef, "lastMessage", lastMessage);

        // D. Commit transaction
        batch.commit().addOnFailureListener(e -> {
            // Handle failure (e.g., log it)
        });
    }

    /**
     * 3. Create or Initialize Chat Room
     * Call this when a user clicks "Message" on a profile.
     * It checks if the chat exists; if not, it creates it with initial data.
     */
    public void initChatRoom(String userId1, String userId2,
                             String name1, String name2) {

        String chatId = generateChatId(userId1, userId2);
        DocumentReference docRef = chatsRef.document(chatId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                // Prepare initial data
                List<String> ids = Arrays.asList(userId1, userId2);

                Map<String, ChatRoom.ParticipantInfo> partData = new HashMap<>();
                partData.put(userId1, new ChatRoom.ParticipantInfo(name1));
                partData.put(userId2, new ChatRoom.ParticipantInfo(name2));

                Map<String, Object> newChat = new HashMap<>();
                newChat.put("participantIds", ids);
                newChat.put("participantData", partData);
                newChat.put("createdAt", Timestamp.now());
                newChat.put("lastMessage", null); // Empty initially

                docRef.set(newChat);
            }
        });

    }

    /**
     * 4. Get Chat List (for the Inbox screen)
     * Returns a query you can pass to a FirestoreRecyclerAdapter
     */
    public Query getChatRoomsQuery(String currentUserId) {
        return chatsRef
                .whereArrayContains("participantIds", currentUserId)
                .orderBy("lastMessage.timestamp", Query.Direction.DESCENDING);
    }

    /**
     * 5. Get Messages (for the Chat Screen)
     * Returns a query for the specific conversation
     */
    public Query getMessagesQuery(String chatId) {
        return chatsRef.document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING);
    }
}
