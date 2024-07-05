package com.Backend.SpringBoot.E_Commerce_backend.model.dao;

import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenDAO extends ListCrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);
    List<VerificationToken> findByUser_IdOrderByDesc(Long id);
}
