package com.example.medkit.model;

import java.util.Calendar;
import java.util.Date;

public class Comment {
    private String content, userId;
    private Long createdTime;

    public Comment() {

    }

    public Comment(String content, String userId) {
        this.content = content;
        this.userId = userId;
        Date tempDate = Calendar.getInstance().getTime();
        this.createdTime = tempDate.getTime();
    }

    /*public Comment(String content, String userId, String userImage, String userName) {
        this.content = content;
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        Date temp = Calendar.getInstance().getTime();
        this.createdTime = temp.getTime();
        //this.createdTime = Timestamp.now();
    }*/

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

    public Long getCreatedTime() {
        return createdTime;
    }

    //@PropertyName("createdTime")
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

}
