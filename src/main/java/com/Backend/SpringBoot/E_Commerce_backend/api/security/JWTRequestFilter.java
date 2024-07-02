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

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private JWTServices jwtServices;
    private LocalUserDAO localUserDAO;

    public JWTRequestFilter(JWTServices jwtServices, LocalUserDAO localUserDAO) {
        this.jwtServices = jwtServices;
        this.localUserDAO = localUserDAO;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer")) {
            String token = tokenHeader.substring(7);
            try {
                String username = jwtServices.getUsername(token);
                Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(username);
                if (opUser.isPresent()) {
                    LocalUser user = opUser.get();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JWTDecodeException ex) {

            }

        }
        filterChain.doFilter(request, response);

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
