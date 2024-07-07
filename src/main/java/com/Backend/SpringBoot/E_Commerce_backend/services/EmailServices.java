package com.Backend.SpringBoot.E_Commerce_backend.services;

import com.Backend.SpringBoot.E_Commerce_backend.exception.EmailFailureException; // Mengimpor exception khusus untuk penanganan kesalahan email
import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.VerificationToken; // Mengimpor model VerificationToken
import org.springframework.beans.factory.annotation.Value; // Mengimpor anotasi untuk membaca nilai dari properti aplikasi
import org.springframework.mail.MailException; // Mengimpor exception untuk kesalahan pengiriman email
import org.springframework.mail.SimpleMailMessage; // Mengimpor kelas untuk membuat pesan email sederhana
import org.springframework.mail.javamail.JavaMailSender; // Mengimpor kelas untuk mengirim email dengan JavaMail
import org.springframework.stereotype.Service; // Mengimpor anotasi untuk menandakan kelas ini sebagai service Spring

@Service // Menandakan bahwa kelas ini adalah service Spring
public class EmailServices {
    @Value("${email.from}") // Membaca nilai dari properti aplikasi untuk alamat pengirim email
    private String fromAddress;
    @Value("${app.frontend.url}") // Membaca nilai dari properti aplikasi untuk URL frontend
    private String url;
    private JavaMailSender javaMailSender; // Objek untuk mengirim email

    // Konstruktor untuk menyuntikkan objek JavaMailSender
    public EmailServices(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    // Metode untuk membuat pesan email sederhana dengan pengirim yang telah diatur
    private SimpleMailMessage makeMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAddress); // Mengatur alamat pengirim
        return simpleMailMessage; // Mengembalikan objek pesan email sederhana
    }

    // Metode untuk mengirim email verifikasi dengan token verifikasi
    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage(); // Membuat pesan email dasar
        message.setTo(verificationToken.getUser().getEmail()); // Mengatur alamat penerima
        message.setSubject("Verify your email to activate your account."); // Mengatur subjek email
        message.setText("Please follow the link below to verify your email to activate your account.\n" +
                url + "/auth/verify?token=" + verificationToken.getToken()); // Mengatur isi email dengan link verifikasi
        try {
            javaMailSender.send(message); // Mengirim email
        } catch (MailException ex) {
            throw new EmailFailureException(); // Melempar exception jika pengiriman email gagal
        }
    }
    public void sendPasswordResetEmail(LocalUser user, String token) throws EmailFailureException {
        SimpleMailMessage message = makeMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Your password reset request link.");
        message.setText("You requested a password reset on our website. Please " +
                "find the link below to be able to reset your password.\n" + url +
                "/auth/reset?token=" + token);
        try {
            javaMailSender.send(message);
        } catch (MailException ex) {
            throw new EmailFailureException();
        }
    }
}
/*
Ilustrasi
Bayangkan Anda memiliki aplikasi yang perlu mengirim email verifikasi saat pengguna mendaftar.

Ilustrasi EmailServices
Membuat Pesan Email:

Sistem membuat pesan email dasar dengan pengirim yang sudah diatur.
Misalnya: fromAddress: "noreply@yourapp.com"
Mengirim Email Verifikasi:

Sistem mengirim email verifikasi ke pengguna yang baru mendaftar.
Misalnya, pengguna baru dengan email john@example.com mendaftar.
Sistem membuat pesan dengan alamat penerima john@example.com, subjek "Verify your email to activate your account.", dan isi email berisi link verifikasi.
 */