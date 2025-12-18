package com.unpam.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.unpam.view.PesanDialog;

/**
 *
 * @author FAUZI RAHMAT
 */
public class Karyawan {
    private String ktp, nama, password;
    private int ruang;
    private String pesan;
    private Object[][] list;
    
    // Pastikan class Koneksi dan PesanDialog sudah ada di project
    private final Koneksi koneksi = new Koneksi();
    private final PesanDialog pesanDialog = new PesanDialog(); 
    
    // --- GETTER & SETTER ---
    public String getKtp() { return ktp; }
    public void setKtp(String ktp) { this.ktp = ktp; }
    
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    
    public int getRuang() { return ruang; }
    public void setRuang(int ruang) { this.ruang = ruang; }
    
    public String getPesan() { return pesan; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Object[][] getList() { return list; }
    public void setList(Object[][] list) { this.list = list; }

    // --- METHOD SIMPAN (Untuk Input Data Baru) ---
    public boolean simpan() {
        boolean adaKesalahan = false;
        Connection connection;

        if ((connection = koneksi.getConnection()) != null) {
            int jumlahSimpan = 0;
            String SQLStatemen = "";
            PreparedStatement preparedStatement = null;

            try {
                // Query: INSERT data baru
                SQLStatemen = "insert into tbkaryawan(ktp, nama, ruang, password) values (?,?,?,?)";
                
                preparedStatement = connection.prepareStatement(SQLStatemen);
                preparedStatement.setString(1, ktp);
                preparedStatement.setString(2, nama);
                preparedStatement.setInt(3, ruang);
                preparedStatement.setString(4, password);
                
                jumlahSimpan = preparedStatement.executeUpdate();

                if (jumlahSimpan < 1) {
                    adaKesalahan = true;
                    pesan = "Gagal menyimpan data karyawan";
                }
                
            } catch (SQLException ex) {
                adaKesalahan = true;
                // Asumsi kesalahan karena duplikasi KTP (Primary Key)
                pesan = "KTP sudah ada atau Gagal menyimpan data\n" + ex.getMessage(); 
            } finally {
                try {
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException ex) {}
            }
        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }

        return !adaKesalahan;
    }

    // --- METHOD BACA DATA 1 (Untuk Pagination/Tabel Lihat) ---
    public boolean bacaData(int mulai, int jumlah) {
        boolean adaKesalahan = false;
        Connection connection;
        list = new Object[0][0];

        if ((connection = koneksi.getConnection()) != null) {
            String SQLStatemen = "";
            PreparedStatement preparedStatement = null;
            ResultSet rset = null;

            try {
                // Query untuk menampilkan data dengan batasan limit
                SQLStatemen = "select ktp, nama, ruang from tbkaryawan limit ?, ?";
                preparedStatement = connection.prepareStatement(SQLStatemen);
                preparedStatement.setInt(1, mulai);
                preparedStatement.setInt(2, jumlah);
                
                rset = preparedStatement.executeQuery();
                ArrayList<Object[]> tempList = new ArrayList<>();
                
                while (rset.next()) {
                    Object[] baris = new Object[]{
                        rset.getString("ktp"),
                        rset.getString("nama"),
                        rset.getInt("ruang")
                    };
                    tempList.add(baris);
                }
                
                list = new Object[tempList.size()][3];
                for (int i = 0; i < tempList.size(); i++) {
                    list[i] = tempList.get(i);
                }

            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Tidak dapat membaca data tabel\n" + ex.getMessage();
            } finally {
                try {
                    if (rset != null) rset.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException ex) {}
            }
        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }
        return !adaKesalahan;
    }

    // --- METHOD BACA DATA 2 (Untuk PENCARIAN SATU DATA - Tombol Cari dan Pilih) ---
    // Di Controller dipanggil: karyawan.bacaData(ktp)
    public boolean bacaData(String kode) {
        boolean adaKesalahan = false;
        Connection connection;
        
        // Reset data
        this.ktp = "";
        this.nama = "";
        this.password = "";
        this.ruang = 0;
        
        if ((connection = koneksi.getConnection()) != null) {
            PreparedStatement preparedStatement = null;
            ResultSet rset = null;
            
            try {
                // Query mencari data berdasarkan KTP
                String SQLStatemen = "select * from tbkaryawan where ktp=?";
                preparedStatement = connection.prepareStatement(SQLStatemen);
                preparedStatement.setString(1, kode);
                rset = preparedStatement.executeQuery();
                
                if (rset.next()) {
                    // Jika ketemu, isi variabel
                    this.ktp = rset.getString("ktp");
                    this.nama = rset.getString("nama");
                    this.ruang = rset.getInt("ruang");
                    this.password = rset.getString("password");
                } else {
                    adaKesalahan = true;
                    pesan = "KTP " + kode + " tidak ditemukan";
                }
            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Tidak dapat membaca data karyawan\n" + ex;
            } finally {
                try {
                    if (rset != null) rset.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException ex) {}
            }
        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat melakukan koneksi ke server\n" + koneksi.getPesanKesalahan();
        }
        
        return !adaKesalahan;
    }
    
    // --- METHOD HAPUS DATA ---
    // Diubah untuk menerima parameter 'ktp' agar sesuai dengan pemanggilan di Controller
    // Di Controller dipanggil: karyawan.hapus(ktp)
    public boolean hapus(String ktp) {
        boolean adaKesalahan = false;
        Connection connection;

        if ((connection = koneksi.getConnection()) != null) {
            PreparedStatement preparedStatement = null;
            String SQLStatemen = "";

            try {
                SQLStatemen = "delete from tbkaryawan where ktp=?";
                preparedStatement = connection.prepareStatement(SQLStatemen);
                preparedStatement.setString(1, ktp); // Menggunakan parameter ktp

                int jumlahHapus = preparedStatement.executeUpdate();

                if (jumlahHapus < 1) {
                    adaKesalahan = true;
                    pesan = "Data karyawan tidak ditemukan atau gagal dihapus";
                }

            } catch (SQLException ex) {
                adaKesalahan = true;
                pesan = "Gagal menghapus data\n" + ex.getMessage();
            } finally {
                try {
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException ex) {}
            }
        } else {
            adaKesalahan = true;
            pesan = "Tidak dapat koneksi ke database\n" + koneksi.getPesanKesalahan();
        }

        return !adaKesalahan;
    }
}