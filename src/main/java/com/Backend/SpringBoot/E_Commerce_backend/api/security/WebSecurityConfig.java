package com.Backend.SpringBoot.E_Commerce_backend.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

/*
@Configuration: menandakan bahwa kelas WebSecurityConfig adalah kelas konfigurasi untuk aplikasi Spring.
WebSecurityConfig:menggunakan @Bean untuk menyediakan objek SecurityFilterChain, yang mendefinisikan bagaimana permintaan HTTP akan diatur dari sisi keamanan.
Konstruktor WebSecurityConfig: menerima JWTRequestFilter sebagai dependensi, yang digunakan untuk melakukan filter permintaan berdasarkan token JWT.
Metode securityFilterChain: menggunakan objek HttpSecurity untuk mengonfigurasi aturan keamanan:
    .addFilterBefore(jwtRequestFilter, AuthorizationFilter.class): Menambahkan JWTRequestFilter sebelum filter otorisasi bawaan Spring Security.
    .csrf(csrf -> csrf.disable()): Menonaktifkan proteksi CSRF untuk mencegah serangan Cross-Site Request Forgery.
    .cors(cors -> cors.disable()): Menonaktifkan konfigurasi CORS untuk mengontrol akses lintas domain.
    .authorizeHttpRequests(auth -> ...): Mengatur izin akses untuk permintaan HTTP:
    .requestMatchers("/product", "/auth/register", "/auth/login").permitAll(): Mengizinkan akses tanpa autentikasi ke endpoint /product, /auth/register, dan /auth/login.
    .anyRequest().authenticated(): Membutuhkan autentikasi untuk semua endpoint lainnya yang tidak terdaftar sebelumnya.
*/
@Configuration
public class WebSecurityConfig {
    private JWTRequestFilter jwtRequestFilter;

    public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    // Metode untuk mengonfigurasi keamanan HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(jwtRequestFilter, AuthorizationFilter.class) // Menambahkan JWTRequestFilter sebelum AuthorizationFilter
                .csrf(csrf -> csrf.disable()) // Menonaktifkan proteksi CSRF
                .cors(cors -> cors.disable()) // Menonaktifkan konfigurasi CORS
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/product", "/auth/register", "/auth/login","/auth/verify","/error").permitAll() // Mengizinkan akses tanpa autentikasi ke endpoint tertentu
                                .anyRequest().authenticated()); // Membutuhkan autentikasi untuk semua endpoint lainnya

        return http.build(); // Mengembalikan konfigurasi keamanan yang telah dikonfigurasi
    }
}
