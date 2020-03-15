package com.example.instagramdemo.Model;

import android.graphics.Bitmap;

public class User {

    private String id;
    private String username;
    private Bitmap imageurl;
    private String bio = "";

    public User(String id, String username, Bitmap imageurl) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Bitmap getImageurl() {
        return imageurl;
    }

    public void setImageurl(Bitmap imageurl) {
        this.imageurl = imageurl;
    }

    public User(String id, String username, Bitmap imageurl, String bio) {
        this.id = id;
        this.username = username;
        this.imageurl = imageurl;
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
