package com.Backend.SpringBoot.E_Commerce_backend.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/*
@Configuration : Anotasi ini menandakan bahwa kelas WebSecurityConfig adalah sebuah konfigurasi untuk aplikasi Spring.
Metode securityFilterChain : Metode ini menggunakan anotasi"@Bean" yang menghasilkan object SecurityFilterChain,yang menentukan bagaimana permintaan HTTP akan diatur dari sisi keamanan.
Konfigurasi HttpSecurity   : Objek HttpSecurity digunakan untuk mengonfigurasi aturan keamanan pada aplikasi. Dalam contoh ini:
.csrf(csrf -> csrf.disable()): Menonaktifkan proteksi CSRF. CSRF (Cross-Site Request Forgery) adalah serangan yang mengeksploitasi sesi yang sudah ada antara pengguna dan aplikasi untuk menjalankan aksi tanpa pengetahuan pengguna.
.cors(cors -> cors.disable()): Menonaktifkan konfigurasi CORS. CORS (Cross-Origin Resource Sharing) mengatur akses lintas domain untuk sumber daya web.
.authorizeHttpRequests(auth -> auth.anyRequest().permitAll()): Mengizinkan semua permintaan (.anyRequest()) untuk diakses tanpa otentikasi atau otorisasi tambahan (.permitAll()). Ini berarti semua endpoint pada aplikasi akan tersedia untuk diakses oleh siapa saja tanpa memerlukan autentikasi.
*/
@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}

