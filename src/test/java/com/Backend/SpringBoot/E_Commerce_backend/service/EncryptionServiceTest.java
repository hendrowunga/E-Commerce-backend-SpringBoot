package com.Backend.SpringBoot.E_Commerce_backend.service;

import com.Backend.SpringBoot.E_Commerce_backend.services.EncryptionServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EncryptionServiceTest {
    @Autowired
    private EncryptionServices encryptionServices;

    @Test
    public void testPasswordEncryption(){
        String password="PasswordIsASecret!123";
        String hash=encryptionServices.encryptPassword(password);
        Assertions.assertTrue(encryptionServices.verifyPassword(password,hash),"Hashed password should match original");
        Assertions.assertFalse(encryptionServices.verifyPassword(password + "Sike!",hash),"Altered password should not be valid.");
    }
}
