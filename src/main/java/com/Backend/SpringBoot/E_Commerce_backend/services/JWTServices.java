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
*/@Service // Menandakan bahwa kelas ini adalah service Spring, komponen yang menyediakan logika bisnis
public class JWTServices {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey; // Mengambil nilai kunci algoritma dari file konfigurasi
    @Value("${jwt.issuer}")
    private String issuer; // Mengambil nilai issuer dari file konfigurasi
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds; // Mengambil nilai expiryInSeconds dari file konfigurasi
    private Algorithm algorithm; // Menyimpan algoritma yang digunakan untuk tanda tangan JWT
    private static final String USERNAME_KEY = "USERNAME"; // Kunci untuk menyimpan klaim username dalam JWT
    private static final String EMAIL_KEY="EMAIL"; // Kunci untuk menyimpan klaim email dalam JWT

    @PostConstruct // Menandakan bahwa metode ini dijalankan setelah konstruksi bean
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey); // Menginisialisasi algoritma HMAC256 dengan kunci algoritma
    }

    // Menghasilkan JWT untuk pengguna
    public String generateJWT(LocalUser user) {
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername()) // Menambahkan klaim username ke JWT
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds))) // Menetapkan waktu kedaluwarsa JWT
                .withIssuer(issuer) // Menetapkan issuer JWT
                .sign(algorithm); // Menandatangani JWT dengan algoritma
    }

    // Menghasilkan JWT untuk verifikasi email pengguna
    public String generateVerificationJWT(LocalUser user){
        return JWT.create()
                .withClaim(EMAIL_KEY, user.getEmail()) // Menambahkan klaim email ke JWT
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds))) // Menetapkan waktu kedaluwarsa JWT
                .withIssuer(issuer) // Menetapkan issuer JWT
                .sign(algorithm); // Menandatangani JWT dengan algoritma
    }

    // Mengambil username dari JWT
    public String getUsername(String token){
        return JWT.decode(token).getClaim(USERNAME_KEY).asString(); // Mendekode JWT dan mendapatkan klaim username sebagai string
    }
}

/*
Ilustrasi Sederhana
Bayangkan Anda memiliki aplikasi toko online dan Anda ingin memastikan bahwa pengguna yang masuk adalah pengguna yang sah. Anda menggunakan JWT untuk ini.

Pengguna Masuk:

Ketika pengguna masuk, aplikasi Anda akan menghasilkan JWT yang berisi username pengguna dan waktu kedaluwarsa. JWT ini ditandatangani dengan kunci rahasia.
Misalnya: token: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVU0VSTkFNRSI6ImpvaG5kb2UifQ.sYSkG1yTYuMbr4OFS0vbA"
Penggunaan JWT:

Setiap kali pengguna melakukan permintaan ke server, mereka akan mengirimkan JWT ini. Server kemudian akan memverifikasi token ini dengan kunci yang sama untuk memastikan bahwa token tersebut sah.
Jika sah, server dapat mengambil informasi username dari token tersebut.
Verifikasi Email:

Ketika pengguna mendaftar, aplikasi akan menghasilkan JWT yang berisi email pengguna untuk verifikasi. Pengguna akan menerima email dengan token ini.
Misalnya: verificationToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJFbWFpbCI6ImpvaG5AZXhhbXBsZS5jb20ifQ.sYSkG1yTYuMbr4OFS0vbA"
 */