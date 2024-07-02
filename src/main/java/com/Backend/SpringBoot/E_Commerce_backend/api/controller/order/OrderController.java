package com.Backend.SpringBoot.E_Commerce_backend.api.controller.order;

import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.WebOrder;
import com.Backend.SpringBoot.E_Commerce_backend.services.OrderServices;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")

public class OrderController {
    private OrderServices orderServices;

    public OrderController(OrderServices orderServices) {
        this.orderServices = orderServices;
    }
    @GetMapping
    public List<WebOrder> getOrders(@AuthenticationPrincipal LocalUser user){
        return orderServices.getOrders(user);
    }
}
/*
OrderController adalah kelas yang menangani permintaan terkait pesanan dari aplikasi klien.
Di dalamnya, terdapat metode getOrders yang digunakan untuk mendapatkan daftar pesanan dari pengguna yang sedang login.
@GetMapping menandakan bahwa metode ini menanggapi permintaan HTTP GET ke endpoint /order, yang berarti aplikasi klien dapat meminta daftar pesanan melalui URL tersebut.
@AuthenticationPrincipal LocalUser user digunakan untuk mendapatkan informasi pengguna yang sedang login, sehingga kita dapat mengetahui pesanan dari pengguna tersebut.
Kita menggunakan OrderServices untuk memproses permintaan dan mengambil daftar pesanan dari pengguna yang sedang login.
 */
