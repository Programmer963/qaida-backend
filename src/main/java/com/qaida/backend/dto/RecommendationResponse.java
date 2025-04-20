package com.qaida.backend.dto;

import com.qaida.backend.entity.Place;

import java.util.List;

public class RecommendationResponse {
    private String city;
    private String weather;
    private List<Place> places;

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    public List<Place> getPlaces() { return places; }
    public void setPlaces(List<Place> places) { this.places = places; }
}
