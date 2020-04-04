package com.example.instagramdemo.Model;

import android.graphics.Bitmap;

public class User {

    private String id;
    private String fullname;
    private String username;
    private Bitmap image;
    private String bio;

    public User(String id, String username, String fullname, Bitmap image, String bio) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.image = image;
        this.bio = bio;
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

    public Bitmap getImage() {
        return image;
    }

    public User(String id, String username, Bitmap image, String bio) {
        this.id = id;
        this.username = username;
        this.image = image;
        this.bio = bio;
    }

    public String getBio() {
        return bio;
    }

    public String getFullName() {
        return fullname;
    }
}
