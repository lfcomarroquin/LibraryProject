package com.api.libraryproject.dto.auth;

public class AuthDto {

    private String token;

    public AuthDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
