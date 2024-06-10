package com.example.kouch.Model;

import com.google.firebase.firestore.PropertyName;

public class User {
    private String id;
    private String FName;
    private String LName;
    private String email;
    private String phone;
    private String password;
    private String photoUrl;
    private Object createdTimeStamp;

    public User() {
    }

    public User(String id, String FName, String LName, String email, String phone, String password, String photoUrl, Object createdTimeStamp) {
        this.id = id;
        this.FName = FName;
        this.LName = LName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.photoUrl = photoUrl;
        this.createdTimeStamp = createdTimeStamp;
    }

    @PropertyName("id")
    public String getId() {
        return id;
    }

    @PropertyName("id")
    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("FName")
    public String getFName() {
        return FName;
    }

    @PropertyName("FName")
    public void setFName(String FName) {
        this.FName = FName;
    }

    @PropertyName("LName")
    public String getLName() {
        return LName;
    }

    @PropertyName("LName")
    public void setLName(String LName) {
        this.LName = LName;
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("phone")
    public String getPhone() {
        return phone;
    }

    @PropertyName("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @PropertyName("password")
    public String getPassword() {
        return password;
    }

    @PropertyName("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @PropertyName("photoUrl")
    public String getPhotoUrl() {
        return photoUrl;
    }

    @PropertyName("photoUrl")
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @PropertyName("createdTimeStamp")
    public Object getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    @PropertyName("createdTimeStamp")
    public void setCreatedTimeStamp(Object createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }
}
