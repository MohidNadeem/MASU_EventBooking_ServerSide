package com.mohid.masu.model;

public class Student {

    private String id;
    private String username;
    private String password;
    private String fullName;
    private String gender;
    private String status; // ACTIVE or ALUMNI
    private boolean passwordUpdatedByStudent;

    public Student() {
    }

    public Student(String id, String username, String password, String fullName, String gender, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.gender = gender;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isPasswordUpdatedByStudent() {
        return passwordUpdatedByStudent;
    }

    public void setPasswordUpdatedByStudent(boolean passwordUpdatedByStudent) {
        this.passwordUpdatedByStudent = passwordUpdatedByStudent;
    }
}