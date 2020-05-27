package com.example.medkit.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

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
    private String userName;
    private String title;
    private String description;
    private String postPhoto;
    private String userPhoto;
    private String userID;
    private String postKey;
    private String category;
    private Bitmap userProfilePicture;
    private Bitmap image;
    private int nComments;
    private Long createdTime;
    public Map<String, Integer> upVotes;


    public PostModel() {

    }


    protected PostModel(Parcel in) {
        userName = in.readString();
        title = in.readString();
        description = in.readString();
        postPhoto = in.readString();
        userPhoto = in.readString();
        userID = in.readString();
        postKey = in.readString();
        category = in.readString();
        userProfilePicture = in.readParcelable(Bitmap.class.getClassLoader());
        image = in.readParcelable(Bitmap.class.getClassLoader());
        nComments = in.readInt();
        if (in.readByte() == 0) {
            createdTime = null;
        } else {
            createdTime = in.readLong();
        }
    }

    public PostModel(String title, String description, String userName, String postPhoto, String userID, String category) {
        this.title = title;
        this.description = description;
        this.userName = userName;
        this.postPhoto = postPhoto;
        this.userID = userID;
        this.category = category;
        Date temp = Calendar.getInstance().getTime();
        this.createdTime = temp.getTime();
        this.upVotes = new HashMap<>();
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

    public void setCreatedTime(long createdTime) {
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
    public int getnComments() {
        return nComments;
    }

    public void setnComments(int nComments) {
        this.nComments = nComments;
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
        parcel.writeString(postPhoto);
        parcel.writeString(userPhoto);
        parcel.writeString(userID);
        parcel.writeString(postKey);
        parcel.writeString(category);
        parcel.writeParcelable(userProfilePicture, i);
        parcel.writeParcelable(image, i);
        parcel.writeInt(nComments);
        if (createdTime == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(createdTime);
        }
    }
}
