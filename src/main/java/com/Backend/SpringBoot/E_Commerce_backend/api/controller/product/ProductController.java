package com.Backend.SpringBoot.E_Commerce_backend.api.controller.product;

import com.Backend.SpringBoot.E_Commerce_backend.model.Product;
import com.Backend.SpringBoot.E_Commerce_backend.services.ProductServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private ProductServices productServices;

    public ProductController(ProductServices productServices) {
        this.productServices = productServices;
    }

    @GetMapping
    public List<Product> getProducts() {
        return productServices.getProducts();
    }
}
/*
ProductController adalah kelas yang menangani permintaan terkait produk dari aplikasi klien.
Di dalamnya, terdapat metode getProducts yang digunakan untuk mendapatkan daftar semua produk yang tersedia.
@GetMapping menandakan bahwa metode ini menanggapi permintaan HTTP GET ke endpoint /product, yang berarti aplikasi klien dapat meminta daftar produk melalui URL tersebut.
Kita menggunakan ProductServices untuk memproses permintaan dan mengambil daftar produk dari database atau sumber data lainnya.
Kontroler ini memungkinkan aplikasi klien untuk mengambil informasi produk yang tersedia dari backend, seperti nama produk, harga, dan deskripsi.
 */
