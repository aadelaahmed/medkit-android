package com.example.medkit.model;

import android.graphics.Bitmap;

public class NotificationModel {
    private String userName;
    private Bitmap userPhoto;
    private String notificationContent;
    private boolean isRead;

    public NotificationModel() {

    }

    public NotificationModel(String userName, Bitmap userPhoto, String notificationContent, boolean isRead) {
        this.userName = userName;
        this.userPhoto = userPhoto;
        this.notificationContent = notificationContent;
        this.isRead = isRead;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Bitmap getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(Bitmap userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
