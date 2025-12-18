package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Pekerjaan {

    // ================== ATRIBUT ==================
    private String kodePekerjaan; // <-- DIPERLUKAN
    private String namaPekerjaan; // <-- DIPERLUKAN
    private int jumlahTugas;     // <-- DIPERLUKAN

    private String pesan;        // <-- DIPERLUKAN
    private Object[][] list;     // <-- DIPERLUKAN

    private final Koneksi koneksi = new Koneksi();
    
    // ================== GETTER & SETTER (WAJIB ADA) ==================
    
    // Dipanggil oleh Controller saat memuat data dari form atau saat memilih data
    public String getKodePekerjaan() {
        return kodePekerjaan;
    }

    // Dipanggil oleh Controller (pekerjaan.setKodePekerjaan(kodePekerjaan);)
    public void setKodePekerjaan(String kodePekerjaan) {
        this.kodePekerjaan = kodePekerjaan;
    }

    // Dipanggil oleh Controller (pekerjaan.getNamaPekerjaan();)
    public String getNamaPekerjaan() {
        return namaPekerjaan;
    }

    // Dipanggil oleh Controller (pekerjaan.setNamaPekerjaan(namaPekerjaan);)
    public void setNamaPekerjaan(String namaPekerjaan) {
        this.namaPekerjaan = namaPekerjaan;
    }

    // Dipanggil oleh Controller (pekerjaan.getJumlahTugas();)
    public int getJumlahTugas() {
        return jumlahTugas;
    }

    // Dipanggil oleh Controller (pekerjaan.setJumlahTugas(Integer.parseInt(jumlahTugas));)
    public void setJumlahTugas(int jumlahTugas) {
        this.jumlahTugas = jumlahTugas;
    }

    // Dipanggil oleh Controller (pekerjaan.getPesan();)
    public String getPesan() {
        return pesan;
    }

    // Dipanggil oleh Controller (pekerjaan.getList();)
    public Object[][] getList() {
        return list;
    }

    // ================== SIMPAN (METHOD YANG SUDAH KITA PERBAIKI) ==================
    public boolean simpan() {
        boolean error = false;
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = koneksi.getConnection();
            if (conn == null) {
                // Asumsi class Koneksi memiliki method getPesanKesalahan()
                throw new SQLException("Koneksi database gagal: " + "Pesan Koneksi tidak tersedia"); 
            }
            
            // Definisikan kolom secara eksplisit
            String sql = "INSERT INTO tbpekerjaan (kodePekerjaan, namaPekerjaan, jumlahTugas) VALUES (?, ?, ?)";

            ps = conn.prepareStatement(sql);
            ps.setString(1, kodePekerjaan);
            ps.setString(2, namaPekerjaan);
            ps.setInt(3, jumlahTugas); 
            
            if (ps.executeUpdate() < 1) {
                error = true;
                pesan = "Data gagal disimpan";
            }

        } catch (SQLException ex) {
            error = true;
            pesan = "SQL Error (Simpan): " + ex.getMessage();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
            }
        }

        return !error;
    }

    // ================== BACA 1 DATA ==================
    public boolean baca(String kode) {
        boolean error = false;
        Connection conn = null;
        PreparedStatement ps = null; 
        ResultSet rs = null;

        try {
            conn = koneksi.getConnection();
            if (conn == null) {
                throw new SQLException("Koneksi database gagal: " + "Pesan Koneksi tidak tersedia");
            }

            String sql = "SELECT * FROM tbpekerjaan WHERE kodepekerjaan=?"; 
            ps = conn.prepareStatement(sql);
            ps.setString(1, kode);
            rs = ps.executeQuery();

            if (rs.next()) {
                kodePekerjaan = rs.getString("kodepekerjaan");
                namaPekerjaan = rs.getString("namapekerjaan");
                jumlahTugas = rs.getInt("jumlahtugas");
            } else {
                error = true;
                pesan = "Data tidak ditemukan";
            }

        } catch (SQLException ex) {
            error = true;
            pesan = "SQL Error (Baca): " + ex.getMessage();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
            }
        }

        return !error;
    }

    // ================== HAPUS ==================
    public boolean hapus(String kode) {
        boolean error = false;
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = koneksi.getConnection();
            if (conn == null) {
                throw new SQLException("Koneksi database gagal: " + "Pesan Koneksi tidak tersedia");
            }

            String sql = "DELETE FROM tbpekerjaan WHERE kodepekerjaan=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, kode);

            if (ps.executeUpdate() < 1) {
                error = true;
                pesan = "Data gagal dihapus atau kode tidak ditemukan";
            }

        } catch (SQLException ex) {
            error = true;
            pesan = "SQL Error (Hapus): " + ex.getMessage();
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
            }
        }

        return !error;
    }

    // ================== BACA SEMUA ==================
    public boolean bacaData(int mulai, int jumlah) {
        boolean error = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        list = new Object[0][0];

        try {
            conn = koneksi.getConnection();
            if (conn == null) {
                throw new SQLException("Koneksi database gagal: " + "Pesan Koneksi tidak tersedia");
            }

            String sql = "SELECT kodepekerjaan, namapekerjaan, jumlahtugas FROM tbpekerjaan LIMIT ?, ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, mulai);
            ps.setInt(2, jumlah);
            rs = ps.executeQuery();

            ArrayList<Object[]> temp = new ArrayList<>();

            while (rs.next()) {
                temp.add(new Object[]{
                    rs.getString("kodepekerjaan"),
                    rs.getString("namapekerjaan"),
                    rs.getInt("jumlahtugas")
                });
            }

            if (!temp.isEmpty()) {
                list = new Object[temp.size()][3];
                for (int i = 0; i < temp.size(); i++) {
                    list[i] = temp.get(i);
                }
            }

        } catch (SQLException ex) {
            error = true;
            pesan = "SQL Error (Baca Data List): " + ex.getMessage();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
            }
        }

        return !error;
    }
}