package com.Backend.SpringBoot.E_Commerce_backend.api.controller.auth;

import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginBody;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginResponse;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.PasswordResetBody;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.RegistrationBody;
import com.Backend.SpringBoot.E_Commerce_backend.exception.EmailFailureException;
import com.Backend.SpringBoot.E_Commerce_backend.exception.EmailNotFoundException;
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


@RestController // Menandakan bahwa kelas ini adalah pengendali RESTful yang akan menangani permintaan HTTP
@RequestMapping("/auth") // Semua permintaan yang diawali dengan "/auth" akan diproses oleh pengendali ini
public class AuthenticationController {

    private UserServices userServices; // Mendefinisikan layanan pengguna yang akan digunakan untuk registrasi dan autentikasi

    public AuthenticationController(UserServices userServices) {
        this.userServices = userServices; // Menginisialisasi layanan pengguna melalui konstruktor
    }

    // Metode untuk registrasi pengguna baru
    @PostMapping("/register") // Menangani permintaan POST ke "/auth/register"
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody) { // @Valid memvalidasi body permintaan berdasarkan aturan di RegistrationBody
        try {
            userServices.registerUser(registrationBody); // Memanggil layanan untuk mendaftarkan pengguna baru
            return ResponseEntity.ok().build(); // Mengembalikan respon sukses dengan status OK (200)
        } catch (UserAlreadyExistsException ex) { // Menangani pengecualian jika pengguna sudah ada
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Mengembalikan respon konflik dengan status CONFLICT (409)
        } catch (EmailFailureException e) { // Menangani pengecualian jika terjadi kesalahan pengiriman email
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Mengembalikan respon kesalahan server dengan status INTERNAL_SERVER_ERROR (500)
        }
    }

    // Metode untuk autentikasi pengguna
    @PostMapping("/login") // Menangani permintaan POST ke "/auth/login"
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) { // @Valid memvalidasi body permintaan berdasarkan aturan di LoginBody
        String jwt = null;
        try {
            jwt = userServices.loginUser(loginBody); // Memanggil layanan untuk autentikasi pengguna dan mendapatkan token JWT
        } catch (UserNotVerifiedException ex) { // Menangani pengecualian jika pengguna belum diverifikasi
            LoginResponse response = new LoginResponse(); // Membuat objek respons login
            response.setSuccess(false); // Menandakan bahwa login gagal
            String reason = "USER_NOT_VERIFIED"; // Alasan kegagalan
            if (ex.isNewEmailSent()) {
                reason += "_EMAIL_RESENT"; // Menambahkan informasi bahwa email verifikasi baru telah dikirim
            }
            response.setFailureReason(reason); // Mengatur alasan kegagalan pada respons
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response); // Mengembalikan respon dengan status FORBIDDEN (403) dan body respons
        } catch (EmailFailureException ex) { // Menangani pengecualian jika terjadi kesalahan pengiriman email
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Mengembalikan respon kesalahan server dengan status INTERNAL_SERVER_ERROR (500)
        }
        if (jwt == null) { // Jika token JWT null
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Mengembalikan respon dengan status BAD_REQUEST (400)
        } else {
            LoginResponse response = new LoginResponse(); // Membuat objek respons login
            response.setJwt(jwt); // Mengatur token JWT pada respons
            response.setSuccess(true); // Menandakan bahwa login berhasil
            return ResponseEntity.ok(response); // Mengembalikan respon sukses dengan status OK (200) dan body respons
        }
    }

    @PostMapping("/verify") // Menangani permintaan POST ke "/auth/verify"
    public ResponseEntity verifyEmail(@RequestParam String token) { // Mengambil parameter token dari permintaan
        if (userServices.verifyUser(token)) { // Memanggil layanan untuk verifikasi pengguna berdasarkan token
            return ResponseEntity.ok().build(); // Mengembalikan respon sukses dengan status OK (200)
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Mengembalikan respon konflik dengan status CONFLICT (409) jika verifikasi gagal
        }
    }

    // Metode untuk mendapatkan profil pengguna yang sedang terautentikasi
    @GetMapping("/me") // Menangani permintaan GET ke "/auth/me"
    public LocalUser getLoggedUserProfile(@AuthenticationPrincipal LocalUser user) { // Mengambil pengguna yang sedang terautentikasi
        return user; // Mengembalikan profil pengguna yang terautentikasi saat ini
    }

    @PostMapping("/forgot")
    public ResponseEntity forgotPassword(@RequestParam String email) {
        try {
            userServices.forgotPassword(email);
            return ResponseEntity.ok().build();
        } catch (EmailNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/reset")
    public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body) {
        userServices.resetPassword(body);
        return ResponseEntity.ok().build();
    }

}
/*
Ilustrasi
Bayangkan Anda memiliki aplikasi toko online dengan fitur registrasi, login, dan verifikasi pengguna.

Registrasi Pengguna Baru:
Endpoint: /auth/register
Mengirim data registrasi dalam format JSON.
Jika berhasil, mengembalikan status 200 (OK).
Jika pengguna sudah ada, mengembalikan status 409 (Conflict).

Login Pengguna:
Endpoint: /auth/login
Mengirim data login dalam format JSON.
Jika berhasil, mengembalikan token JWT dan status 200 (OK).
Jika pengguna belum diverifikasi, mengembalikan status 403 (Forbidden) dengan alasan kegagalan.
Jika ada kesalahan lain, mengembalikan status 500 (Internal Server Error).

Verifikasi Email:
Endpoint: /auth/verify
Mengirim token verifikasi sebagai parameter.
Jika berhasil, mengembalikan status 200 (OK).
Jika gagal, mengembalikan status 409 (Conflict).

Mendapatkan Profil Pengguna yang Terautentikasi:
Endpoint: /auth/me
Mengembalikan data pengguna yang sedang login dalam format JSON.
 */