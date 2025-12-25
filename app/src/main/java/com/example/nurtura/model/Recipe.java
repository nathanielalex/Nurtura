package com.example.nurtura.model;

import java.io.Serializable;
import java.util.List;

public class Recipe implements Serializable {
    private int id;
    private String title;
    private String imageUrl;
    private int readyInMinutes;
    private List<String> ingredients;
    private List<String> instructions;

    public Recipe(int id, String title, String imageUrl, int readyInMinutes, List<String> ingredients, List<String> instructions) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.readyInMinutes = readyInMinutes;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(int readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
}
