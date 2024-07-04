package com.Backend.SpringBoot.E_Commerce_backend.api.controller.product;

import com.Backend.SpringBoot.E_Commerce_backend.model.Product;
import com.Backend.SpringBoot.E_Commerce_backend.services.ProductServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
// Menandai kelas ini sebagai kontroler REST yang menangani permintaan HTTP di jalur "/product".

public class ProductController {
    private ProductServices productServices;

    public ProductController(ProductServices productServices) {
        this.productServices = productServices;
    }
    // Konstruktor untuk menginisialisasi layanan produk (ProductServices).

    @GetMapping // Menangani permintaan HTTP GET ke "/product".
    public List<Product> getProducts() {
        return productServices.getProducts();  // Mengembalikan daftar produk yang diperoleh dari layanan produk (ProductServices).
    }
}

/*
Ilustrasi Sederhana
Pengguna: Jane Doe mengakses aplikasi e-commerce melalui browser atau aplikasi mobile.

Permintaan GET: Jane mengunjungi halaman "Produk" di aplikasi, yang mengirim permintaan HTTP GET ke endpoint "/product".

Memanggil Layanan Produk: Metode getProducts dalam ProductController memanggil productServices.getProducts() untuk mendapatkan daftar semua produk yang tersedia.

Mengembalikan Daftar Produk: productServices.getProducts() mengembalikan daftar produk yang kemudian dikirim kembali sebagai respons HTTP dalam bentuk JSON.
 */
