package com.qaida.backend.dto;

public class LoginResponse {
    private Long userId;
    private String city;
    private String token;

    public LoginResponse(Long userId, String city, String token) {
        this.userId = userId;
        this.city = city;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCity() {
        return city;
    }

    public String getToken() {
        return token;
    }

}
