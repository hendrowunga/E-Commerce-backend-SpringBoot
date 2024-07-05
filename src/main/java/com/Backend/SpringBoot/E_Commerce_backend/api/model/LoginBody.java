package com.Backend.SpringBoot.E_Commerce_backend.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginBody {
    @NotNull // Field ini tidak boleh null
    @NotBlank // Field ini tidak boleh kosong atau berisikan spasi
    private String username;
    @NotNull
    @NotBlank
    private String password;

    // Metode Getter and setter


    public @NotNull @NotBlank String getUsername() {
        return username;
    }

    public void setUsername(@NotNull @NotBlank String username) {
        this.username = username;
    }

    public @NotNull @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @NotBlank String password) {
        this.password = password;
    }
}
