package com.example.ecoscout;

public class LitterReport {
    private String userId;
    private String litterType;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private long timestamp;
    private int points;

    public LitterReport() {
        // Default constructor required for calls to DataSnapshot.getValue(LitterReport.class)
    }

    public LitterReport(String userId, String litterType, double latitude, double longitude, String imageUrl, int points) {
        this.userId = userId;
        this.litterType = litterType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.timestamp = System.currentTimeMillis();
        this.points = points;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLitterType() {
        return litterType;
    }
}