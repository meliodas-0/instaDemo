package com.example.instagramdemo.Model;

public class Comment {

    private String comment;
    private String publisher;
    private String commentId;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Comment(String comment, String publisher, String commentId) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentId = commentId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
