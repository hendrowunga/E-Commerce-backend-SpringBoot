package com.Backend.SpringBoot.E_Commerce_backend.model.dao;

import com.Backend.SpringBoot.E_Commerce_backend.model.Product;
import org.springframework.data.repository.ListCrudRepository;


public interface ProductDAO extends ListCrudRepository<Product,Long> {


}
