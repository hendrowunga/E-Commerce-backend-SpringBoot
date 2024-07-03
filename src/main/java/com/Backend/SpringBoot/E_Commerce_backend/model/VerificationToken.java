package com.Backend.SpringBoot.E_Commerce_backend.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity // Menandakan bahwa kelas ini adalah entitas yang akan dipetakan ke tabel database
@Table(name = "verification_token") // Menentukan nama tabel di database
public class VerificationToken {
    @Id // Menentukan bahwa properti ini adalah kunci utama
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Nilai kunci utama dihasilkan secara otomatis (auto-increment)
    @Column(name = "id", nullable = false) // Kolom id tidak boleh kosong
    private Long id;

    @Lob // Menandakan bahwa kolom ini akan menyimpan data dalam ukuran besar seperti teks yang panjang ataupun file
    @Column(name = "token", nullable = false, unique = true) // Kolom token tidak boleh kosong dan harus unik
    private String token;

    @Column(name = "created_timestamp", nullable = false) // Kolom created_timestamp tidak boleh kosong
    private Timestamp createdTimestamp;

    @ManyToOne(optional = false) // Menandakan bahwa ada banyak VerificationToken yang berhubungan dengan satu LocalUser
    @JoinColumn(name = "user_id", nullable = false) // Kolom user_id tidak boleh kosong dan digunakan untuk menyimpan referensi ke LocalUser
    private LocalUser user;

    // Getter dan Setter untuk properti-properti di atas

    public LocalUser getUser() {
        return user; // Mengembalikan pengguna yang terkait dengan token ini
    }

    public void setUser(LocalUser user) {
        this.user = user; // Menetapkan pengguna yang terkait dengan token ini
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp; // Mengembalikan timestamp kapan token ini dibuat
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp; // Menetapkan timestamp kapan token ini dibuat
    }

    public String getToken() {
        return token; // Mengembalikan nilai token
    }

    public void setToken(String token) {
        this.token = token; // Menetapkan nilai token
    }

    public Long getId() {
        return id; // Mengembalikan nilai id
    }

    public void setId(Long id) {
        this.id = id; // Menetapkan nilai id
    }
}
