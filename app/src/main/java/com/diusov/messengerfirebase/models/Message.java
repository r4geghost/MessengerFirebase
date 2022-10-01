package com.diusov.messengerfirebase.models;

public class Message {

    private String text;
    private String senderId;
    private String ReceiverId;

    public Message() {
    }

    public Message(String text, String senderId, String receiverId) {
        this.text = text;
        this.senderId = senderId;
        ReceiverId = receiverId;
    }

    public String getText() {
        return text;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return ReceiverId;
    }
}
