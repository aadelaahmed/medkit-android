package com.example.medkit.model;

import android.graphics.Bitmap;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FieldValue;

public class PostModel {

    public static final String TITLE_KEY = "TITLE_KEY";
    public static final String DESCRIPTION_KEY = "DESCRIPTION_KEY";
    public static final String POST_IMAGE_KEY = "POST_IMAGE_KEY";
    public static final String TIME_KEY = "TIME_KEY";
    public static final String USER_NAME_KEY = "USER_NAME_KEY";
    public static final String POST_IMAGE_FLAG = "POST_IMAGE_FLAG";
    public static final String USER_IMAGE_KEY = "USER_IMAGE_KEY";
    public static final String POST_KEY = "POST_KEY";

    private String userName;
    private String title;
    private String description;
    //private String createdTime;
    private String postPhoto;
    private String userPhoto;
    private String userID;
    private String postKey;
    private String category;
    private Object createdTime;

    private Bitmap userProfilePicture;
    private Bitmap image;
    private int upVotes;
    private int downVotes;
    private int nComments;
    private boolean isUpVoted;
    private boolean isDownVoted;


    public PostModel(String title, String description, String postPhoto, String userPhoto, String userID, String category) {
        this.title = title;
        this.description = description;
        this.postPhoto = postPhoto;
        this.userPhoto = userPhoto;
        this.userID = userID;
        this.category = category;
    }


    public PostModel() {

    }

    public PostModel( String title, String description, String postPhoto, String userPhoto, String userID, String category, int upVotes, int downVotes) {
        this.title = title;
        this.description = description;
        this.postPhoto = postPhoto;
        this.userPhoto = userPhoto;
        this.userID = userID;
        this.category = category;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.createdTime = FieldValue.serverTimestamp();
    }

    public Object getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Object createdTime) {
        this.createdTime = createdTime;
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

    public PostModel(String userName,
                     String title,
                     String description,
                     String category,
                     Bitmap userProfilePicture,
                     Bitmap image,
                     int upVotes,
                     int downVotes,
                     int nComments,
                     boolean isUpVoted,
                     boolean isDownVoted) {
        this.userName = userName;
        this.title = title;
        this.description = description;
        this.category = category;
        this.userProfilePicture = userProfilePicture;
        this.image = image;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.nComments = nComments;
        this.isUpVoted = isUpVoted;
        this.isDownVoted = isDownVoted;
    }


    public String getPostPhoto() {
        return postPhoto;
    }

    public void setPostPhoto(String postPhoto) {
        this.postPhoto = postPhoto;
    }

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


    public int getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(int upVotes) {
        this.upVotes = upVotes;
    }


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
