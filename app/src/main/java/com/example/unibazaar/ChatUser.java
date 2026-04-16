package com.example.unibazaar;

public class ChatUser {

    private String otherUserId;
    private String otherUserEmail;
    private String lastMessage;

    public ChatUser() {
    }

    public ChatUser(String otherUserId, String otherUserEmail, String lastMessage) {
        this.otherUserId = otherUserId;
        this.otherUserEmail = otherUserEmail;
        this.lastMessage = lastMessage;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public String getOtherUserEmail() {
        return otherUserEmail;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}

