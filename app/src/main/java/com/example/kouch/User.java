package com.example.kouch;

public class User {
    private String id;
    private String SName, FName, LName, Gender, birthday, email, phone, password;
    private String photoUrl;

    public User() {
    }

    public User(String id, String SName, String FName, String LName, String Gender, String birthday, String email, String phone, String password, String photoUrl) {
        this.id = id;
        this.SName = SName;
        this.FName = FName;
        this.LName = LName;
        this.Gender = Gender;
        this.birthday = birthday;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.photoUrl = photoUrl;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public String getSName() {
        return SName;
    }

    public void setSName(String SName) {
        this.SName = SName;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
