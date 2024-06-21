package com.example.kouch.Model;

import com.google.firebase.firestore.PropertyName;

public class User {
    private String id;
    private String FName;
    private String Role;
    private String email;
    private String city;
    private String password;
    private String photoUrl;
    private Object createdTimeStamp;
    private String fcmToken;
    private String status;
    private String addres;

    public User() {
    }

    public User(String id, String FName, String Role, String email, String city, String password, String photoUrl, Object createdTimeStamp,String status,String addres) {
        this.id = id;
        this.FName = FName;
        this.Role = Role;
        this.email = email;
        this.city = city;
        this.password = password;
        this.photoUrl = photoUrl;
        this.createdTimeStamp = createdTimeStamp;
        this.status=status;
        this.addres=addres;
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

    @PropertyName("Role")
    public String getRole() {
        return Role;
    }

    @PropertyName("Role")
    public void setRole(String Role) {
        this.Role = Role;
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
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

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddres() {
        return addres;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }
}
