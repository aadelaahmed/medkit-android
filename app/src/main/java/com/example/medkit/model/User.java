package com.example.medkit.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class User implements Parcelable {
     public final static String USER_ID = "uid";
    public final static String FULLNAME = "fullName";
     public final static String IMGURL = "imgUrl";
    public final static String BIO = "BIO_KEY";
    public static final String USER_COLLECTION = "Users";
    public static final String USER_IMAGES_STORAGE = "userPhoto";
    public static final String OBJECT_KEY = "OBJECT_KEY";
    public static final String COMMENT_COUNTER = "commentCounter";
    public static final String CLAPPING_COUNTER = "clappingCounter";

    /*
     public final static String AGE = "AGE";
     public final static String GENDER = "GENDER";*/
    public final static String G_YEAR = "g-year";
    public final static String G_FACULTY = "g-faculty";
    public final static String SPECIALITY = "speciality";
    public final static String USERTYPE = "usertype";
    public static final String LOCATION = "location";
    public final static String CREATED_TIME = "CREATED_TIME";
    public static final String IS_DOCTOR = "IS_DOCTOR";
    public static final String IS_CLICKED = "IS_CLICKED";
    public static final String EMAIL = "EMAIL";
    public static final String USER_PHOTO = "USER_PHOTO";
    public static final String NORMAL_REGISTER = "NORMAL_REGISTER";
    // private Map<String,String> userType;
    //private Map<String,String> generalInfo;
   /* private int age;
    private String createdTime;
    */
    private String userId;
    private long createdTime;
    private String email;
    private String fullName;
    private Map<String, Object> userType;
    private int clappingCounter;
    private int commentCounter;
    public User() {

    }


    protected User(Parcel in) {
        userId = in.readString();
        createdTime = in.readLong();
        email = in.readString();
        fullName = in.readString();
        clappingCounter = in.readInt();
        commentCounter = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    //private String photoUrl;
    public String getUid() {
        return userId;
    }

    public User(int clappingCounter, int commentCounter, String email, String fullName, String userId, Map<String, Object> userType) {
        this.userType = userType;
        this.email = email;
        this.fullName = fullName;
        this.userId = userId;
        this.clappingCounter = clappingCounter;
        this.commentCounter = commentCounter;
        Date tempDate = Calendar.getInstance().getTime();
        this.createdTime = tempDate.getTime();
    }

    public void setUid(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Map<String, Object> getUserType() {
        return userType;
    }

    public void setUserType(Map<String, Object> userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getClappingCounter() {
        return clappingCounter;
    }

    public void setClappingCounter(int clappingCounter) {
        this.clappingCounter = clappingCounter;
    }

    public int getCommentCounter() {
        return commentCounter;
    }

    public void setCommentCounter(int commentCounter) {
        this.commentCounter = commentCounter;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeLong(createdTime);
        parcel.writeString(email);
        parcel.writeString(fullName);
        parcel.writeInt(clappingCounter);
        parcel.writeInt(commentCounter);
    }
}
