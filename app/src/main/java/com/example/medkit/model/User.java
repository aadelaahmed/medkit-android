package com.example.medkit.model;

import java.util.Map;

public class User {
    /* public final static String USER_ID = "uid";
     public final static String FULLNAME = "fullname";
     public final static String IMGURL = "imgUrl";
     public final static String LOCATION = "LOCATION";
     public final static String AGE = "AGE";
     public final static String GENDER = "GENDER";*/
    public final static String G_YEAR = "g-year";
    public final static String G_FACULTY = "g-faculty";
    public final static String SPECIALITY = "speciality";
    public final static String USERTYPE = "usertype";
    public static final String LOCATION = "location";
    public final static String CREATED_TIME = "CREATED_TIME";
    public static final String IS_DOCTOR = "IS_DOCTOR";
    public static final String EMAIL = "EMAIL";
    // private Map<String,String> userType;
    //private Map<String,String> generalInfo;
   /* private int age;
    private String createdTime;
    private String email;
    private String fullName;
    private String gender;
    private String imgUrl;
    private String userId;
    private String lastSignIn;
    private String gradFaculty;
    private String gradYear;
    private String speciality;
    private String userType;*/

    private String createdTime;
    /* private String gradFaculty;
     private String gradYear;
     private String speciality;
     private String userType;*/
    private Map<String, Object> userType;

   /* public User(String createdTime, String gradFaculty, String gradYear, String speciality, String userType) {
        this.createdTime = createdTime;
        this.gradFaculty = gradFaculty;
        this.gradYear = gradYear;
        this.speciality = speciality;
        this.userType = userType;
    }*/



  /*  public User(String createdTime, String userType) {
        this.createdTime = createdTime;
        this.userType = userType;
    }*/

    public User(String createdTime, Map<String, Object> userType) {
        this.createdTime = createdTime;
        this.userType = userType;
    }




    /*public User(int age, String createdTime, String email, String fullName, String gender, String imgUrl, String userId, String gradFaculty, String gradYear, String speciality, String userType) {
        this.age = age;
        this.createdTime = createdTime;
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.imgUrl = imgUrl;
        this.userId = userId;

        this.gradFaculty = gradFaculty;
        this.gradYear = gradYear;
        this.speciality = speciality;
        this.userType = userType;
    }*/

    /* public User(String createdTime, String email, Map<String, String> generalInfo, String lastSignIn, String userId, Map<String, String> userType) {
        this.createdTime = createdTime;
        this.email = email;
        this.generalInfo = generalInfo;
        this.lastSignIn = lastSignIn;
        this.userId = userId;
        this.userType = userType;
    }*/



  /*  public User(String createdTime, String email, String age, String gender, String lastSignIn, String userId, String gradFaculty, String gradYear, String speciality, String userType) {
        this.createdTime = createdTime;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.lastSignIn = lastSignIn;
        this.userId = userId;
        this.gradFaculty = gradFaculty;
        this.gradYear = gradYear;
        this.speciality = speciality;
        this.userType = userType;
    }*/

    public Map<String, Object> getMapType() {
        return userType;
    }

    public void setMapType(Map<String, Object> userType) {
        this.userType = userType;
    }

    public User() {
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

 /*   public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }*/

   /* public Map<String, String> getGeneralInfo() {
        return generalInfo;
    }

    public void setGeneralInfo(Map<String, String> generalInfo) {
        this.generalInfo = generalInfo;
    }*/

  /*  public String getLastSignIn() {
        return lastSignIn;
    }

    public void setLastSignIn(String lastSignIn) {
        this.lastSignIn = lastSignIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }*/

   /* public Map<String, String> getUserType() {
        return userType;
    }

    public void setUserType(Map<String, String> userType) {
        this.userType = userType;
    }*/

  /*  public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }*/

   /* public String getGradFaculty() {
        return gradFaculty;
    }

    public void setGradFaculty(String gradFaculty) {
        this.gradFaculty = gradFaculty;
    }

    public String getGradYear() {
        return gradYear;
    }

    public void setGradYear(String gradYear) {
        this.gradYear = gradYear;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }*/

   /* public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }*/
}