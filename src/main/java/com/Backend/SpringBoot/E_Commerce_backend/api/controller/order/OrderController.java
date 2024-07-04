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
@RequestMapping("/order") // Menandai kelas ini sebagai kontroler REST yang menangani permintaan HTTP di jalur "/order".

public class OrderController {
    private OrderServices orderServices;

    public OrderController(OrderServices orderServices) { // Konstruktor untuk menginisialisasi layanan order (OrderServices).
        this.orderServices = orderServices;
    }


    // Menangani permintaan HTTP GET ke "/order".
    @GetMapping
    // Metode untuk mendapatkan daftar pesanan (orders) untuk pengguna yang terautentikasi.
    public List<WebOrder> getOrders(@AuthenticationPrincipal LocalUser user){
        return orderServices.getOrders(user); // Mengembalikan daftar pesanan yang diperoleh dari layanan order (OrderServices).

    }
}

/*
Ilustrasi
Bayangkan Anda memiliki aplikasi e-commerce di mana pengguna dapat melihat pesanan mereka. Kelas OrderController ini bertugas menangani permintaan untuk melihat pesanan pengguna.

Ilustrasi Sederhana
Pengguna: John Doe terautentikasi dan mengakses aplikasi melalui browser atau aplikasi mobile.

Permintaan GET: John mengunjungi halaman "Pesanan Saya" di aplikasi, yang mengirim permintaan HTTP GET ke endpoint "/order".

Pengambilan Pengguna Terautentikasi: Spring Security mengenali bahwa John sudah login dan menyuntikkan objek LocalUser yang mewakili John ke dalam metode getOrders.

Memanggil Layanan Pesanan: Metode getOrders dalam OrderController memanggil orderServices.getOrders(user), di mana user adalah John Doe.

Mengembalikan Daftar Pesanan: orderServices.getOrders(user) mengembalikan daftar pesanan milik John Doe, yang kemudian dikirim kembali sebagai respons HTTP dalam bentuk JSON.
 */
