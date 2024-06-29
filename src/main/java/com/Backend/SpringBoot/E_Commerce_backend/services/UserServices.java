package com.Backend.SpringBoot.E_Commerce_backend.services;

import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginBody;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.RegistrationBody;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserAlreadyExistsException;
import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.dao.LocalUserDAO;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*
@Service: Anotasi ini menunjukkan bahwa kelas UserServices adalah sebuah service dalam konteks Spring. Service digunakan untuk menampung logika bisnis aplikasi dan
sering digunakan untuk mengelola operasi yang terkait dengan database atau proses bisnis.

Konstruktor : Konstruktor UserServices menerima tiga parameter: LocalUserDAO, EncryptionServices, dan JWTServices.
LocalUserDAO digunakan untuk berinteraksi dengan entitas LocalUser di database.
EncryptionServices digunakan untuk enkripsi dan verifikasi kata sandi pengguna.
JWTServices digunakan untuk menghasilkan token JWT setelah pengguna berhasil login.

Metode registerUser : Metode ini digunakan untuk mendaftarkan pengguna baru berdasarkan data yang diterima dari objek RegistrationBody.
Pertama, metode ini memeriksa apakah pengguna dengan email atau username yang sama sudah ada di dalam database menggunakan LocalUserDAO.
Jika pengguna sudah ada, maka UserAlreadyExistsException dilemparkan.
Jika tidak ada, objek LocalUser dibuat dan diisi dengan informasi dari registrationBody, termasuk enkripsi kata sandi sebelum disimpan ke database.
Akhirnya, objek LocalUser yang baru dibuat disimpan ke database menggunakan localUserDAO.save(user).

Metode loginUser : Metode ini mengelola proses login pengguna.
Pertama, mencari pengguna berdasarkan username yang diberikan menggunakan LocalUserDAO.
Jika pengguna ditemukan, metode akan memverifikasi kata sandi yang dimasukkan dengan kata sandi yang tersimpan dalam database menggunakan encryptionServices.
Jika verifikasi berhasil, metode akan menghasilkan token JWT menggunakan jwtServices.generateJWT(user) dan mengembalikan token JWT tersebut.
Jika tidak ada pengguna yang ditemukan atau verifikasi gagal, metode mengembalikan null.
*/
@Service
public class UserServices {

    private LocalUserDAO localUserDAO;
    private EncryptionServices encryptionServices;
    private JWTServices jwtServices;

    public UserServices(LocalUserDAO localUserDAO, EncryptionServices encryptionServices, JWTServices jwtServices) {
        this.localUserDAO = localUserDAO;
        this.encryptionServices = encryptionServices;
        this.jwtServices = jwtServices;
    }


    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException {

        if (localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent() ||
                localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        LocalUser user = new LocalUser();
        user.setUsername(registrationBody.getUsername());
        user.setEmail(registrationBody.getEmail());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setPassword(encryptionServices.encryptPassword(registrationBody.getPassword()));
        return localUserDAO.save(user);
    }

    public String loginUser(LoginBody loginBody) {
        Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());
        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            if (encryptionServices.verifyPassword(loginBody.getPassword(), user.getPassword())) {
                return jwtServices.generateJWT(user);
            }
        }
        return null;
    }

}
