package com.example.instagramdemo.Model;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.List;

public class Story implements Comparable<Story>{

    private Bitmap image;
    private String storyId;
    private String storyBy;
    private Date createdAt;
    private List<String> seenBy;

    public Story(Bitmap image,  String storyId, String storyBy, Date createdAt, List<String> seenBy) {
        this.image = image;
        this.storyId = storyId;
        this.storyBy = storyBy;
        this.createdAt = createdAt;
        this.seenBy = seenBy;
    }

    public List<String> getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(List<String> seenBy) {
        this.seenBy = seenBy;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int compareTo(Story story) {
        return this.createdAt.compareTo(story.createdAt);
    }
}
