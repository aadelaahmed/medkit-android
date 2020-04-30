package com.example.medkit.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class Comment {
    private String content, userId, userImage, userName;
    private Timestamp createdTime;

    public Comment() {

    }

    public Comment(String content, String userId, String userImage, String userName) {
        this.content = content;
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        this.createdTime = Timestamp.now();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    @PropertyName("createdTime")
    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

}
