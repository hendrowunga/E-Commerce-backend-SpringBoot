package com.Backend.SpringBoot.E_Commerce_backend.api.model;

import jakarta.validation.constraints.*;
/*
 @NotNull:Memastikan password tidak null.
 @NotBlank:Memastikan password bukan string kosong atau hanya berisi spasi.
 @Size(min = 6,max = 32) :Memastikan password memiliki panjang minimal 6 karakter dan maksimal 32 karakter.
 @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$"): Memastikan password mengandung setidaknya satu huruf dan satu angka, serta terdiri dari huruf dan angka saja dengan panjang minimal 6 karakter.
 */
public class RegistrationBody {

    @NotNull
    @NotBlank
    @Size(min = 3,max = 255)
    private String username;

    @NotNull
    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 6,max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
    private String password;

    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    private String lastName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}