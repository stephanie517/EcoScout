package com.example.ecoscout;

public class ProfileData {
    private String name;
    private String email;
    private String profileImagePath;

    // Default constructor
    public ProfileData() {
        this.name = "EcoScout User";
        this.email = "user@ecoscout.com";
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}