package com.example.ltapp;

public class Comment {
    private String userName;
    private String content;
    private float rating;

    public Comment(String userName, String content, float rating) {
        this.userName = userName;
        this.content = content;
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public String getContent() {
        return content;
    }

    public float getRating() {
        return rating;
    }
} 