package com.example.instagramdemo.Model;

import android.graphics.Bitmap;

public class Post {


    private String username;
    private String description;
    private String postId;
    private Bitmap image;

    public Post( String username, String description, String postId, Bitmap image) {

        this.username = username;
        this.description = description;
        this.postId = postId;
        this.image = image;
    }

    public Post() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostId() {
        return postId;
    }

    public void setUserId(String userId) {
        this.postId = userId;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
