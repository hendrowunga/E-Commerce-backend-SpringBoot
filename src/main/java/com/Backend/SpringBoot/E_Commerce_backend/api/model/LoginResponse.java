package com.Backend.SpringBoot.E_Commerce_backend.api.model;

public class LoginResponse {
    private String jwt;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
