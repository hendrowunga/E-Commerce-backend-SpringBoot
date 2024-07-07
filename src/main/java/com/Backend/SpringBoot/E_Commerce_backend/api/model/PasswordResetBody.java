package com.Backend.SpringBoot.E_Commerce_backend.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordResetBody {

    @NotNull
    @NotBlank
    private String token;
    @NotNull
    @NotBlank
    @Size(min = 6,max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
    private String password;


    public @NotNull @NotBlank String getToken() {
        return token;
    }

    public void setToken(@NotNull @NotBlank String token) {
        this.token = token;
    }

    public @NotNull @NotBlank @Size(min = 6, max = 32) @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$") String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @NotBlank @Size(min = 6, max = 32) @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$") String password) {
        this.password = password;
    }


}
