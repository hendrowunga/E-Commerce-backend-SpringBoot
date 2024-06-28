package com.Backend.SpringBoot.E_Commerce_backend.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "local_user")
public class LocalUser {
    @Id // ini menentukan kunci utama dari entitas ini
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // ini menentukan nilai kunci utama dihasilkan secara otomatis/auto-Increment
    @Column(name = "id", nullable = false) // nama colom id dari tabel local_user dan tidak boleh null,jadi harus diisi
    private Long id;

    @Column(name = "username", nullable = false, unique = true) // ini juga nilainya tidak boleh kosong dan harus unik
    private String username;

    @Column(name = "password", nullable = false, unique = true, length = 1000)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
// Menunjukkan bahwa ada hubungan satu-ke-banyak antara LocalUser dan Address. Relasi ini dipetakan oleh properti user di kelas Address.
// Atribut cascade = CascadeType.REMOVE memastikan bahwa penghapusan pengguna juga akan menghapus semua alamat terkait,
// dan orphanRemoval = true memastikan bahwa alamat yang tidak lagi terkait dengan pengguna akan dihapus.
    private List<Address> addresses = new ArrayList<>(); //Mendeklarasikan dan menginisialisasi daftar alamat untuk menyimpan alamat-alamat yang terkait dengan pengguna.

    public List<Address> getAddress() {
        return addresses;
    }

    public void setAddress(List<Address> addresses) {
        this.addresses = addresses;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
