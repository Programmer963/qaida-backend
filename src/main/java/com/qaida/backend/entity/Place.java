package com.qaida.backend.entity;

public class Place {
    private String name;
    private String address;
    private double rating;
    private String photoUrl;
    private String openingHours;
    private String type;

    public Place(String name, String address, String photoUrl, double rating, String openingHours, String type) {
        this.name = name;
        this.address = address;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.openingHours = openingHours;
        this.type = type;
    }

    public Place() {

    }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }


    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
