package com.kelompoklima.cinemape;

public class Movie {
    private String title;
    private String description;
    private String imageUrl; // Nanti untuk API

    public Movie(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
}