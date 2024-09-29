package com.example.ezchatapp;

public class ChatMessage {
    private String message;
    private String sender;

    // Constructor
    public ChatMessage(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
