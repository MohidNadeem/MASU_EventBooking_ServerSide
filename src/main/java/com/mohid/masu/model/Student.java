package com.mohid.masu.model;

public class Student {

    private String id;
    private String username;
    private String password;
    private String fullName;
    private String status; // ACTIVE or ALUMNI

    public Student() {
    }

    public Student(String id, String username, String password, String fullName, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}