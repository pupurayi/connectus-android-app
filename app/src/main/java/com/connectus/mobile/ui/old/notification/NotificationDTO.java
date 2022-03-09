package com.connectus.mobile.ui.old.notification;

public class NotificationDTO {
    private long notificationId;
    private String title;
    private String message;
    private long sentTime;

    public NotificationDTO(long notificationId, String title, String message, long sentTime) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.sentTime = sentTime;
    }

    public NotificationDTO(String title, String message, long sentTime) {
        this.title = title;
        this.message = message;
        this.sentTime = sentTime;
    }

    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSentTime() {
        return sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }
}
