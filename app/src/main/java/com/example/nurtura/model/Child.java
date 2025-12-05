package com.example.nurtura.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Child {
    @Exclude
    private String id;
    private DocumentReference parentId;
    private String name;
    private String gender;

    @ServerTimestamp
    private Date dateOfBirth;

    private String bloodType;
    private List<String> allergies;

    private double latestWeight;
    private double latestHeight;

    public Child(String id, DocumentReference parentId, String name, String gender, Date dateOfBirth, String bloodType, List<String> allergies, double latestWeight, double latestHeight) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.latestWeight = latestWeight;
        this.latestHeight = latestHeight;
    }

    public Child() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DocumentReference getParentId() {
        return parentId;
    }

    public void setParentId(DocumentReference parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    public double getLatestWeight() {
        return latestWeight;
    }

    public void setLatestWeight(double latestWeight) {
        this.latestWeight = latestWeight;
    }

    public double getLatestHeight() {
        return latestHeight;
    }

    public void setLatestHeight(double latestHeight) {
        this.latestHeight = latestHeight;
    }
}
