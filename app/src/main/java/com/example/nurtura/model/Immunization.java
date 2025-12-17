package com.example.nurtura.model;

public class Immunization {
    private String id;
    private String name;
    private String schedule;
    private String dueStatus;
    private String date;
    private boolean isCompleted;

    public Immunization() {}

    public Immunization(String id, String name, String schedule, String dueStatus, String date, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.schedule = schedule;
        this.dueStatus = dueStatus;
        this.date = date;
        this.isCompleted = isCompleted;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSchedule() { return schedule; }
    public String getDueStatus() { return dueStatus; }
    public String getDate() { return date; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
