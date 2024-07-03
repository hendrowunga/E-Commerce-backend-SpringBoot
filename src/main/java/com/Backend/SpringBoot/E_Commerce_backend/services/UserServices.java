package com.Backend.SpringBoot.E_Commerce_backend.services;

import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginBody;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.RegistrationBody;
import com.Backend.SpringBoot.E_Commerce_backend.exception.EmailFailureException;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserAlreadyExistsException;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserNotVerifiedException;
import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.VerificationToken;
import com.Backend.SpringBoot.E_Commerce_backend.model.dao.LocalUserDAO;
import com.Backend.SpringBoot.E_Commerce_backend.model.dao.VerificationTokenDAO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
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
    private VerificationTokenDAO verificationTokenDAO;
    private EncryptionServices encryptionServices;
    private JWTServices jwtServices;
    private EmailServices emailServices;

    public UserServices(LocalUserDAO localUserDAO, EncryptionServices encryptionServices, JWTServices jwtServices, EmailServices emailServices, VerificationTokenDAO verificationTokenDAO) {
        this.localUserDAO = localUserDAO;
        this.encryptionServices = encryptionServices;
        this.jwtServices = jwtServices;
        this.emailServices = emailServices;
        this.verificationTokenDAO = verificationTokenDAO;
    }

    @Transactional
    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {

        if (localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent() ||
                localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        LocalUser user = new LocalUser();
        user.setEmail(registrationBody.getEmail());
        user.setUsername(registrationBody.getUsername());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());
        user.setPassword(encryptionServices.encryptPassword(registrationBody.getPassword()));
        VerificationToken verificationToken = createVerificationToken(user);
        emailServices.sendVerificationEmail(verificationToken);
        return localUserDAO.save(user);
    }

    private VerificationToken createVerificationToken(LocalUser user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwtServices.generateVerificationJWT(user));
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        verificationToken.setUser(user);
        user.getVerificationTokens().add(verificationToken);
        return verificationToken;
    }

    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername());
        if (opUser.isPresent()) {
            LocalUser user = opUser.get();
            if (encryptionServices.verifyPassword(loginBody.getPassword(), user.getPassword())) {
                if (user.isEmailVerified()) {
                    return jwtServices.generateJWT(user);
                } else {
                    List<VerificationToken> verificationTokens = user.getVerificationTokens();
                    boolean resend = verificationTokens.size() == 0 ||
                            verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
                    if (resend) {
                        VerificationToken verificationToken = createVerificationToken(user);
                        verificationTokenDAO.save(verificationToken);
                        emailServices.sendVerificationEmail(verificationToken);
                    }
                    throw new UserNotVerifiedException(resend);
                }
            }
        }
        return null;
    }

    @Transactional
    public boolean verifyUser(String token) {
        Optional<VerificationToken> opToken = verificationTokenDAO.findByToken(token);
        if (opToken.isPresent()) {
            VerificationToken verificationToken = opToken.get();
            LocalUser user = verificationToken.getUser();
            if (!user.isEmailVerified()) {
                user.setEmailVerified(true);
                localUserDAO.save(user);
                verificationTokenDAO.deleteByUser(user);
                return true;
            }
        }
        return false;
    }

}