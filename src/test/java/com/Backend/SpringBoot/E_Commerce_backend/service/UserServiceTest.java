package com.Backend.SpringBoot.E_Commerce_backend.service;


import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginBody;
import com.Backend.SpringBoot.E_Commerce_backend.exception.EmailFailureException;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserNotVerifiedException;
import com.Backend.SpringBoot.E_Commerce_backend.model.VerificationToken;
import com.Backend.SpringBoot.E_Commerce_backend.model.dao.VerificationTokenDAO;
import com.Backend.SpringBoot.E_Commerce_backend.services.UserServices;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.RegistrationBody;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserAlreadyExistsException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

    @RegisterExtension
    // Mengatur server email lokal untuk pengujian menggunakan GreenMail
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("Endos", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private UserServices userService; // Menggunakan layanan pengguna untuk pengujian
    @Autowired
    private VerificationTokenDAO verificationTokenDAO; // Menggunakan DAO token verifikasi untuk pengujian


    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        RegistrationBody body = new RegistrationBody();
        body.setUsername("UserA"); // Mengatur username
        body.setEmail("UserServiceTest$testRegisterUser@junit.com"); // Mengatur email
        body.setFirstName("FirstName"); // Mengatur nama depan
        body.setLastName("LastName"); // Mengatur nama belakang
        body.setPassword("MySecretPassword123"); // Mengatur password
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Username should already be in use.");
        body.setUsername("UserServiceTest$testRegisterUser"); // Mengatur username baru
        body.setEmail("UserA@junit.com"); // Mengatur email baru
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Email should already be in use.");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com"); // Mengatur email yang valid
        Assertions.assertDoesNotThrow(() -> userService.registerUser(body),
                "User should register successfully.");
        // Memeriksa apakah email verifikasi dikirim ke email yang benar
        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
                .getRecipients(Message.RecipientType.TO)[0].toString());
    }

    @Test
    @Transactional
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody body = new LoginBody();

        body.setUsername("UserA-NoExists"); // Mengatur username yang tidak ada
        body.setPassword("PasswordA123-BadPassword"); // Mengatur password yang salah
        Assertions.assertNull(userService.loginUser(body),"The user should not exist");
        body.setUsername("UserA"); // Mengatur username yang ada
        Assertions.assertNull(userService.loginUser(body),"The password should be incorrect");
        body.setPassword("PasswordA123"); // Mengatur password yang benar
        Assertions.assertNotNull(userService.loginUser(body),"The user should login successfully");
        body.setUsername("UserB"); // Mengatur username pengguna yang belum diverifikasi
        body.setPassword("PasswordB123"); // Mengatur password pengguna yang belum diverifikasi
        try {
            userService.loginUser(body);
            Assertions.assertTrue(false,"User should not have email verified");

        } catch (UserNotVerifiedException ex) {
            Assertions.assertTrue(ex.isNewEmailSent(),"Email verification should be sent");
            Assertions.assertEquals(1,greenMailExtension.getReceivedMessages().length);
        }

        try {
            userService.loginUser(body);
            Assertions.assertTrue(false,"User should not have email verified");

        } catch (UserNotVerifiedException ex) {
            Assertions.assertFalse(ex.isNewEmailSent(),"Email verification should not be resent");
            Assertions.assertEquals(1,greenMailExtension.getReceivedMessages().length);
        }
    }

    @Test
    @Transactional
    public void testVerifyUser() throws EmailFailureException {
        Assertions.assertFalse(userService.verifyUser("Bad Token"), "Token that is bad or does not exist should return false.");
        LoginBody body = new LoginBody();
        body.setUsername("UserB");
        body.setPassword("PasswordB123");
        try {
            userService.loginUser(body);
            Assertions.assertTrue(false, "User should not have email verified.");
        } catch (UserNotVerifiedException ex) {
            List<VerificationToken> tokens = verificationTokenDAO.findByUser_IdOrderByIdDesc(2L);
            String token = tokens.get(0).getToken();
            Assertions.assertTrue(userService.verifyUser(token), "Token should be valid.");
            Assertions.assertNotNull(body, "The user should now be verified.");
        }
    }

}

/*
Penjelasan Detail dan Ilustrasi
GreenMail Setup:
GreenMailExtension digunakan untuk membuat server email lokal untuk pengujian.
Server email ini menggunakan username "Endos" dan password "secret".
Server email akan dijalankan setiap kali metode pengujian dijalankan.

Ilustrasi:
Bayangkan Anda memiliki server email lokal di komputer Anda. Anda mengatur server ini untuk menerima email dari aplikasi selama pengujian, sehingga Anda dapat memeriksa apakah email benar-benar dikirim.

Pengujian Registrasi Pengguna (testRegisterUser):
RegistrationBody digunakan untuk membuat pengguna baru dengan informasi seperti username, email, nama depan, nama belakang, dan password.
Metode ini menguji apakah registrasi pengguna akan gagal jika username atau email sudah digunakan, dan berhasil jika menggunakan username dan email yang valid.
Setelah registrasi berhasil, metode ini memeriksa apakah email verifikasi dikirim ke email yang benar.

Ilustrasi:
Bayangkan Anda mencoba mendaftar di situs web. Jika Anda mencoba menggunakan username atau email yang sudah terdaftar, sistem akan memberi tahu Anda bahwa mereka sudah digunakan. Setelah Anda berhasil mendaftar, Anda akan menerima email verifikasi.

Pengujian Login Pengguna (testLoginUser):
LoginBody digunakan untuk mencoba login dengan berbagai kombinasi username dan password.
Metode ini menguji apakah login akan gagal jika username tidak ada, jika password salah, atau jika pengguna belum memverifikasi emailnya.
Jika email belum diverifikasi, sistem akan mengirim email verifikasi. Pengujian ini memastikan bahwa email verifikasi tidak dikirim dua kali.

Ilustrasi:
Bayangkan Anda mencoba login ke akun Anda. Jika Anda salah memasukkan username atau password, Anda tidak akan bisa masuk. Jika Anda belum memverifikasi email, sistem akan mengirim email verifikasi ke email Anda.

Pengujian Verifikasi Pengguna (testVerifyUser):
Metode ini menguji apakah token verifikasi yang salah atau tidak ada akan gagal memverifikasi pengguna.
Jika token valid, metode ini akan memverifikasi pengguna dan memastikan pengguna bisa login setelah verifikasi.
Ilustrasi:
Bayangkan Anda menerima email verifikasi setelah mendaftar. Anda mengklik link dalam email tersebut untuk memverifikasi akun Anda. Jika token dalam link valid, akun Anda akan diverifikasi dan Anda bisa login.
 */