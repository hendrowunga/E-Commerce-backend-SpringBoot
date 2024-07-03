package com.Backend.SpringBoot.E_Commerce_backend.api.controller.auth;

import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginBody;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginResponse;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.RegistrationBody;
import com.Backend.SpringBoot.E_Commerce_backend.exception.EmailFailureException;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserAlreadyExistsException;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserNotVerifiedException;
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
AuthenticationController adalah kelas yang menangani permintaan terkait otentikasi pengguna seperti registrasi dan login.
@RestController menandakan bahwa kelas ini adalah RESTful controller yang menangani permintaan HTTP dan mengembalikan data sebagai respons berbasis objek langsung.
@RequestMapping("/auth") menetapkan rute dasar URL /auth untuk controller ini. Semua permintaan HTTP yang dikirim ke /auth akan ditangani oleh metode-metode dalam kelas ini.
Konstruktor AuthenticationController menginisialisasi controller dengan dependensi UserServices, yang digunakan untuk melakukan logika bisnis terkait pengguna.
Metode registerUser memproses permintaan POST untuk mendaftarkan pengguna baru dengan memanggil userServices.registerUser(registrationBody). Jika pengguna sudah ada, metode ini menangkap UserAlreadyExistsException dan mengembalikan respons dengan status HTTP 409 CONFLICT.
Metode loginUser memproses permintaan POST untuk autentikasi pengguna dengan memanggil userServices.loginUser(loginBody). Jika autentikasi berhasil, metode ini mengembalikan JWT dalam respons dengan status HTTP 200 OK. Jika gagal, metode ini mengembalikan status HTTP 400 BAD REQUEST.
Metode getLoggedUserProfile menggunakan anotasi @GetMapping("/me") untuk mengembalikan profil pengguna yang sedang terautentikasi saat ini. Ini memanfaatkan anotasi @AuthenticationPrincipal untuk mengambil pengguna dari konteks otentikasi Spring Security.
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private UserServices userServices;

    public AuthenticationController(UserServices userServices) {
        this.userServices = userServices;
    }

    // Metode untuk registrasi pengguna baru
    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) {
        try {
            userServices.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Metode untuk autentikasi pengguna
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
        String jwt = null;
        try {
            jwt = userServices.loginUser(loginBody);
        } catch (UserNotVerifiedException ex) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            String reason = "USER_NOT_VERIFIED";
            if (ex.isNewEmailSent()) {
                reason += "_EMAIL_RESENT";
            }
            response.setFailureReason(reason);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (EmailFailureException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (jwt == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            LoginResponse response = new LoginResponse();
            response.setJwt(jwt);
            response.setSuccess(true);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity verifyEmail(@RequestParam String token) {
        if (userServices.verifyUser(token)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // Metode untuk mendapatkan profil pengguna yang sedang terautentikasi
    @GetMapping("/me")
    public LocalUser getLoggedUserProfile(@AuthenticationPrincipal LocalUser user) {
        return user; // Mengembalikan profil pengguna yang terautentikasi saat ini
    }
}
