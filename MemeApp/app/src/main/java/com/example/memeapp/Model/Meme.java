package com.example.memeapp.Model;

public class Meme {

    private String memeId;
    private String picture;
    private String title;
    private String userId;
    private String userPhoto;
    private Object postedAt;

    public Meme() {

    }

    public Meme(String picture, String title, String userId, String userPhoto) {
        this.picture = picture;
        this.title = title;
        this.userId = userId;
        this.userPhoto = userPhoto;
    }

    public void setMemeId(String memeId) {
        this.memeId = memeId;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setPostedAt(Object postedAt) {
        this.postedAt = postedAt;
    }

    public String getMemeId() {
        return memeId;
    }

    public String getPicture() {
        return picture;
    }

    public String getTitle() {
        return title;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Object getPostedAt() {
        return postedAt;
    }
}
