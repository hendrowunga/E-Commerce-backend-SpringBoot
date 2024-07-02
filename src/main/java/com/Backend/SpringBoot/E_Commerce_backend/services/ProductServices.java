package com.Backend.SpringBoot.E_Commerce_backend.services;

import com.Backend.SpringBoot.E_Commerce_backend.model.Product;
import com.Backend.SpringBoot.E_Commerce_backend.model.dao.ProductDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServices {
    private ProductDAO productDAO;

    public ProductServices(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }
    public List<Product> getProducts(){
        return productDAO.findAll();
    }
}
/*
ProductServices adalah kelas yang membantu kita mengelola semua produk di toko online.
Di dalamnya, kita menggunakan ProductDAO (Data Access Object) untuk mengakses dan memanipulasi data produk dari database.
Fungsinya getProducts digunakan untuk mengambil semua produk yang tersedia dari database.
 */