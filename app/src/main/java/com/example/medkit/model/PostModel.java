package com.example.medkit.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostModel implements Parcelable {

    public static final String TITLE_KEY = "title";
    public static final String DESCRIPTION_KEY = "description";
    public static final String CREATED_TIME_KEY = "createdTime";
    public static final String TIME_KEY = "TIME_KEY";
    public static final String POST_IMAGE_FLAG = "POST_IMAGE_FLAG";
    public static final String POST_KEY = "postKey";
    public static final String USER_ID = "userID";
    public static final String OBJECT_KEY = "OBJECT_KEY";
    public static final String POST_COLLECTION = "Posts";
    public static final String POST_IMAGES_STORAGE = "postImages";
    public static final String UP_VOTES = "upVotes";
    public static final String CATEGORY_KEY = "category";
    public static final String COMMENT_COUNTER = "commentCounter";
    private String userName;
    private String title;
    private String description;
    public static final Creator<PostModel> CREATOR = new Creator<PostModel>() {
        @Override
        public PostModel createFromParcel(Parcel in) {
            return new PostModel(in);
        }

        @Override
        public PostModel[] newArray(int size) {
            return new PostModel[size];
        }
    };
    private String userID;
    private String postKey;
    private String category;
    private Long createdTime;
    private Boolean postPhoto;
    private Integer commentCounter;
    public Map<String, Integer> upVotes;

    public PostModel() {

    }

    private Integer upVotesCounter;


    public PostModel(String postKey, String userID, int upVotesCounter, String userName, String title, String description, String category, Boolean postPhoto, int commentCounter) {
        this.postKey = postKey;
        this.userID = userID;
        this.userName = userName;
        this.title = title;
        this.description = description;
        this.category = category;
        this.postPhoto = postPhoto;
        Date temp = Calendar.getInstance().getTime();
        this.createdTime = temp.getTime();
        this.upVotes = new HashMap<>();
        this.commentCounter = commentCounter;
        this.upVotesCounter = upVotesCounter;
    }

    protected PostModel(Parcel in) {
        userName = in.readString();
        title = in.readString();
        description = in.readString();
        byte tmpPostPhoto = in.readByte();
        postPhoto = tmpPostPhoto == 0 ? null : tmpPostPhoto == 1;
        userID = in.readString();
        postKey = in.readString();
        category = in.readString();
        if (in.readByte() == 0) {
            createdTime = null;
        } else {
            createdTime = in.readLong();
        }
        if (in.readByte() == 0) {
            commentCounter = null;
        } else {
            commentCounter = in.readInt();
        }
        if (in.readByte() == 0) {
            upVotesCounter = null;
        } else {
            upVotesCounter = in.readInt();
        }
    }

    public Map<String, Integer> getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(Map<String, Integer> upVotes) {
        this.upVotes = upVotes;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public Boolean getPostPhoto() {
        return postPhoto;
    }

    public void setPostPhoto(Boolean postPhoto) {
        this.postPhoto = postPhoto;
    }

    public Integer getUpVotesCounter() {
        return upVotesCounter;
    }

    public void setUpVotesCounter(Integer upVotesCounter) {
        this.upVotesCounter = upVotesCounter;
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

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getCommentCounter() {
        return commentCounter;
    }

    public void setCommentCounter(Integer commentCounter) {
        this.commentCounter = commentCounter;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(userName);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeByte((byte) (postPhoto == null ? 0 : postPhoto ? 1 : 2));
        parcel.writeString(userID);
        parcel.writeString(postKey);
        parcel.writeString(category);
        if (createdTime == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(createdTime);
        }
        if (commentCounter == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(commentCounter);
        }
        if (upVotesCounter == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(upVotesCounter);
        }
    }


}
