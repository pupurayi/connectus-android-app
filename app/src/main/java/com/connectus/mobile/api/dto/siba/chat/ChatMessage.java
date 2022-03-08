package com.connectus.mobile.api.dto.siba.chat;


import java.util.UUID;

public class ChatMessage {
    private String messageId;
    private UUID siba_profile_id;
    private UUID sender_profile_id;
    private String message;
    private Long createdAt;

    public ChatMessage(String messageId, UUID siba_profile_id, UUID sender_profile_id, String message, Long createdAt) {
        this.messageId = messageId;
        this.siba_profile_id = siba_profile_id;
        this.sender_profile_id = sender_profile_id;
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public UUID getSiba_profile_id() {
        return siba_profile_id;
    }

    public void setSiba_profile_id(UUID siba_profile_id) {
        this.siba_profile_id = siba_profile_id;
    }

    public UUID getSender_profile_id() {
        return sender_profile_id;
    }

    public void setSender_profile_id(UUID sender_profile_id) {
        this.sender_profile_id = sender_profile_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
