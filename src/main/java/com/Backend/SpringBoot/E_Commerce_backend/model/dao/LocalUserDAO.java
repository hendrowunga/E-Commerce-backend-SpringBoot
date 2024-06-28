package com.Backend.SpringBoot.E_Commerce_backend.model.dao;

import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
/*
org.springframework.data.repository.CrudRepository: Mengimpor CrudRepository dari Spring Data, yang menyediakan operasi CRUD dasar (Create, Read, Update, Delete).
java.util.Optional: Mengimpor Optional yang digunakan untuk menangani nilai yang mungkin null dengan cara yang lebih aman.
Optional<LocalUser> findByUsernameIgnoreCase(String username): Mendeklarasikan metode untuk mencari LocalUser berdasarkan username (tidak sensitif terhadap huruf besar/kecil). Mengembalikan Optional<LocalUser>, yang dapat mengandung pengguna yang ditemukan atau kosong jika tidak ada pengguna dengan username tersebut.
extends CrudRepository<LocalUser, Long>: LocalUserDAO mewarisi dari CrudRepository dengan tipe entitas LocalUser dan tipe kunci utama Long. Ini memberikan metode CRUD standar seperti save, findById, findAll, delete, dll.
 */

public interface LocalUserDAO extends CrudRepository<LocalUser, Long> {

    Optional<LocalUser> findByUsernameIgnoreCase(String username);

    Optional<LocalUser> findByEmailIgnoreCase(String email);
}
