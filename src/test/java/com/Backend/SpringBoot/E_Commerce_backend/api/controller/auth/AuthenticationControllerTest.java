package com.Backend.SpringBoot.E_Commerce_backend.api.controller.auth;

import com.Backend.SpringBoot.E_Commerce_backend.api.model.RegistrationBody; // Mengimpor model RegistrationBody
import com.fasterxml.jackson.databind.ObjectMapper; // Mengimpor ObjectMapper untuk konversi objek Java ke JSON
import com.icegreen.greenmail.configuration.GreenMailConfiguration; // Mengimpor konfigurasi GreenMail
import com.icegreen.greenmail.junit5.GreenMailExtension; // Mengimpor ekstensi GreenMail untuk pengujian email
import com.icegreen.greenmail.util.ServerSetupTest; // Mengimpor ServerSetupTest untuk konfigurasi server email uji
import jakarta.transaction.Transactional; // Mengimpor anotasi untuk manajemen transaksi
import org.junit.jupiter.api.Test; // Mengimpor anotasi untuk pengujian
import org.junit.jupiter.api.extension.RegisterExtension; // Mengimpor anotasi untuk registrasi ekstensi
import org.springframework.beans.factory.annotation.Autowired; // Mengimpor anotasi untuk injeksi dependensi
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // Mengimpor anotasi untuk konfigurasi MockMvc
import org.springframework.boot.test.context.SpringBootTest; // Mengimpor anotasi untuk pengujian aplikasi Spring Boot
import org.springframework.http.HttpStatus; // Mengimpor status HTTP
import org.springframework.http.MediaType; // Mengimpor tipe media HTTP
import org.springframework.test.web.servlet.MockMvc; // Mengimpor MockMvc untuk pengujian pengontrol web

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // Mengimpor pembuat permintaan POST untuk MockMvc
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // Mengimpor pengecek status untuk hasil MockMvc

@SpringBootTest // Menandakan bahwa ini adalah kelas pengujian Spring Boot
@AutoConfigureMockMvc // Mengonfigurasi MockMvc untuk pengujian pengontrol web
public class AuthenticationControllerTest {

    @RegisterExtension // Mendaftarkan ekstensi GreenMail untuk pengujian email
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("Endos", "secret")) // Menyiapkan server SMTP dengan pengguna uji
            .withPerMethodLifecycle(true); // Menyetel siklus hidup per metode untuk GreenMail

    @Autowired // Menginjeksi MockMvc ke dalam kelas ini
    private MockMvc mvc;

    @Test // Menandakan bahwa ini adalah metode pengujian
    @Transactional // Menandakan bahwa metode ini berjalan dalam konteks transaksi
    public void testRegister() throws Exception {
        ObjectMapper mapper = new ObjectMapper(); // Membuat instance ObjectMapper untuk konversi objek ke JSON
        RegistrationBody body = new RegistrationBody(); // Membuat instance RegistrationBody untuk permintaan registrasi
        body.setEmail("AuthenticationControllerTest$testRegister@junit.com"); // Mengatur email dalam permintaan registrasi
        body.setFirstName("FirstName"); // Mengatur nama depan dalam permintaan registrasi
        body.setLastName("LastName"); // Mengatur nama belakang dalam permintaan registrasi
        body.setPassword("Password123"); // Mengatur kata sandi dalam permintaan registrasi
        // Null or blank username.
        body.setUsername(null); // Mengatur username null untuk menguji validasi
        mvc.perform(post("/auth/register") // Mengirim permintaan POST ke endpoint /auth/register
                        .contentType(MediaType.APPLICATION_JSON) // Mengatur tipe konten permintaan ke JSON
                        .content(mapper.writeValueAsString(body))) // Mengonversi objek body ke JSON dan mengirim sebagai konten permintaan
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value())); // Memeriksa bahwa respons adalah BAD_REQUEST
        body.setUsername(""); // Mengatur username kosong untuk menguji validasi
        mvc.perform(post("/auth/register") // Mengirim permintaan POST lagi dengan username kosong
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setUsername("AuthenticationControllerTest$testRegister"); // Mengatur username yang valid
        // Null or blank email.
        body.setEmail(null); // Mengatur email null untuk menguji validasi
        mvc.perform(post("/auth/register") // Mengirim permintaan POST dengan email null
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setEmail(""); // Mengatur email kosong untuk menguji validasi
        mvc.perform(post("/auth/register") // Mengirim permintaan POST dengan email kosong
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setEmail("AuthenticationControllerTest$testRegister@junit.com"); // Mengatur email yang valid
        // Null or blank password.
        body.setPassword(null); // Mengatur kata sandi null untuk menguji validasi
        mvc.perform(post("/auth/register") // Mengirim permintaan POST dengan kata sandi null
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setPassword(""); // Mengatur kata sandi kosong untuk menguji validasi
        mvc.perform(post("/auth/register") // Mengirim permintaan POST dengan kata sandi kosong
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setPassword("Password123"); // Mengatur kata sandi yang valid
        // Null or blank first name.
        body.setFirstName(null); // Mengatur nama depan null untuk menguji validasi
        mvc.perform(post("/auth/register") // Mengirim permintaan POST dengan nama depan null
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setFirstName(""); // Mengatur nama depan kosong untuk menguji validasi
        mvc.perform(post("/auth/register") // Mengirim permintaan POST dengan nama depan kosong
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setFirstName("FirstName"); // Mengatur nama depan yang valid
        // Null or blank last name.
        body.setLastName(null); // Mengatur nama belakang null untuk menguji validasi
        mvc.perform(post("/auth/register") // Mengirim permintaan POST dengan nama belakang null
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setLastName(""); // Mengatur nama belakang kosong untuk menguji validasi
        mvc.perform(post("/auth/register") // Mengirim permintaan POST dengan nama belakang kosong
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        body.setLastName("LastName"); // Mengatur nama belakang yang valid
        //TODO: Test password characters, username length & email validity. // Menandai bahwa pengujian tambahan diperlukan untuk karakter kata sandi, panjang username, dan validitas email
        // Valid registration.
        mvc.perform(post("/auth/register") // Mengirim permintaan POST dengan semua data valid
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.OK.value())); // Memeriksa bahwa respons adalah OK
    }

}

/*
Penjelasan dan Ilustrasi

Deklarasi Kelas dan Anotasi:
@SpringBootTest: Menandakan bahwa ini adalah kelas pengujian untuk aplikasi Spring Boot.
@AutoConfigureMockMvc: Mengonfigurasi MockMvc untuk pengujian pengontrol web.
AuthenticationControllerTest: Kelas pengujian untuk pengontrol autentikasi.

Ilustrasi:
Bayangkan Anda sedang menyiapkan pengujian untuk endpoint registrasi di aplikasi Anda. Anotasi @SpringBootTest dan @AutoConfigureMockMvc membantu Anda melakukan ini dalam konteks aplikasi Spring Boot dan pengontrol web.

Menyuntikkan Dependensi:
@RegisterExtension: Mendaftarkan ekstensi GreenMail untuk pengujian email.
@Autowired: Menginjeksi MockMvc ke dalam kelas ini.

Ilustrasi:
Bayangkan Anda memiliki server email uji dan pengontrol web yang ingin Anda uji. Anotasi @RegisterExtension dan @Autowired membantu Anda menyuntikkan dan mengonfigurasi dependensi ini ke dalam kelas pengujian.

Metode Pengujian Registrasi (testRegister):
@Test: Menandakan bahwa metode ini adalah metode pengujian.
@Transactional: Menandakan bahwa metode ini berjalan dalam konteks transaksi.
ObjectMapper mapper = new ObjectMapper(): Membuat instance ObjectMapper untuk konversi objek ke JSON.
RegistrationBody body = new RegistrationBody(): Membuat instance RegistrationBody untuk permintaan registrasi.
mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body))).andExpect(status().is(HttpStatus.BAD_REQUEST.value())): Mengirim permintaan POST dengan konten JSON dan memeriksa status respons.

Ilustrasi:
Bayangkan Anda ingin menguji endpoint registrasi dengan berbagai kondisi data, seperti username null, email kosong, atau kata sandi kosong. Anda mengirim permintaan POST dengan data yang tidak valid dan memeriksa bahwa respons adalah BAD_REQUEST. Kemudian Anda mengirim permintaan dengan data yang valid dan memeriksa bahwa respons adalah OK.


Ilustrasi Detail

Pengujian dengan Data Tidak Valid:
- Username null atau kosong: Menguji bahwa endpoint registrasi menolak username yang tidak valid.
- Email null atau kosong: Menguji bahwa endpoint registrasi menolak email yang tidak valid.
- Password null atau kosong: Menguji bahwa endpoint registrasi menolak kata sandi yang tidak valid.
- Nama depan null atau kosong: Menguji bahwa endpoint registrasi menolak nama depan yang tidak valid.
- Nama belakang null atau kosong: Menguji bahwa endpoint registrasi menolak nama belakang yang tidak valid.

Pengujian dengan Data Valid:
- Semua data valid: Menguji bahwa endpoint registrasi menerima dan memproses permintaan dengan data yang valid, menghasilkan respons OK.
 */