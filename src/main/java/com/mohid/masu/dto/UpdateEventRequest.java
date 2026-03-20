package com.mohid.masu.dto;

public class UpdateEventRequest {

    private String date;
    private String startTime;
    private String endTime;
    private String description;
    private String duration;
    private double cost;
    private int maxParticipants;
    private int alumniReservedSlots;

    public UpdateEventRequest() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getAlumniReservedSlots() {
        return alumniReservedSlots;
    }

    public void setAlumniReservedSlots(int alumniReservedSlots) {
        this.alumniReservedSlots = alumniReservedSlots;
    }
}