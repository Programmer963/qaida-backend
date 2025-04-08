package com.qaida.backend.service;

import com.qaida.backend.model.RecommendationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class RecommendationService {

    private final RestTemplate restTemplate;

    @Value("${openweather.api.key}")
    private String openWeatherApiKey;

    @Value("${google.api.key}")
    private String googleApiKey;

    public RecommendationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RecommendationResponse getRecommendations(String city) {
//        String weather = getWeatherForCity(city);
//
//        RecommendationResponse response = new RecommendationResponse();
//        response.setCity(city);
//        response.setWeather(weather);
//        response.setPlaces(Arrays.asList("Центральный парк", "Музей искусств", "Кафе CoffeeTime"));
//        return response;
        String weather = getWeatherForCity(city);
        double[] coords = getCoordinates(city);

        List<String> places = (coords != null)
                ? getNearbyPlaces(coords[0], coords[1])
                : Arrays.asList("Парк", "Музей", "Кафе");

        RecommendationResponse response = new RecommendationResponse();
        response.setCity(city);
        response.setWeather(weather);
        response.setPlaces(places);

        return response;
    }

    private String getWeatherForCity(String city) {
        try {
            String url = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=ru",
                    city, openWeatherApiKey
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = new JSONObject(response.getBody());

            String description = json.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("description");

            double temp = json.getJSONObject("main").getDouble("temp");

            return String.format("%s, %.1f°C", description, temp);
        }
        catch (Exception e) {
            return "Не удалось получить данные о погоде";
        }
    }

    private double[] getCoordinates(String city) {
        try {
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
                    city, googleApiKey
            );
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = new JSONObject(response.getBody());

            if (!json.getString("status").equals("OK")) return null;

            JSONObject location = json.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONObject("location");

            return new double[] {
                    location.getDouble("lat"),
                    location.getDouble("lng")
            };
        } catch (JSONException e) {
            return null;
        }
    }

    private List<String> getNearbyPlaces(double lat, double lng) {
        List<String> types = Arrays.asList("tourist_attraction", "park", "cafe", "museum");
        List<String> results = new ArrayList<>();

        for (String type : types) {
            try {
                String url = String.format(
                        Locale.US,
                        "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=8000&type=%s&key=%s",
                        lat, lng, type, googleApiKey
                );


                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                JSONObject json = new JSONObject(response.getBody());

                String status = json.getString("status");
                System.out.println("Google API (" + type + ") status: " + status);

                if (!"OK".equals(status)) {
                    continue;
                }

                JSONArray jsonArray = json.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);
                    if (place.has("name")) {
                        results.add(place.getString("name"));
                    }
                }
                System.out.println(results);
            } catch (Exception e) {
                System.out.println("Error for type " + type + ": " + e.getMessage());
            }
        }

        System.out.println("Final places: " + results);
        return results;
    }

}
