package com.Backend.SpringBoot.E_Commerce_backend.model.dao;

import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
/*
WebOrderDAO merupakan sebuah interface yang diperuntukan unutk mengakses data WebOrder dari database.
Antarmuka ini menggunkan "ListCrudRepository" dari Spring Data,yang menyediakan metode CRUD dasar untuk entitas "WebOrder"

Metode interface WebOrderDAO:
sebuah antarmuka yang meng-extend ListCrudRepository dengan tipe entitas WebOrder dan tipe ID Long.
ListCrudRepository adalah sub-antarmuka dari CrudRepository yang memungkinkan hasil berupa List.

Metode Kustom:
Metode ini adalah metode kustom yang dideklarasikan untuk mencari daftar "WebOrder"
berdasarkan pengguna "LocalUser". Spring Data JPA secara otomatis akan mengimplementasikan metode ini berdasarkan
konvensi penamaan.
*/
public interface WebOrderDAO extends ListCrudRepository<WebOrder,Long> {
    List<WebOrder> findByUser(LocalUser user);
}

/*
Ilustrasi:
Pembuatan dan Penggunaan DAO:
WebOrderDAO digunakan dalam layanan atau komponen lain untuk mengakses data WebOrder dari database.
Misalnya, saat ingin mendapatkan semua pesanan (WebOrder) yang terkait dengan pengguna tertentu (LocalUser), metode findByUser dapat digunakan.

Spring Data JPA:
Spring Data JPA akan membuat implementasi konkret dari antarmuka WebOrderDAO saat aplikasi Spring Boot berjalan.
Ini menghemat waktu dan usaha karena Anda tidak perlu menulis implementasi DAO secara manual.
 */