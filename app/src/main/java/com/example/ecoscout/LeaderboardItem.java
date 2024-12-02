package com.example.ecoscout;

public class LeaderboardItem {
    private String name;
    private int points;
    private int imageResourceId;

    public LeaderboardItem(String name, int points, int imageResourceId) {
        this.name = name;
        this.points = points;
        this.imageResourceId = imageResourceId;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    // Optional Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
