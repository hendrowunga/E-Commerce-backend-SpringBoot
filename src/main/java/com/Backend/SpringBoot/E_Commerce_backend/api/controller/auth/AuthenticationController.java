package com.Backend.SpringBoot.E_Commerce_backend.api.controller.auth;

import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginBody;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginResponse;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.RegistrationBody;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserAlreadyExistsException;
import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/*
Anotasi @RestController:
Menandakan bahwa kelas ini adalah RESTful controller yang akan menangani permintaan HTTP dan mengembalikan data berbasis objek langsung sebagai respons.

Anotasi @RequestMapping("/auth"):
Menetapkan rute dasar URL /auth untuk controller ini. Semua permintaan HTTP yang dikirim ke /auth akan ditangani oleh metode-metode dalam kelas ini.

Constructor AuthenticationController(UserServices userServices):
Constructor ini menyuntikkan dependensi UserServices ke dalam controller ini, memungkinkan metode dalam kelas ini untuk memanggil layanan pengguna.

Metode registerUser dengan Anotasi @PostMapping("/register"):
Menandakan bahwa metode ini akan menangani permintaan HTTP POST yang dikirim ke URL /auth/register. Endpoint ini digunakan untuk mendaftarkan pengguna baru.

Parameter @RequestBody RegistrationBody registrationBody:
Mengikat permintaan HTTP yang masuk ke objek registrationBody. Data dari permintaan POST akan di-deserialize menjadi objek RegistrationBody.

Metode registerUser:
Metode ini memanggil userServices.registerUser untuk mendaftarkan pengguna baru. Jika pengguna sudah ada, metode ini menangkap UserAlreadyExistsException dan mengembalikan respons dengan status HTTP 409 CONFLICT.

Metode loginUser dengan Anotasi @PostMapping("/login"):
Menandakan bahwa metode ini akan menangani permintaan HTTP POST yang dikirim ke URL /auth/login. Endpoint ini digunakan untuk autentikasi pengguna.

Parameter @RequestBody LoginBody loginBody:
Mengikat permintaan HTTP yang masuk ke objek loginBody. Data dari permintaan POST akan di-deserialize menjadi objek LoginBody.

Metode loginUser:
Metode ini memanggil userServices.loginUser untuk memverifikasi kredensial pengguna. Jika autentikasi berhasil, metode ini mengembalikan JWT dalam respons. Jika gagal, metode ini mengembalikan status HTTP 400 BAD REQUEST.*/

/*
ILUSTRASI:

Registrasi Pengguna:
Permintaan: Pengguna mengirimkan permintaan POST ke /auth/register dengan JSON payload yang berisi informasi pendaftaran (username, email, password, dll.).
Proses:
AuthenticationController memanggil userServices.registerUser.
userServices memeriksa apakah pengguna sudah ada.
Jika pengguna baru, userServices menyimpan pengguna ke database.
Respons:
Jika sukses, mengembalikan HTTP 200 OK.
Jika pengguna sudah ada, mengembalikan HTTP 409 CONFLICT.

Login Pengguna:
Permintaan: Pengguna mengirimkan permintaan POST ke /auth/login dengan JSON payload yang berisi informasi login (username dan password).
Proses:
AuthenticationController memanggil userServices.loginUser.
userServices memverifikasi kredensial pengguna.
Jika berhasil, userServices menghasilkan JWT untuk pengguna.
Respons:
Jika sukses, mengembalikan HTTP 200 OK dengan JWT dalam respons.
Jika gagal, mengembalikan HTTP 400 BAD REQUEST.
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private UserServices userServices;

    public AuthenticationController(UserServices userServices) {
        this.userServices = userServices;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
        try {
            userServices.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = userServices.loginUser(loginBody);
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/me")
    public LocalUser getLoggedUserProfile(@AuthenticationPrincipal LocalUser user) {
        return user;
    }
}
