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
@Service // Menandakan bahwa kelas ini adalah kelas layanan (Service) Spring
public class UserServices {

    private LocalUserDAO localUserDAO;
    private VerificationTokenDAO verificationTokenDAO;
    private EncryptionServices encryptionServices;
    private JWTServices jwtServices;
    private EmailServices emailServices;

    // Konstruktor untuk inisialisasi dependensi melalui injeksi konstruktor
    public UserServices(LocalUserDAO localUserDAO, EncryptionServices encryptionServices, JWTServices jwtServices, EmailServices emailServices, VerificationTokenDAO verificationTokenDAO) {
        this.localUserDAO = localUserDAO;
        this.encryptionServices = encryptionServices;
        this.jwtServices = jwtServices;
        this.emailServices = emailServices;
        this.verificationTokenDAO = verificationTokenDAO;
    }

    @Transactional // Menandakan bahwa metode ini harus dieksekusi dalam satu transaksi database
    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {
        // Mengecek apakah email atau username sudah ada di database
        if (localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent() ||
                localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException(); // Jika sudah ada, lempar pengecualian
        }

        LocalUser user = new LocalUser(); // Membuat objek pengguna baru
        user.setEmail(registrationBody.getEmail()); // Mengatur email pengguna
        user.setUsername(registrationBody.getUsername()); // Mengatur username pengguna
        user.setFirstName(registrationBody.getFirstName()); // Mengatur nama depan pengguna
        user.setLastName(registrationBody.getLastName()); // Mengatur nama belakang pengguna
        user.setPassword(encryptionServices.encryptPassword(registrationBody.getPassword())); // Mengenkripsi dan mengatur password pengguna
        VerificationToken verificationToken = createVerificationToken(user); // Membuat token verifikasi untuk pengguna
        emailServices.sendVerificationEmail(verificationToken); // Mengirim email verifikasi kepada pengguna
        return localUserDAO.save(user); // Menyimpan pengguna ke database dan mengembalikan objek pengguna
    }

    // Metode untuk membuat token verifikasi untuk pengguna
    private VerificationToken createVerificationToken(LocalUser user) {
        VerificationToken verificationToken = new VerificationToken(); // Membuat objek token verifikasi baru
        verificationToken.setToken(jwtServices.generateVerificationJWT(user)); // Mengatur token dengan nilai JWT
        verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis())); // Mengatur timestamp saat token dibuat
        verificationToken.setUser(user); // Mengatur pengguna yang terkait dengan token ini
        user.getVerificationTokens().add(verificationToken); // Menambahkan token ke daftar token verifikasi pengguna
        return verificationToken; // Mengembalikan objek token verifikasi
    }

    // Metode untuk login pengguna
    public String loginUser(LoginBody loginBody) throws UserNotVerifiedException, EmailFailureException {
        Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(loginBody.getUsername()); // Mencari pengguna berdasarkan username
        if (opUser.isPresent()) { // Jika pengguna ditemukan
            LocalUser user = opUser.get(); // Mendapatkan objek pengguna
            if (encryptionServices.verifyPassword(loginBody.getPassword(), user.getPassword())) { // Memverifikasi password pengguna
                if (user.isEmailVerified()) { // Mengecek apakah email pengguna sudah diverifikasi
                    return jwtServices.generateJWT(user); // Menghasilkan token JWT dan mengembalikannya
                } else { // Jika email belum diverifikasi
                    List<VerificationToken> verificationTokens = user.getVerificationTokens(); // Mendapatkan daftar token verifikasi pengguna
                    boolean resend = verificationTokens.size() == 0 || // Mengecek apakah perlu mengirim ulang token verifikasi
                            verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000))); // Mengecek apakah token verifikasi sudah kadaluarsa (lebih dari 1 jam)
                    if (resend) { // Jika perlu mengirim ulang token verifikasi
                        VerificationToken verificationToken = createVerificationToken(user); // Membuat token verifikasi baru
                        verificationTokenDAO.save(verificationToken); // Menyimpan token verifikasi ke database
                        emailServices.sendVerificationEmail(verificationToken); // Mengirim email verifikasi baru kepada pengguna
                    }
                    throw new UserNotVerifiedException(resend); // Lempar pengecualian bahwa pengguna belum diverifikasi
                }
            }
        }
        return null; // Jika pengguna tidak ditemukan atau password salah, mengembalikan null
    }

    @Transactional // Menandakan bahwa metode ini harus dieksekusi dalam satu transaksi database
    public boolean verifyUser(String token) {
        Optional<VerificationToken> opToken = verificationTokenDAO.findByToken(token); // Mencari token verifikasi berdasarkan nilai token
        if (opToken.isPresent()) { // Jika token ditemukan
            VerificationToken verificationToken = opToken.get(); // Mendapatkan objek token verifikasi
            LocalUser user = verificationToken.getUser(); // Mendapatkan pengguna yang terkait dengan token ini
            if (!user.isEmailVerified()) { // Jika email pengguna belum diverifikasi
                user.setEmailVerified(true); // Mengatur email pengguna sebagai diverifikasi
                localUserDAO.save(user); // Menyimpan perubahan pengguna ke database
                verificationTokenDAO.deleteByUser(user); // Menghapus semua token verifikasi untuk pengguna ini dari database
                return true; // Mengembalikan nilai true menandakan verifikasi berhasil
            }
        }
        return false; // Jika token tidak ditemukan atau email sudah diverifikasi, mengembalikan nilai false
    }
}
/*
Ilustrasi
Bayangkan Anda memiliki aplikasi toko online. Layanan ini menangani pendaftaran pengguna, login, dan verifikasi email.

Ilustrasi registerUser-->
Pengecekan Email dan Username:
Saat pengguna baru mendaftar, aplikasi akan mengecek apakah email atau username sudah ada di database.
Jika sudah ada, pengguna tidak bisa mendaftar dan akan diberitahu bahwa akun sudah ada.

Membuat Pengguna Baru:
Jika email dan username belum ada, aplikasi akan membuat pengguna baru.
Password pengguna akan dienkripsi sebelum disimpan.

Membuat dan Mengirim Token Verifikasi:
Aplikasi akan membuat token verifikasi dan mengirim email verifikasi ke pengguna.

Ilustrasi loginUser-->
Mencari Pengguna Berdasarkan Username:
Saat pengguna mencoba login, aplikasi akan mencari pengguna berdasarkan username.
Jika pengguna ditemukan, aplikasi akan memverifikasi password.

Memverifikasi Email:
Jika password benar dan email sudah diverifikasi, aplikasi akan menghasilkan token JWT untuk sesi login.
Jika email belum diverifikasi, aplikasi akan mengecek apakah perlu mengirim ulang token verifikasi dan mengirim email verifikasi baru jika diperlukan.

Ilustrasi verifyUser-->
Mencari Token Verifikasi:
Saat pengguna mengklik link verifikasi di email, aplikasi akan mencari token verifikasi di database.
Jika token ditemukan, aplikasi akan memverifikasi email pengguna dan menghapus token verifikasi dari database.
 */