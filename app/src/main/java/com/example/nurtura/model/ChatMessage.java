package com.example.nurtura.model;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String senderId;
    private String text;
    private Timestamp timestamp;
    private String type;

    public ChatMessage() {}

    public ChatMessage(String senderId, String text, String type) {
        this.senderId = senderId;
        this.text = text;
        this.timestamp = Timestamp.now();
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
