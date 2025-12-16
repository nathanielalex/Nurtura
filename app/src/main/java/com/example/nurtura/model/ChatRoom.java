package com.example.nurtura.model;

import com.google.firebase.Timestamp;

import java.util.List;
import java.util.Map;

public class ChatRoom {
    private List<String> participantIds;
    private Map<String, ParticipantInfo> participantData;
    private LastMessage lastMessage;
    private Timestamp createdAt;

    // Inner class for the denormalized participant data
    public static class ParticipantInfo {
        public String name;

        public ParticipantInfo() {}
        public ParticipantInfo(String name) {
            this.name = name;
        }
    }

    // Inner class for the last message summary
    public static class LastMessage {
        public String text;
        public String senderId;
        public Timestamp timestamp;
        public boolean isRead;

        public LastMessage() {}
        public LastMessage(String text, String senderId, Timestamp timestamp) {
            this.text = text;
            this.senderId = senderId;
            this.timestamp = timestamp;
            this.isRead = false;
        }
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public Map<String, ParticipantInfo> getParticipantData() {
        return participantData;
    }

    public void setParticipantData(Map<String, ParticipantInfo> participantData) {
        this.participantData = participantData;
    }

    public LastMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(LastMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
