package com.Backend.SpringBoot.E_Commerce_backend.model.dao;

import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrder,Long> {
    List<WebOrder> findByUser(LocalUser user);
}
