package com.example.medkit.model;

import android.graphics.Bitmap;

import com.google.firebase.firestore.Exclude;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostModel {

    public static final String TITLE_KEY = "TITLE_KEY";
    public static final String DESCRIPTION_KEY = "DESCRIPTION_KEY";
    public static final String TIME_KEY = "TIME_KEY";
    public static final String POST_IMAGE_FLAG = "POST_IMAGE_FLAG";
    public static final String POST_KEY = "POST_KEY";
    public static final String USER_ID = "USE_ID";
    public String userName;
    public String title;
    public String description;
    public String postPhoto;
    public String userPhoto;
    public String userID;
    public String postKey;
    public String category;
    public Long tempTime;
    private Bitmap userProfilePicture;
    private Bitmap image;
    private int upVotes;
    private int downVotes;
    private int nComments;
    private boolean isUpVoted;
    private boolean isDownVoted;
    public Map<String, Integer> mapUpVotes;

    public PostModel(String title, String description, String postPhoto, String userID, String category) {
        this.title = title;
        this.description = description;
        this.postPhoto = postPhoto;
        this.userID = userID;
        this.category = category;
        Date temp = Calendar.getInstance().getTime();
        this.tempTime = temp.getTime();
        this.mapUpVotes = new HashMap<>();
    }


    public PostModel() {

    }

   /* public Map<String, Integer> getMapUpVotes() {
        return mapUpVotes;
    }

    public void setMapUpVotes(Map<String, Integer> mapUpVotes) {
        this.mapUpVotes = mapUpVotes;
    }*/

    public Long getCreatedTime() {
        return tempTime;
    }

    public void setCreatedTime(long createdTime) {
        this.tempTime = createdTime;
    }

    @Override
    public String toString() {
        return "PostModel{" +
                "userName='" + userName + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", postPhoto='" + postPhoto + '\'' +
                ", userPhoto='" + userPhoto + '\'' +
                ", userID='" + userID + '\'' +
                ", postKey='" + postKey + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    public String getPostPhoto() {
        return postPhoto;
    }

    public void setPostPhoto(String postPhoto) {
        this.postPhoto = postPhoto;
    }

    @Exclude
    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    @Exclude
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Exclude
    public Bitmap getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(Bitmap userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }


    @Exclude
    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Exclude
    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }

    @Exclude
    public int getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(int downVotes) {
        this.downVotes = downVotes;
    }

    @Exclude
    public int getnComments() {
        return nComments;
    }

    public void setnComments(int nComments) {
        this.nComments = nComments;
    }

    @Exclude
    public boolean isUpVoted() {
        return isUpVoted;
    }

    public void setUpVoted(boolean upVoted) {
        isUpVoted = upVoted;
    }

    @Exclude
    public boolean isDownVoted() {
        return isDownVoted;
    }

    public void setDownVoted(boolean downVoted) {
        isDownVoted = downVoted;
    }
}
