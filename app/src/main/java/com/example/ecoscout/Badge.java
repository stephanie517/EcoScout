package com.example.ecoscout;

public class Badge {
    private String name;
    private int pointsRequired;
    private int badgeIcon;
    private boolean isUnlocked;

    public Badge(String name, int pointsRequired, int badgeIcon) {
        this.name = name;
        this.pointsRequired = pointsRequired;
        this.badgeIcon = badgeIcon;
        this.isUnlocked = false;
    }

    // Getters and setters
    public String getName() { return name; }
    public int getPointsRequired() { return pointsRequired; }
    public int getBadgeIcon() { return badgeIcon; }
    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }
}