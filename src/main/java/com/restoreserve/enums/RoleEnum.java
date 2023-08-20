package com.restoreserve.enums;

public enum RoleEnum {
    Super_Admin,App_Admin,Customer,Restaurant_Admin
}
/*
-RestoAdmin (Administrator Restoran):
Akses penuh untuk mengelola profil restoran, jadwal operasional, menu, dan reservasi.
Dapat mengkonfirmasi atau menolak permintaan reservasi dari pelanggan.
Mengelola meja yang tersedia dan mengatur kapasitas restoran.

-Pelanggan (Customer):
Akses untuk mencari restoran berdasarkan lokasi, jenis masakan, dan ketersediaan.
Dapat melihat daftar menu,dan harga.
Dapat membuat reservasi untuk waktu dan tanggal tertentu.
Dapat membatalkan atau mengubah reservasi.

-AppAdmin (Administrator Aplikasi):
Akses untuk mengelola seluruh aplikasi, termasuk pengaturan umum dan konfigurasi.
Dapat mengelola akun pengguna, mengatur peran, dan menangani masalah umum.

-SuperAdmin (Super Administrator):
Akses paling tinggi dalam sistem.
Dapat mengelola semua aspek aplikasi, termasuk pengaturan global, keuangan, dan layanan pelanggan.
Dapat mengakses semua fitur dan data dalam aplikasi. */