package com.example.tmates;

public class SportLocation {
    private String name, description;
    private double latitude, longitude;

    // Constructor.
    public SportLocation(String name, double latitude, double longitude, String description){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    // Getters and setters.
    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
