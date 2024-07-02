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
