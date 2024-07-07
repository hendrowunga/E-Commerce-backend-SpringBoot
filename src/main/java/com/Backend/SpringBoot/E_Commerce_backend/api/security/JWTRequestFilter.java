package com.Backend.SpringBoot.E_Commerce_backend.api.security;

import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.dao.LocalUserDAO;
import com.Backend.SpringBoot.E_Commerce_backend.services.JWTServices;
import com.auth0.jwt.exceptions.JWTDecodeException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/*
JWTRequestFilter adalah sebuah filter yang dijalankan pada setiap permintaan HTTP yang masuk,dan bertanggung jawab untuk memeriksa
apakah permintaan tersebut memiliki token JWT yang valid. Filter ini mengextends "OncePerRequestFilter",yang berarti bahwa filter ini hanya akan dijalankan
sekali per permintaan.
 */

@Component // Menandakan bahawa kelas ini merupakan komponen Spring yang dikelola oleh container Spring
public class JWTRequestFilter extends OncePerRequestFilter {

    private JWTServices jwtServices; // Mendeklarasikan dependensi JWTServices untuk Service JWT
    private LocalUserDAO localUserDAO; // Mendeklarsikan dependensi LocalUserDAO untuk akses data pengguna

    public JWTRequestFilter(JWTServices jwtServices, LocalUserDAO localUserDAO) { // Konstraktor untuk menginisialisasi dependensi JWTServices dan LocalUserDAO
        this.jwtServices = jwtServices;
        this.localUserDAO = localUserDAO;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization"); // Mengambil header "Authorization" dari request
        if (tokenHeader != null && tokenHeader.startsWith("Bearer")) { // Memeriksa apakah header berisi token "Bearer"
            String token = tokenHeader.substring(7); // Mengambil token dengan menghapus prefix "Bearer"
            try {
                String username = jwtServices.getUsername(token); // Mendapatkan username dari token menggunakan jwtServices
                Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(username); // Mencari pengguna berdasarkan username
                if (opUser.isPresent()) { // Jika pengguna ditemukan
                    LocalUser user = opUser.get(); // Mengambil objek pengguna
                    if (user.isEmailVerified()) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList()); // Membuat objek otentikasi menggunakan pengguna yang ditemukan
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Menambahkan detail request ke outentikasi
                        SecurityContextHolder.getContext().setAuthentication(authentication); // Menetapkan outentikasi ke konteks keamanan
                    }
                }
            } catch (JWTDecodeException ex) {
                // Menangani kesalahan decoding token,misalnya log kesalahan atau kirim respon kesalahan

            }

        }
        filterChain.doFilter(request, response); // Meneruskan request dan respon ke filter berikutnya dalam rantai filter

    }

}

/*
Metode doFilterInternal:
- tokenHeader mengambil header Authorization dari permintaan.
- Jika tokenHeader tidak null dan dimulai dengan "Bearer", token diambil dengan substring(7).
- jwtServices.getUsername(token) digunakan untuk mendapatkan username dari token.
- localUserDAO.findByUsernameIgnoreCase(username) digunakan untuk mencari pengguna berdasarkan username.
- Jika pengguna ditemukan, UsernamePasswordAuthenticationToken dibuat dan disetel dalam SecurityContextHolder.
 */

/*
Ilustrasi:
Permintaan HTTP:
Klien mengirimkan permintaan HTTP dengan header Authorization yang berisi token JWT.
Contoh header: Authorization: Bearer <JWT_TOKEN>

JWTRequestFilter Menangkap Permintaan:
JWTRequestFilter menangkap permintaan dan memeriksa header Authorization.
Jika header berisi token yang valid (dimulai dengan "Bearer"), token diproses lebih lanjut.

Proses Token JWT:
Token dipotong untuk menghilangkan prefix "Bearer".
jwtServices.getUsername(token) digunakan untuk mengekstrak username dari token.
localUserDAO.findByUsernameIgnoreCase(username) digunakan untuk mencari pengguna berdasarkan username yang diekstrak.

Mengatur Authentication:
Jika pengguna ditemukan di database, UsernamePasswordAuthenticationToken dibuat dengan informasi pengguna.
Authentication token disetel dalam SecurityContextHolder, sehingga Spring Security mengetahui bahwa pengguna telah terotentikasi.

Filter Chain:
Permintaan dilanjutkan ke filter berikutnya dalam rantai filter.
 */
