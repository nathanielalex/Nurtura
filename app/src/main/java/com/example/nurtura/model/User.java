package com.example.nurtura.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class User {
    @Exclude
    private String id;
    private String name;
    private String email;
    @ServerTimestamp
    private Date lastLogin;

    public User(String id, String name, String email, Date lastLogin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.lastLogin = lastLogin;
    }

    public User() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}
