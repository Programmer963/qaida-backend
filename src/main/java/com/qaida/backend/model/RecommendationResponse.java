package com.qaida.backend.model;

import java.util.List;

public class RecommendationResponse {
    private String city;
    private String weather;
    private List<String> places;

    // getters and setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    public List<String> getPlaces() { return places; }
    public void setPlaces(List<String> places) { this.places = places; }
}
