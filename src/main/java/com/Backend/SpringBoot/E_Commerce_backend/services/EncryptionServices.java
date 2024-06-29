package com.Backend.SpringBoot.E_Commerce_backend.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/*
Anotasi @Service: Anotasi @Service menandakan bahwa kelas EncryptionServices adalah sebuah layanan (service) dalam aplikasi Spring.
Layanan ini bertanggung jawab untuk mengenkripsi dan memverifikasi kata sandi pengguna menggunakan algoritma BCrypt.
Injeksi Nilai (@Value) : Anotasi @Value digunakan untuk menyuntikkan nilai properti dari file konfigurasi aplikasi Spring.
Dalam contoh ini, saltRounds diambil dari konfigurasi aplikasi menggunakan placeholder ${...}. Nilai ini menentukan jumlah putaran (rounds) yang digunakan untuk menghasilkan salt pada BCrypt.

Metode postConstruct():
Metode ini dianotasi dengan @PostConstruct, yang menandakan bahwa metode ini akan dijalankan setelah semua injeksi dependensi selesai dilakukan.
Pada tahap ini, salt untuk BCrypt dihasilkan menggunakan BCrypt.gensalt(saltRounds). Salt ini akan digunakan bersama dengan kata sandi pengguna untuk menghasilkan hash yang aman.

Metode encryptPassword(String password):
Metode ini menggunakan BCrypt untuk mengenkripsi kata sandi pengguna. BCrypt.hashpw(password, salt) mengambil kata sandi yang belum dienkripsi dan salt yang telah dihasilkan,
kemudian mengembalikan hash yang dihasilkan.

Metode verifyPassword(String password, String hash):
Metode ini digunakan untuk memverifikasi apakah kata sandi yang diberikan cocok dengan hash yang disimpan.
BCrypt.checkpw(password, hash) membandingkan kata sandi yang belum dienkripsi dengan hash yang telah disimpan, dan mengembalikan true jika cocok, dan false jika tidak cocok.
 */
@Service
public class EncryptionServices {


    @Value("${encryption.salt.rounds}")
    private int saltRounds;
    private String salt;


    @PostConstruct
    public void postConstruct() {
        salt = BCrypt.gensalt(saltRounds);
    }


    public String encryptPassword(String password) {
        return BCrypt.hashpw(password, salt);
    }


    public boolean verifyPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

}
