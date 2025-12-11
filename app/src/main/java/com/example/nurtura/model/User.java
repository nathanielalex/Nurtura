package com.example.nurtura.model;

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
    private String phoneNumber;
    private String role;

    public User(String id, String name, String email, Date lastLogin, String phoneNumber, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.lastLogin = lastLogin;
        this.phoneNumber = phoneNumber;
        this.role = role;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
