package com.Backend.SpringBoot.E_Commerce_backend.api.controller.user;

import com.Backend.SpringBoot.E_Commerce_backend.api.model.DataChange;
import com.Backend.SpringBoot.E_Commerce_backend.model.Address;
import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.dao.AddressDAO;
import com.Backend.SpringBoot.E_Commerce_backend.services.UserServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private AddressDAO addressDAO;
    private SimpMessagingTemplate simpMessagingTemplate;
    private UserServices userServices;

    public UserController(AddressDAO addressDAO,SimpMessagingTemplate simpMessagingTemplate,UserServices userServices) {
        this.addressDAO = addressDAO;
        this.simpMessagingTemplate=simpMessagingTemplate;
        this.userServices=userServices;
    }

    @GetMapping("/{userId}/address")
    public ResponseEntity<List<Address>> getAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId) {
        if (!userServices.userHasPermissionToUser(user, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(addressDAO.findByUser_Id(userId));
    }


    @PutMapping("/{userId}/address")
    public ResponseEntity<Address> putAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId, @RequestBody Address address) {
        if (!userServices.userHasPermissionToUser(user, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);
        LocalUser refUser = new LocalUser();
        refUser.setId(userId);
        address.setUser(refUser);
        Address savedAddress = addressDAO.save(address);
        simpMessagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
                new DataChange<>(DataChange.ChangeType.INSERT, address));
        return ResponseEntity.ok(savedAddress);
    }

    @PatchMapping("/{userId}/address/{addressId}")
    public ResponseEntity<Address> patchAddress(
            @AuthenticationPrincipal LocalUser user, @PathVariable Long userId,
            @PathVariable Long addressId, @RequestBody Address address) {
        if (!userServices.userHasPermissionToUser(user, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (address.getId() == addressId) {
            Optional<Address> opOriginalAddress = addressDAO.findById(addressId);
            if (opOriginalAddress.isPresent()) {
                LocalUser originalUser = opOriginalAddress.get().getUser();
                if (originalUser.getId() == userId) {
                    address.setUser(originalUser);
                    Address savedAddress = addressDAO.save(address);
                    simpMessagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
                            new DataChange<>(DataChange.ChangeType.UPDATE, address));
                    return ResponseEntity.ok(savedAddress);
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }


}
