package com.example.nurtura.model;

import com.google.firebase.firestore.Exclude;

public class Article {
    @Exclude
    private String id;
    private String title;
    private String subtitle;
    private String content;
    private String imageUrl;

    //need empty constructor for firestore
    public Article() { }

    public Article(String id, String title, String subtitle, String content, String imageUrl) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
