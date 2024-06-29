package com.Backend.SpringBoot.E_Commerce_backend.api.controller.auth;

import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginBody;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginResponse;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.RegistrationBody;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserAlreadyExistsException;
import com.Backend.SpringBoot.E_Commerce_backend.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
@RestController : Anotasi ini menandakan bahwa kelas ini adalah sebuah controller yang berfungsi sebagai RESTful controller. Ini berarti kelas ini akan menangani permintaan permintaan HTTP dan mengembalikan  data berbasis objek langsung sebagai response,bukan view HTML.
@RequestMapping("/auth") : Anotasi ini digunakan untuk menteapkan rute dasarr URL'/auth' kedalam controller ini. Artinya,semua permintaan HTTP yang dikirim ke '/auth' akan ditangani oleh metode-metode yang didefinisikan dalam kelas ini.
@PostMapping("/register"): Anotasi ini menandakan bahwa metode registerUser akan menangani permintaan HTTP POST yang dikirimkan ke URL /auth/register. Dengan kata lain, ini adalah endpoint yang akan dipanggil saat ada permintaan untuk mendaftarkan pengguna baru.
@RequestBody: Anotasi ini digunakan untuk mengikat permintaan HTTP yang masuk ke objek registrationBody. Data dari permintaan POST akan di-deserialize menjadi objek RegistrationBody. Biasanya, RegistrationBody akan berisi informasi yang diperlukan untuk mendaftarkan pengguna baru seperti nama pengguna, kata sandi, alamat email, dll.
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

}
