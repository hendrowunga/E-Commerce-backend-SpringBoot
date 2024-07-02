package com.Backend.SpringBoot.E_Commerce_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
/*
fetch = FetchType.EAGER:

Deskripsi: Saat Anda memuat sebuah entitas dari database, data terkait (misalnya, relasi) akan dimuat sekaligus. Jadi, tidak perlu melakukan query tambahan untuk mendapatkan data terkait tersebut.
Contoh: Jika Anda memuat pengguna dari database, semua alamat yang terkait dengan pengguna tersebut juga akan dimuat secara otomatis.
@JsonIgnore:

Deskripsi: Anotasi ini digunakan untuk mengabaikan properti tertentu saat objek Java diubah menjadi JSON atau sebaliknya. Properti yang diabaikan tidak akan muncul dalam output JSON.
Contoh: Jika objek pengguna memiliki daftar alamat, dan Anda menandai daftar alamat dengan @JsonIgnore, maka daftar alamat tersebut tidak akan muncul dalam JSON ketika objek pengguna dikonversi ke JSON.
 */

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
    @JsonIgnore
    @Column(name = "password", nullable = false, length = 1000)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true,fetch = FetchType.EAGER)
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
