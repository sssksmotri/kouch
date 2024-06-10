package com.example.kouch.Model;

import com.google.firebase.Timestamp;
import java.util.List;

public class ChatRoomModel {
    String ChatRoomId;
    List<String> userIds;
    Timestamp lastMessage;
    String lastMessageSenderId;
    String  lastMessage_user;

    public ChatRoomModel() {
    }

    public ChatRoomModel(String chatRoomId, List<String> userIds, Timestamp lastMessage, String lastMessageSenderId) {
        ChatRoomId = chatRoomId;
        this.userIds = userIds;
        this.lastMessage = lastMessage;
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getChatRoomId() {
        return ChatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        ChatRoomId = chatRoomId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public Timestamp getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Timestamp lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public String getLastMessage_user() {
        return lastMessage_user;
    }

    public void setLastMessage_user(String lastMessage_user) {
        this.lastMessage_user = lastMessage_user;
    }
}
