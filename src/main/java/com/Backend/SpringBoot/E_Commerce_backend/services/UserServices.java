package com.Backend.SpringBoot.E_Commerce_backend.services;

import com.Backend.SpringBoot.E_Commerce_backend.api.model.RegistrationBody;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserAlreadyExistsException;
import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.dao.LocalUserDAO;
import org.springframework.stereotype.Service;

/*
@Service: Anotasi ini menunjukkan bahwa kelas UserServices adalah sebuah service dalam konteks Spring. Service digunakan untuk menampung logika bisnis aplikasi dan sering digunakan untuk mengelola operasi yang terkait dengan database atau proses bisnis.
*/
@Service
public class UserServices {

    private LocalUserDAO localUserDAO;

    public UserServices(LocalUserDAO localUserDAO) {
        this.localUserDAO = localUserDAO;
    }

    public LocalUser registerUser( RegistrationBody registrationBody) throws UserAlreadyExistsException {

        if (localUserDAO.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent() ||
                localUserDAO.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        LocalUser user = new LocalUser();
        user.setUsername(registrationBody.getUsername());
        user.setEmail(registrationBody.getEmail());
        user.setFirstName(registrationBody.getFirstName());
        user.setLastName(registrationBody.getLastName());

        //TODO:Encrypt password!!
        user.setPassword(registrationBody.getPassword());
        return localUserDAO.save(user);
    }
}
