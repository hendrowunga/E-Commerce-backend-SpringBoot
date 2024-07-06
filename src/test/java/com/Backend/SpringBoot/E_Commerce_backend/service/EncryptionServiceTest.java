package com.Backend.SpringBoot.E_Commerce_backend.service;

import com.Backend.SpringBoot.E_Commerce_backend.services.EncryptionServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest // Menandakan bahwa ini adalah kelas pengujian untuk aplikasi Spring Boot
public class EncryptionServiceTest {
    @Autowired // Menyuntikkan layanan enkripsi ke dalam kelas ini
    private EncryptionServices encryptionServices;

    @Test // Menandakan bahwa metode ini adalah metode pengujian
    public void testPasswordEncryption(){
        String password="PasswordIsASecret!123"; // Password yang akan dienkripsi
        String hash=encryptionServices.encryptPassword(password); // Menggunakan layanan enkripsi untuk mengenkripsi password
        Assertions.assertTrue(encryptionServices.verifyPassword(password,hash),"Hashed password should match original"); // Memastikan bahwa password yang dienkripsi cocok dengan password asli
        Assertions.assertFalse(encryptionServices.verifyPassword(password + "Sike!",hash),"Altered password should not be valid."); // Memastikan bahwa password yang diubah tidak valid
    }
}

/*
Penjelasan Detail dan Ilustrasi

Deklarasi Kelas dan Anotasi:
@SpringBootTest menandakan bahwa ini adalah kelas pengujian untuk aplikasi Spring Boot.
EncryptionServiceTest adalah kelas pengujian untuk layanan enkripsi.

Ilustrasi:
Bayangkan Anda menyiapkan lingkungan pengujian khusus untuk aplikasi Spring Boot Anda. Anotasi @SpringBootTest membantu Anda melakukan ini sehingga Anda dapat menguji layanan enkripsi dalam konteks aplikasi Spring Boot.

Menyuntikkan Layanan Enkripsi:
@Autowired digunakan untuk menyuntikkan EncryptionServices ke dalam kelas ini.

Ilustrasi:
Bayangkan Anda memanggil layanan enkripsi dari bagian lain aplikasi Anda dan Anda ingin mengujinya di sini. Anotasi @Autowired membantu Anda melakukan ini dengan menyuntikkan layanan enkripsi ke dalam kelas pengujian.

Metode Pengujian Password Encryption (testPasswordEncryption):
@Test menandakan bahwa ini adalah metode pengujian.
password adalah password yang akan dienkripsi.
hash adalah hasil enkripsi dari password.

Ilustrasi:
Bayangkan Anda memiliki password dan Anda ingin mengenkripsinya untuk keamanan. Anda menggunakan layanan enkripsi untuk mengenkripsi password dan menghasilkan hash.

Memeriksa Kecocokan Password yang Dienkripsi:
Assertions.assertTrue digunakan untuk memastikan bahwa password yang dienkripsi cocok dengan password asli.
Assertions.assertFalse digunakan untuk memastikan bahwa password yang diubah tidak valid.

Ilustrasi:
Bayangkan Anda memiliki password yang sudah dienkripsi. Anda ingin memeriksa apakah password asli cocok dengan hash yang dihasilkan. Anda juga ingin memastikan bahwa jika seseorang mengubah password, hash tersebut tidak valid lagi.

Ilustrasi Detail
Bayangkan Anda memiliki layanan enkripsi yang bekerja seperti ini:

- Anda memasukkan password "PasswordIsASecret!123" ke dalam layanan enkripsi.
- Layanan enkripsi mengubah password ini menjadi hash seperti "abc123hash".
- Anda kemudian ingin memeriksa apakah password asli "PasswordIsASecret!123" cocok dengan hash "abc123hash".
- Jika Anda mencoba menggunakan password yang sedikit diubah, seperti "PasswordIsASecret!123Sike!", layanan enkripsi akan memberitahu Anda bahwa hash tersebut tidak cocok.
 */