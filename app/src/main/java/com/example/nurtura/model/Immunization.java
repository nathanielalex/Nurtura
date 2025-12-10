package com.example.nurtura.model;

public class Immunization {
    private String name;
    private String schedule;
    private String dueStatus;
    private String date;

    public Immunization(String name, String schedule, String dueStatus, String date) {
        this.name = name;
        this.schedule = schedule;
        this.dueStatus = dueStatus;
        this.date = date;
    }

    public String getName() { return name; }
    public String getSchedule() { return schedule; }
    public String getDueStatus() { return dueStatus; }
    public String getDate() { return date; }
}
