package com.matchstick.brightlife.models;

public class Notification {
    private String title, notificaitonTime, notificationText, notificationReason;

    public Notification(String title, String notificaitonTime, String notificationText, String notificationReason) {
        this.title = title;
        this.notificaitonTime = notificaitonTime;
        this.notificationText = notificationText;
        this.notificationReason = notificationReason;
    }

    public String getTitle() {
        return title;
    }

    public String getNotificaitonTime() {
        return notificaitonTime;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public String getNotificationReason() {
        return notificationReason;
    }
}
