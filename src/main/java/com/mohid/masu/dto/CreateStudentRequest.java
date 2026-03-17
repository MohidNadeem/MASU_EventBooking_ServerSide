package com.mohid.masu.dto;

public class CreateStudentRequest {

    private String username;
    private String password;
    private String fullName;
    private String gender;
    private boolean passwordUpdatedByStudent;

    public CreateStudentRequest() {
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
    
    public boolean isPasswordUpdatedByStudent() {
        return passwordUpdatedByStudent;
    }

    public void setPasswordUpdatedByStudent(boolean passwordUpdatedByStudent) {
        this.passwordUpdatedByStudent = passwordUpdatedByStudent;
    }
}