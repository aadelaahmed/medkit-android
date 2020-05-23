package com.example.medkit.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class User {
     public final static String USER_ID = "uid";
     public final static String FULLNAME = "fullname";
     public final static String IMGURL = "imgUrl";
    public final static String BIO = "BIO_KEY";
    public static final String USER_COLLECTION = "USER_COLLECTION";
    public static final String USER_IMAGES_STORAGE = "userPhoto";
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

    public User() {

    }

    //private String photoUrl;
    public String getUid() {
        return userId;
    }

    public User(Map<String, Object> userType, String email, String fullName) {
        this.userType = userType;
        this.email = email;
        this.fullName = fullName;
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

}
