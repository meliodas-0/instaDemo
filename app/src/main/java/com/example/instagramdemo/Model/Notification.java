package com.example.instagramdemo.Model;

public class Notification {

    private String userId;
    private String description;
    private String postId;
    private boolean isPost;

    public Notification(String userId, String description, String postId, boolean isPost) {
        this.userId = userId;
        this.description = description;
        this.postId = postId;
        this.isPost = isPost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
