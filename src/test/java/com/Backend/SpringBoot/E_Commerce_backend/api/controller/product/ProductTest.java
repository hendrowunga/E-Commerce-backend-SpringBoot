package com.Backend.SpringBoot.E_Commerce_backend.api.controller.product;

import org.junit.jupiter.api.Test; // Mengimpor anotasi @Test dari JUnit untuk menandakan metode pengujian
import org.springframework.beans.factory.annotation.Autowired; // Mengimpor anotasi @Autowired untuk menyuntikkan dependensi secara otomatis
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // Mengimpor anotasi @AutoConfigureMockMvc untuk mengkonfigurasi MockMvc secara otomatis
import org.springframework.boot.test.context.SpringBootTest; // Mengimpor anotasi @SpringBootTest untuk menandakan bahwa ini adalah kelas pengujian Spring Boot
import org.springframework.http.HttpStatus; // Mengimpor kelas HttpStatus untuk menggunakan status HTTP
import org.springframework.test.web.servlet.MockMvc; // Mengimpor kelas MockMvc untuk melakukan pengujian pada layer web

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get; // Mengimpor metode get untuk membuat permintaan GET
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status; // Mengimpor status untuk memeriksa status respons

@SpringBootTest // Menandakan bahwa ini adalah kelas pengujian Spring Boot
@AutoConfigureMockMvc // Mengkonfigurasi MockMvc secara otomatis
public class ProductTest {

    @Autowired // Menyuntikkan MockMvc secara otomatis
    private MockMvc mockMvc;

    @Test // Menandakan bahwa metode ini adalah metode pengujian
    public void testProductList() throws Exception { // Metode pengujian untuk memeriksa daftar produk
        mockMvc.perform(get("/product")) // Melakukan permintaan GET ke endpoint "/product"
                .andExpect(status().is(HttpStatus.OK.value())); // Memeriksa bahwa status respons adalah 200 OK
    }
}
