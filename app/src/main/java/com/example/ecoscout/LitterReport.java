package com.example.ecoscout;

public class LitterReport {
    private String litterType;
    private double latitude;
    private double longitude;

    // Default constructor required for calls to DataSnapshot.getValue(LitterReport.class)
    public LitterReport() {
    }

    public LitterReport(String litterType, double latitude, double longitude) {
        this.litterType = litterType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLitterType() {
        return litterType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}