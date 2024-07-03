package com.Backend.SpringBoot.E_Commerce_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity // Menandakan bahwa kelas ini adalah entitas JPA yang akan dipetakan ke tabel database
@Table(name = "address") // Menentukan nama tabel di database untuk entitas ini
public class Address {
    @Id // Menentukan bahwa properti 'id' adalah kunci utama dari entitas ini
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Menentukan bahwa nilai kunci utama akan dihasilkan secara otomatis oleh database (auto-increment)
    @Column(name = "id", nullable = false) // Nama kolom 'id' di tabel 'address' dan kolom ini tidak boleh null
    private Long id;

    @Column(name = "address_line_1", nullable = false, length = 512) // Nama kolom 'address_line_1', nilainya tidak boleh kosong dan maksimal panjangnya 512 karakter
    private String addressLine1;

    @Column(name = "address_Line_2", length = 512) // Nama kolom 'address_Line_2', maksimal panjangnya 512 karakter, nilainya boleh kosong
    private String addressLine2;

    @Column(name = "city", nullable = false) // Nama kolom 'city', nilainya tidak boleh kosong
    private String city;

    @Column(name = "country", nullable = false, length = 75) // Nama kolom 'country', nilainya tidak boleh kosong dan maksimal panjangnya 75 karakter
    private String country;

    @JsonIgnore // Mengabaikan properti ini saat objek diubah menjadi JSON untuk menjaga keamanan
    @ManyToOne(optional = false) // Menandakan hubungan banyak-ke-satu antara Address dan LocalUser, setiap alamat harus memiliki pengguna
    @JoinColumn(name = "user_id", nullable = false) // Menentukan kolom 'user_id' sebagai kunci asing yang mengacu pada kolom 'id' dalam tabel 'local_user', kolom ini tidak boleh kosong
    private LocalUser user;

    // Getter dan setter untuk properti 'user'
    public LocalUser getUser() {
        return user;
    }

    public void setUser(LocalUser user) {
        this.user = user;
    }

    // Getter dan setter untuk properti 'city'
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Getter dan setter untuk properti 'addressLine2'
    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    // Getter dan setter untuk properti 'addressLine1'
    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    // Getter dan setter untuk properti 'id'
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
