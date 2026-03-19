package com.mohid.masu.model;

public class Event {

    private String id;
    private String publisherId;
    private String title;
    private String type;
    private String date;
    private String startTime;
    private String endTime;
    private String venueName;
    private String location;
    private String country;
    private double latitude;
    private double longitude;
    private String postalCode;
    private String description;
    private String gender;
    private String currency;
    private String duration;
    private double cost;
    private int maxParticipants;
    private int alumniReservedSlots;
    private String status;

    private int remainingSeats;
    private int remainingAlumniSlots;

    public Event() {
    }

    public Event(String id, String publisherId, String title, String type, String date,
                 String startTime, String endTime, String venueName, String location,
                 String country, double latitude, double longitude, String postalCode, String description,
                 String gender, String currency, String duration, double cost,
                 int maxParticipants, int alumniReservedSlots, String status) {
        this.id = id;
        this.publisherId = publisherId;
        this.title = title;
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.venueName = venueName;
        this.location = location;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postalCode = postalCode;
        this.description = description;
        this.gender = gender;
        this.currency = currency;
        this.duration = duration;
        this.cost = cost;
        this.maxParticipants = maxParticipants;
        this.alumniReservedSlots = alumniReservedSlots;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRemainingSeats() {
        return remainingSeats;
    }

    public void setRemainingSeats(int remainingSeats) {
        this.remainingSeats = remainingSeats;
    }

    public int getRemainingAlumniSlots() {
        return remainingAlumniSlots;
    }

    public void setRemainingAlumniSlots(int remainingAlumniSlots) {
        this.remainingAlumniSlots = remainingAlumniSlots;
    }
}