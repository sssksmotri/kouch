package com.example.kouch.Model;

import com.google.firebase.Timestamp;

import java.util.UUID;

public class ChatMessageModel {
    private String id;
    private String message;
    private String senderId;
    private Timestamp timestamp;
    private String replyToMessageId;
    private String replyToMessageText;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String id, String message, String senderId, Timestamp timestamp) {
        this.id=id;
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
    public ChatMessageModel(String id, String message, String senderId, Timestamp timestamp, String replyToMessageId, String replyToMessageText) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.replyToMessageId = replyToMessageId;
        this.replyToMessageText = replyToMessageText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(String replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public String getReplyToMessageText() {
        return replyToMessageText;
    }

    public void setReplyToMessageText(String replyToMessageText) {
        this.replyToMessageText = replyToMessageText;
    }
}
