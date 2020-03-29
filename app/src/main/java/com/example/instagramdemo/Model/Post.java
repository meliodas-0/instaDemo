package com.example.instagramdemo.Model;

import android.graphics.Bitmap;

public class Post implements Comparable<Post> {


    private String username;
    private String description;
    private String postId;
    private Bitmap image;
    private long dateTime;

    public Post( String username, String description, String postId, Bitmap image, long dateTime) {

        this.username = username;
        this.description = description;
        this.postId = postId;
        this.image = image;
        this.dateTime = dateTime;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
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

    @Override
    public int compareTo(Post post) {
        Long number = this.dateTime;
        return - number.compareTo(post.dateTime);
    }
}
