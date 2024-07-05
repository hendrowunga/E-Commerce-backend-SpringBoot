package com.Backend.SpringBoot.E_Commerce_backend.service;


import com.Backend.SpringBoot.E_Commerce_backend.api.model.LoginBody;
import com.Backend.SpringBoot.E_Commerce_backend.exception.EmailFailureException;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserNotVerifiedException;
import com.Backend.SpringBoot.E_Commerce_backend.services.UserServices;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.Backend.SpringBoot.E_Commerce_backend.api.model.RegistrationBody;
import com.Backend.SpringBoot.E_Commerce_backend.exception.UserAlreadyExistsException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class UserServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("Endos", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private UserServices userService;


    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        RegistrationBody body = new RegistrationBody();
        body.setUsername("UserA");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        body.setFirstName("FirstName");
        body.setLastName("LastName");
        body.setPassword("MySecretPassword123");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Username should already be in use.");
        body.setUsername("UserServiceTest$testRegisterUser");
        body.setEmail("UserA@junit.com");
        Assertions.assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(body), "Email should already be in use.");
        body.setEmail("UserServiceTest$testRegisterUser@junit.com");
        Assertions.assertDoesNotThrow(() -> userService.registerUser(body),
                "User should register successfully.");
        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
                .getRecipients(Message.RecipientType.TO)[0].toString());
    }
    @Test
    @Transactional
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody body=new LoginBody();

        body.setUsername("UserA-NoExists");
        body.setPassword("PasswordA123-BadPassword");
        Assertions.assertNull(userService.loginUser(body),"The user should not exits");
        body.setUsername("UserA");
        Assertions.assertNull(userService.loginUser(body),"The password should be incorrect");
        body.setPassword("PasswordA123");
        Assertions.assertNotNull(userService.loginUser(body),"The user should login successfully");
        body.setUsername("UserB");
        body.setPassword("PasswordB123");
        try{
            userService.loginUser(body);
            Assertions.assertTrue(false,"User should not have email verified");

        }catch(UserNotVerifiedException ex){
            Assertions.assertTrue(ex.isNewEmailSent(),"Email verification should be sent");
            Assertions.assertEquals(1,greenMailExtension.getReceivedMessages().length);
        }

        try{
            userService.loginUser(body);
            Assertions.assertTrue(false,"User should not have email verified");

        }catch(UserNotVerifiedException ex){
            Assertions.assertFalse(ex.isNewEmailSent(),"Email verification should not be resent");
            Assertions.assertEquals(1,greenMailExtension.getReceivedMessages().length);
        }
    }

}