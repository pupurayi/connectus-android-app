package com.connectus.mobile.common;

public class DetailedChatMessage {
    private String messageId;
    private Sender sender;
    private String message;
    private Long createdAt;

    public DetailedChatMessage(String messageId, Sender sender, String message, Long createdAt) {
        this.messageId = messageId;
        this.sender = sender;
        this.message = message;
        this.createdAt = createdAt;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
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
