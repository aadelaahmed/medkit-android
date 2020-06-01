package com.example.medkit.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Comment {
    public static final String CLAPPING = "clappings";
    private String content, userId, userName, commentId, postId;
    private Long createdTime,
            clappingCounter;
    public static final String COMMENT_COLLECTION = "Comments";
    private Map<String, Integer> clappings;
    public Comment() {

    }

    public Comment(String commentId, String content, String postId, String userId, String userName, Long clappingCounter) {
        this.commentId = commentId;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        Date tempDate = Calendar.getInstance().getTime();
        this.createdTime = tempDate.getTime();
        this.clappings = new HashMap<>();
        this.clappingCounter = clappingCounter;
    }

    public Comment(String content, String userId, String userName) {
        this.content = content;
        this.userName = userName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Long getClappingCounter() {
        return clappingCounter;
    }

    public void setClappingCounter(Long clappingCounter) {
        this.clappingCounter = clappingCounter;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Map<String, Integer> getClappings() {
        return clappings;
    }

    public void setClappings(Map<String, Integer> clappings) {
        this.clappings = clappings;
    }

    //@PropertyName("createdTime")
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

}
