package com.lms.cdac.entities;

public enum Badge {
    GREEN("Excellent", "#10B981"), // Green
    BLUE("Good", "#3B82F6"),      // Blue
    YELLOW("Average", "#FBBF24"); // Yellow

    private final String label;
    private final String color;

    Badge(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }
} 