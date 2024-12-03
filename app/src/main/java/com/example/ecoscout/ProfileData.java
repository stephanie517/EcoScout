package com.example.ecoscout;

public class ProfileData {
    private static ProfileData instance; // Singleton instance
    private String name;
    private String email;
    private String profileImagePath;
    private int litterReportCount; // Add this field

    // Private constructor to prevent instantiation
    private ProfileData() {
        this.name = "EcoScout User";
        this.email = "user@ecoscout.com";
        this.litterReportCount = 0; // Initialize count
    }

    // Method to get the singleton instance
    public static ProfileData getInstance() {
        if (instance == null) {
            instance = new ProfileData();
        }
        return instance;
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

    public int getLitterReportCount() { // Getter for litter report count
        return litterReportCount;
    }

    public void setLitterReportCount(int count) { // Setter for litter report count
        this.litterReportCount = count;
    }
}