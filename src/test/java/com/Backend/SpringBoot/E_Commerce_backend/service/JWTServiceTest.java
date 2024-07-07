package com.Backend.SpringBoot.E_Commerce_backend.service;

import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.dao.LocalUserDAO;
import com.Backend.SpringBoot.E_Commerce_backend.services.JWTServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // Menandakan bahwa ini adalah kelas pengujian untuk aplikasi Spring Boot
@AutoConfigureMockMvc
public class JWTServiceTest {
    @Autowired // Menyuntikkan layanan JWT ke dalam kelas ini
    private JWTServices jwtServices;

    @Autowired // Menyuntikkan DAO pengguna lokal ke dalam kelas ini
    private LocalUserDAO localUserDAO;

    @Test // Menandakan bahwa metode ini adalah metode pengujian
    public void testVerificationTokenNotUsableForLogin(){
        LocalUser user=localUserDAO.findByUsernameIgnoreCase("UserA").get(); // Mencari pengguna dengan username "UserA"
        String token=jwtServices.generateVerificationJWT(user); // Menghasilkan token verifikasi untuk pengguna ini
        Assertions.assertNull(jwtServices.getUsername(token),"Verification token should not contain username."); // Memastikan bahwa token verifikasi tidak mengandung username
    }

    @Test // Menandakan bahwa metode ini adalah metode pengujian
    public void testAuthTokenReturnsUsername(){
        LocalUser user=localUserDAO.findByUsernameIgnoreCase("UserA").get(); // Mencari pengguna dengan username "UserA"
        String token=jwtServices.generateJWT(user); // Menghasilkan token autentikasi untuk pengguna ini
        Assertions.assertEquals(user.getUsername(),jwtServices.getUsername(token),"Token for auth should contain user's username."); // Memastikan bahwa token autentikasi mengandung username pengguna
    }
}


/*
Penjelasan dan Ilustrasi
Deklarasi Kelas dan Anotasi:
@SpringBootTest menandakan bahwa ini adalah kelas pengujian untuk aplikasi Spring Boot.
JWTServiceTest adalah kelas pengujian untuk layanan JWT.
Ilustrasi:
Bayangkan Anda sedang menyiapkan pengujian untuk layanan yang menghasilkan dan memverifikasi token JWT. Anotasi @SpringBootTest membantu Anda melakukan ini dalam konteks aplikasi Spring Boot.

Menyuntikkan Layanan dan DAO:
@Autowired digunakan untuk menyuntikkan JWTServices dan LocalUserDAO ke dalam kelas ini.
Ilustrasi:
Bayangkan Anda memiliki layanan JWT dan DAO pengguna lokal yang ingin Anda uji. Anotasi @Autowired membantu Anda menyuntikkan layanan ini ke dalam kelas pengujian agar Anda bisa menggunakannya.

Metode Pengujian Token Verifikasi (testVerificationTokenNotUsableForLogin):
@Test menandakan bahwa metode ini adalah metode pengujian.
Mencari pengguna dengan username "UserA" dan menghasilkan token verifikasi untuk pengguna ini.
Assertions.assertNull digunakan untuk memastikan bahwa token verifikasi tidak mengandung username.

Ilustrasi:
Bayangkan Anda membuat token verifikasi untuk pengguna. Anda ingin memastikan bahwa token ini tidak bisa digunakan untuk login, jadi Anda memeriksa bahwa token tersebut tidak mengandung username.

Metode Pengujian Token Autentikasi (testAuthTokenReturnsUsername):
@Test menandakan bahwa metode ini adalah metode pengujian.
Mencari pengguna dengan username "UserA" dan menghasilkan token autentikasi untuk pengguna ini.
Assertions.assertEquals digunakan untuk memastikan bahwa token autentikasi mengandung username pengguna.

Ilustrasi:
Bayangkan Anda membuat token autentikasi untuk pengguna. Anda ingin memastikan bahwa token ini mengandung username pengguna agar bisa digunakan untuk autentikasi di aplikasi.

Ilustrasi Detail

Token Verifikasi:
- Anda memiliki pengguna "UserA".
- Anda menghasilkan token verifikasi untuk "UserA".
- Anda memeriksa bahwa token ini tidak mengandung username karena hanya digunakan untuk verifikasi, bukan untuk login.

Token Autentikasi:
- Anda memiliki pengguna "UserA".
- Anda menghasilkan token autentikasi untuk "UserA".
- Anda memeriksa bahwa token ini mengandung username "UserA" sehingga dapat digunakan untuk autentikasi di aplikasi.
 */