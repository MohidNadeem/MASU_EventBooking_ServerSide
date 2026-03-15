package com.mohid.masu.model;

public class Booking {

    private String id;
    private String eventId;
    private String studentId;
    private String bookingType; // STUDENT or ALUMNI
    private String bookingDate;

    public Booking() {
    }

    public Booking(String id, String eventId, String studentId, String bookingType, String bookingDate) {
        this.id = id;
        this.eventId = eventId;
        this.studentId = studentId;
        this.bookingType = bookingType;
        this.bookingDate = bookingDate;
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

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
}