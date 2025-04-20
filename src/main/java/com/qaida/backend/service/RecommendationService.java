package com.qaida.backend.service;

import com.qaida.backend.entity.Place;
import com.qaida.backend.dto.RecommendationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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


    public RecommendationResponse getRecommendationsByAddress(String address) {
        double[] coords = getCoordinates(address);

        if (coords == null) {
            RecommendationResponse response = new RecommendationResponse();
            response.setCity("Неизвестный адрес");
            response.setWeather("Не удалось получить погоду");
            response.setPlaces(List.of());
            return response;
        }

        String weather = getWeatherByAddressCoordinates(coords[0], coords[1]);
        List<String> placeTypes = getPlaceTypesByWeather(weather);
        List<Place> nearbyPlaces = getNearbyPlaces(coords[0], coords[1], placeTypes);

        RecommendationResponse response = new RecommendationResponse();
        response.setCity(address);
        response.setWeather(weather);
        response.setPlaces(nearbyPlaces);

        return response;
    }
    private String getWeatherByAddressCoordinates(double lat, double lon) {
        try {
            String url = String.format(
                    Locale.US,
                    "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric&lang=ru",
                    lat, lon, openWeatherApiKey
            );

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JSONObject json = new JSONObject(response.getBody());

            String description = json.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("description");

            double temp = json.getJSONObject("main").getDouble("temp");

            return String.format("%s, %.1f°C", description, temp);
        } catch (Exception e) {
            return "Не удалось получить данные о погоде";
        }
    }

    public RecommendationResponse getRecommendations(String city) {
        String weather = getWeatherForCity(city).toLowerCase();
        double[] coords = getCoordinates(city);

        List<String> recommendedTypes = getPlaceTypesByWeather(weather);

        List<Place> places = (coords != null)
                ? getNearbyPlaces(coords[0], coords[1], recommendedTypes)
                : new ArrayList<>();

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
        } catch (Exception e) {
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

            return new double[]{
                    location.getDouble("lat"),
                    location.getDouble("lng")
            };
        } catch (JSONException e) {
            return null;
        }
    }

    private List<String> getPlaceTypesByWeather(String weather) {
        if (weather.contains("дожд") || weather.contains("снег") || weather.contains("гроза")) {
            return Arrays.asList("cafe", "museum", "movie_theater", "shopping_mall", "library");
        } else if (weather.contains("ясно") || weather.contains("солнечно")) {
            return Arrays.asList("park", "tourist_attraction", "zoo", "amusement_park", "art_gallery");
        } else if (weather.contains("облачно") || weather.contains("пасмурно")) {
            return Arrays.asList("cafe", "museum", "movie_theater", "art_gallery");
        } else {
            return Arrays.asList("cafe", "museum", "movie_theater");
        }
    }

    private List<Place> getNearbyPlaces(double lat, double lng, List<String> types) {
        List<Place> results = new ArrayList<>();
        Set<String> seenNames = new HashSet<>();

        for (String type : types) {
            try {
                String url = String.format(Locale.US,
                        "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=8000&type=%s&key=%s",
                        lat, lng, type, googleApiKey
                );

                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                JSONObject json = new JSONObject(response.getBody());

                if (!"OK".equals(json.getString("status"))) continue;

                JSONArray jsonArray = json.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject placeJson = jsonArray.getJSONObject(i);

                    String name = placeJson.optString("name");
                    if (seenNames.contains(name)) continue;

                    seenNames.add(name);

                    Place place = new Place();
                    place.setName(name);
                    place.setAddress(placeJson.optString("vicinity", "Адрес не найден"));
                    place.setRating(placeJson.optDouble("rating", 0.0));

                    // Фото
                    if (placeJson.has("photos")) {
                        JSONArray photos = placeJson.getJSONArray("photos");
                        String photoRef = photos.getJSONObject(0).getString("photo_reference");
                        String photoUrl = String.format(
                                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=%s",
                                photoRef, googleApiKey
                        );
                        place.setPhotoUrl(photoUrl);
                    }

                    // Часы работы (если есть)
                    if (placeJson.has("opening_hours")) {
                        JSONObject hours = placeJson.getJSONObject("opening_hours");
                        boolean openNow = hours.optBoolean("open_now", false);
                        place.setOpeningHours(openNow ? "Открыто сейчас" : "Закрыто сейчас");
                    } else {
                        place.setOpeningHours("Нет информации");
                    }

                    // Тип (категория)
                    place.setType(type);

                    results.add(place);
                }
            } catch (Exception e) {
                System.out.println("Ошибка при поиске по типу " + type + ": " + e.getMessage());
            }
        }

        return results;
    }
}
