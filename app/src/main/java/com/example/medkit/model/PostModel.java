package com.example.medkit.model;

import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.NonNull;

public class PostModel {
    private String userName;
    private String title;
    private String content;
    private String category;
    private Bitmap userProfilePicture;
    private Bitmap image;

    public PostModel(String userName, String title, String content, String category, Bitmap userProfilePicture, Bitmap image) {
        this.userName = userName;
        this.title = title;
        this.content = content;
        this.category = category;
        this.userProfilePicture = userProfilePicture;
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Bitmap getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(Bitmap userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}