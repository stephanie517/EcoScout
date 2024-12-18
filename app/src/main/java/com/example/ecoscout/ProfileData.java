package com.example.ecoscout;

public class ProfileData {
    private static ProfileData instance; // Singleton instance
    private String name;
    private String email;
    private String profileImagePath;
    private int litterReportCount;
    private int totalPoints;
    private int eventsJoined;// Add this field

    // Private constructor to prevent instantiation
    private ProfileData() {
        this.name = "EcoScout User";
        this.email = "user@ecoscout.com";
        this.litterReportCount = 0;
        this.totalPoints = 0;
        this.eventsJoined = 0;
    }

    // Method to get the singleton instance
    public static ProfileData getInstance() {
        if (instance == null) {
            instance = new ProfileData();
        }
        return instance;
    }

    // Add methods to manage points and events
    public int getTotalPoints() {
        return totalPoints;
    }

    public void addPoints(int points) {
        this.totalPoints += points;
    }

    public void setTotalPoints(int points) {
        this.totalPoints = points;
    }

    public void setEventsJoined(int events) {
        this.eventsJoined = events;
    }

    public int getEventsJoined() {
        return eventsJoined;
    }

    public void incrementEventsJoined() {
        this.eventsJoined++;
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