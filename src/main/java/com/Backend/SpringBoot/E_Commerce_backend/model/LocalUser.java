package com.Backend.SpringBoot.E_Commerce_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
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
public class LocalUser implements UserDetails {
    @Id // ini menentukan kunci utama dari entitas ini
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // ini menentukan nilai kunci utama dihasilkan secara otomatis/auto-Increment
    @Column(name = "id", nullable = false) // nama kolom id dari tabel local_user dan tidak boleh null, jadi harus diisi
    private Long id;

    @Column(name = "username", nullable = false, unique = true) // ini juga nilainya tidak boleh kosong dan harus unik
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false, length = 1000) // password tidak boleh kosong dan panjang maksimum 1000 karakter
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 320) // email tidak boleh kosong, harus unik, dan panjang maksimum 320 karakter
    private String email;

    @Column(name = "first_name", nullable = false) // nama depan tidak boleh kosong
    private String firstName;

    @Column(name = "last_name", nullable = false) // nama belakang tidak boleh kosong
    private String lastName;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.EAGER)
    // relasi satu-ke-banyak dengan Address, jika pengguna dihapus, semua alamat terkait juga dihapus, data alamat dimuat sekaligus
    private List<Address> addresses = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // relasi satu-ke-banyak dengan VerificationToken, semua operasi pada pengguna akan mempengaruhi token terkait, token akan dihapus jika pengguna dihapus
    @OrderBy("id desc")
    private List<VerificationToken> verificationTokens = new ArrayList<>();

    @Column(name="email_verified",nullable = false)
    private Boolean emailVerified=false;

    public Boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public List<VerificationToken> getVerificationTokens() {
        return verificationTokens;
    }

    public void setVerificationTokens(List<VerificationToken> verificationTokens) {
        this.verificationTokens = verificationTokens;
    }

    // Getter dan Setter untuk properti-properti di atas
    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
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

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
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

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isEnabled() {
        return true;
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
/*
Ilustrasi Sederhana
Bayangkan Anda memiliki aplikasi toko online yang memiliki pengguna dan alamat pengguna.

Ilustrasi LocalUser
ID Pengguna:

Setiap pengguna memiliki ID unik yang dihasilkan secara otomatis.
Misalnya: id: 1
Username, Password, Email, Nama Depan, Nama Belakang:

Setiap pengguna memiliki username, password, email, nama depan, dan nama belakang yang unik.
Misalnya: username: "john_doe", password: "hashed_password", email: "john@example.com", firstName: "John", lastName: "Doe"
Daftar Alamat (Address):

Setiap pengguna dapat memiliki banyak alamat.
Misalnya, John Doe memiliki dua alamat: satu untuk rumah dan satu untuk kantor.
Saat Anda memuat pengguna John Doe, semua alamatnya juga akan dimuat.
Daftar Token Verifikasi (VerificationToken):

Setiap pengguna dapat memiliki beberapa token verifikasi untuk keperluan keamanan.
Misalnya, John Doe memiliki token verifikasi untuk memverifikasi emailnya.
 */

/*
fetch = FetchType.EAGER:

Penjelasan:
Menandakan bahwa data terkait akan dimuat sekaligus saat entitas utama dimuat dari database.
Tidak perlu melakukan query tambahan untuk mendapatkan data terkait.
Contoh:
Misalnya, Anda memuat pengguna dengan ID 1 dari database. Jika fetch = FetchType.EAGER, maka semua alamat yang terkait dengan pengguna tersebut juga akan dimuat secara otomatis.
Ilustrasi:
Sebelum: Anda memuat pengguna, kemudian memuat alamat-alamat secara terpisah.
Sesudah: Anda memuat pengguna, dan alamat-alamat langsung ikut dimuat.
cascade = CascadeType.REMOVE:

Penjelasan:
Menandakan bahwa saat entitas utama dihapus, semua entitas terkait juga akan dihapus dari database.
Contoh:
Misalnya, Anda menghapus pengguna dengan ID 1. Jika cascade = CascadeType.REMOVE, maka semua alamat yang terkait dengan pengguna tersebut juga akan dihapus.
Ilustrasi:
Sebelum: Anda menghapus pengguna, kemudian menghapus alamat-alamat secara terpisah.
Sesudah: Anda menghapus pengguna, dan alamat-alamat terkait langsung ikut terhapus.
cascade = CascadeType.ALL:

Penjelasan:
Menandakan bahwa semua operasi pada entitas utama (simpan, hapus, perbarui) akan diterapkan juga pada entitas terkait.
Contoh:
Misalnya, Anda menyimpan atau memperbarui pengguna dengan ID 1. Jika cascade = CascadeType.ALL, maka semua token verifikasi yang terkait dengan pengguna tersebut juga akan disimpan atau diperbarui.
Ilustrasi:
Sebelum: Anda menyimpan atau memperbarui pengguna, kemudian menyimpan atau memperbarui token verifikasi secara terpisah.
Sesudah: Anda menyimpan atau memperbarui pengguna, dan token verifikasi terkait langsung ikut disimpan atau diperbarui.
 */