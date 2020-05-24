package com.example.medkit.model;

import com.example.medkit.utils.NotificationHelper;

import java.util.Calendar;

public class NotificationModel {
    private String from;

    public static String NOTIFICATION_COLLECTION = "Notification";
    private String from_full_name;

    public NotificationModel(String from, String post_id, String message, boolean read, String time, String n_id, String from_full_name) {
        this.from = from;
        this.from_full_name = from_full_name;
        this.post_id = post_id;
        this.message = message;
        this.read = read;
        this.time = time;
        this.n_id = n_id;
        this.createdTime = Calendar.getInstance().getTime().getTime();
    }

    public String getFrom_full_name() {
        return from_full_name;
    }

    private String post_id;
    private String message;
    private String time;
    private boolean read;
    private String n_id;
    private long createdTime;

    public void setFrom_full_name(String from_full_name) {
        this.from_full_name = from_full_name;
    }

    public String getN_id() {
        return n_id;
    }

    public NotificationModel() {

    }

    public void setN_id(String n_id) {
        this.n_id = n_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    //
    public void setRead(boolean read, String id) {
        this.read = read;
        NotificationHelper.update_read(id);
    }
}
