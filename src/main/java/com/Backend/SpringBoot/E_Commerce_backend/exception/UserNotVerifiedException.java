package com.Backend.SpringBoot.E_Commerce_backend.exception;

// Membuat kelas pengecualian khusus yang bernama UserNotVerifiedException
public class UserNotVerifiedException extends Exception {
    // Properti untuk menyimpan informasi apakah email baru telah dikirim
    private boolean newEmailSent;

    // Konstruktor yang menerima parameter boolean untuk mengatur nilai newEmailSent
    public UserNotVerifiedException(boolean newEmailSent) {
        // Mengatur nilai properti newEmailSent dengan nilai yang diterima dari parameter
        this.newEmailSent = newEmailSent;
    }

    // Metode untuk memeriksa apakah email baru telah dikirim
    public boolean isNewEmailSent() {
        // Mengembalikan nilai properti newEmailSent
        return newEmailSent;
    }
}

