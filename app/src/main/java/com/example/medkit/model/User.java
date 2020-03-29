package com.example.medkit.model;

import java.util.Map;

public class User {
    public static String LOCATION = "LOCATION";
    public static String AGE = "AGE";
    public static String GENDER = "GENDER";
    public static String G_YEAR = "G_YEAR";
    public static String G_FACULTY = "G_FACULTY";
    public static String SPECIALITY = "SPECIALITY";
    public static String USERTYPE = "USERTYPE";
    private String createdTime;
    private String email;
    //private Map<String,String> generalInfo;
    private String age;
    // private Map<String,String> userType;
    private String gender;
    private String lastSignIn;
    private String userId;
    private String gradFaculty;
    private String gradYear;
    private String speciality;
    private String userType;


   /* public User(String createdTime, String email, Map<String, String> generalInfo, String lastSignIn, String userId, Map<String, String> userType) {
        this.createdTime = createdTime;
        this.email = email;
        this.generalInfo = generalInfo;
        this.lastSignIn = lastSignIn;
        this.userId = userId;
        this.userType = userType;
    }*/

    public User(String createdTime, String email, String age, String gender, String lastSignIn, String userId, String gradFaculty, String gradYear, String speciality, String userType) {
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
    }

    public User(String createdTime, String email, String userId) {
        this.createdTime = createdTime;
        this.email = email;
        this.userId = userId;
    }

    public User(String email, String userId) {
        this.email = email;
        this.userId = userId;
    }

    public User() {
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

   /* public Map<String, String> getGeneralInfo() {
        return generalInfo;
    }

    public void setGeneralInfo(Map<String, String> generalInfo) {
        this.generalInfo = generalInfo;
    }*/

    public String getLastSignIn() {
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
    }

   /* public Map<String, String> getUserType() {
        return userType;
    }

    public void setUserType(Map<String, String> userType) {
        this.userType = userType;
    }*/

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGradFaculty() {
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
    }
}
