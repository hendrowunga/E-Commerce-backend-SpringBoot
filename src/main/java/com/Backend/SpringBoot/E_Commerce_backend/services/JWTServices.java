package com.Backend.SpringBoot.E_Commerce_backend.services;

import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Date;

/*
Anotasi @Service : Anotasi @Service menandakan bahwa kelas JWTServices adalah sebuah layanan (service) dalam aplikasi Spring. Layanan ini bertanggung jawab untuk menghasilkan dan mengelola token JSON Web Token (JWT) untuk otentikasi dan otorisasi.
Injeksi Nilai (@Value) : Anotasi @Value digunakan untuk menyuntikkan nilai properti dari file konfigurasi aplikasi Spring. Dalam contoh ini, algorithmKey, issuer, dan expiryInSeconds diambil dari konfigurasi aplikasi menggunakan placeholder ${...}.

Metode postConstruct():
Metode ini dianotasi dengan @PostConstruct, yang menandakan bahwa metode ini akan dijalankan setelah semua injeksi dependensi selesai dilakukan. Pada tahap ini, objek Algorithm untuk JWT diinisialisasi menggunakan kunci algoritma (algorithmKey) yang telah dimuat dari konfigurasi.

Metode generateJWT(LocalUser user):
Metode ini digunakan untuk menghasilkan token JWT berdasarkan informasi pengguna (LocalUser) yang diberikan.
JWT.create() memulai proses pembuatan JWT.
.withClaim(USERNAME_KEY, user.getUsername()) menambahkan klaim (claim) ke token JWT dengan nama "USERNAME" dan nilai username dari LocalUser.
.withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds))) menentukan waktu kedaluwarsa token. Dalam contoh ini, token akan kedaluwarsa dalam waktu yang ditentukan dalam satuan detik (expiryInSeconds).
.withIssuer(issuer) menentukan penerbit token (issuer).
.sign(algorithm) menghasilkan token JWT yang ditandatangani menggunakan algoritma yang telah disiapkan.
JWTServices bertanggung jawab untuk mengelola pembuatan token JWT dengan menggunakan kunci dan konfigurasi yang ditentukan dalam file konfigurasi aplikasi Spring. Metode generateJWT() digunakan oleh layanan lain, seperti UserServices, untuk menghasilkan token JWT setelah pengguna berhasil login,
sehingga token dapat digunakan untuk otentikasi pengguna pada setiap permintaan yang memerlukan otorisasi.
*/
@Service
public class JWTServices {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;
    private Algorithm algorithm;
    private static final String USERNAME_KEY = "USERNAME";
    private static final String EMAIL_KEY="EMAIL";

    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }


    public String generateJWT(LocalUser user) {
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generateVerificationJWT(LocalUser user){
        return JWT.create()
                .withClaim(EMAIL_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }
    public String getUsername(String token){
     return JWT.decode(token).getClaim(USERNAME_KEY).asString();
    }

}