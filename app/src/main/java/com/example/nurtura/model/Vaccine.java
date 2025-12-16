package com.example.nurtura.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class Vaccine {
    @Exclude
    private String id;
    private String name;
    private String description;
    private Long recommendedAgeInMonths;
    private boolean mandatory;
    private String sideEffectsInfo;

    public Vaccine() { }

    public Vaccine(String id, String name, String description, int recommendedAgeInMonths, boolean mandatory, String sideEffectsInfo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.recommendedAgeInMonths = (long) recommendedAgeInMonths;
        this.mandatory = mandatory;
        this.sideEffectsInfo = sideEffectsInfo;
    }

    @Exclude
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Exclude
    public int getRecommendedAgeInMonthsInt() {
        return recommendedAgeInMonths != null ? recommendedAgeInMonths.intValue() : 0;
    }

    @PropertyName("recommendedAgeInMonths")
    public Long getRecommendedAgeInMonths() { return recommendedAgeInMonths; }

    @PropertyName("recommendedAgeInMonths")
    public void setRecommendedAgeInMonths(Long recommendedAgeInMonths) {
        this.recommendedAgeInMonths = recommendedAgeInMonths;
    }

    public boolean isMandatory() { return mandatory; }
    public void setMandatory(boolean mandatory) { this.mandatory = mandatory; }

    public String getSideEffectsInfo() { return sideEffectsInfo; }
    public void setSideEffectsInfo(String sideEffectsInfo) { this.sideEffectsInfo = sideEffectsInfo; }
}
