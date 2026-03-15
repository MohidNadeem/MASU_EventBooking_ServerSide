package com.mohid.masu.model;

public class Rating {

    private String id;
    private String eventId;
    private String studentId;
    private int stars;

    public Rating() {
    }

    public Rating(String id, String eventId, String studentId, int stars) {
        this.id = id;
        this.eventId = eventId;
        this.studentId = studentId;
        this.stars = stars;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}