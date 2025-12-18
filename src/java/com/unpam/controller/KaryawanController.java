package com.unpam.controller;

import com.unpam.model.Karyawan;
import com.unpam.view.MainForm; 
import com.unpam.model.Enkripsi; 

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author FAUZI RAHMAT
 */
@WebServlet(name = "KaryawanController", urlPatterns = {"/KaryawanController"})
public class KaryawanController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(true);
        Karyawan karyawan = new Karyawan();
        Enkripsi enkripsi = new Enkripsi(); 
        String userName = "";

        // Ambil parameter dari form
        String tombol = request.getParameter("tombol");
        String ktp = request.getParameter("ktp");
        String nama = request.getParameter("nama");
        String ruang = request.getParameter("ruang");
        String password = request.getParameter("password");
        String mulaiParameter = request.getParameter("mulai");
        String jumlahParameter = request.getParameter("jumlah");
        String ktpDipilih = request.getParameter("ktpDipilih");

        // Null Safety check
        if (tombol == null) tombol = "";
        if (ktp == null) ktp = "";
        if (nama == null) nama = "";
        if (ruang == null) ruang = "1";
        if (password == null) password = "";
        if (ktpDipilih == null) ktpDipilih = "";

        int mulai = 0, jumlah = 10;

        try {
            mulai = Integer.parseInt(mulaiParameter);
        } catch (NumberFormatException ex) { }

        try {
            jumlah = Integer.parseInt(jumlahParameter);
        } catch (NumberFormatException ex) { }

        String keterangan = "<br>";

        // Cek Session Login
        try {
            userName = session.getAttribute("userName").toString();
        } catch (Exception ex) { }

        // Jika User sudah login (userName tidak kosong)
        if (!((userName == null) || userName.equals(""))) {

            // --- LOGIKA TOMBOL ---
            if (tombol.equals("Simpan")) {
                if (!ktp.equals("")) {
                    karyawan.setKtp(ktp);
                    karyawan.setNama(nama);
                    karyawan.setRuang(Integer.parseInt(ruang));
                    
                    String passwordEcrypted = "";
                    try {
                        passwordEcrypted = enkripsi.hashMD5(password);
                    } catch (Exception ex) {}
                    karyawan.setPassword(passwordEcrypted);

                    if (karyawan.simpan()) {
                        ktp = "";
                        nama = "";
                        ruang = "1";
                        password = "";
                        keterangan = "Sudah tersimpan";
                    } else {
                        keterangan = karyawan.getPesan();
                    }
                } else {
                    keterangan = "KTP tidak boleh kosong";
                }
                
            } else if (tombol.equals("Hapus")) {
                if (!ktp.equals("")) {
                    // Panggil method hapus yang sudah menerima parameter ktp
                    // Panggil method hapus dengan parameter ktp
                    if (karyawan.hapus(ktp)) { 
                        ktp = "";
                        nama = "";
                        ruang = "1";
                        password = "";
                        keterangan = "Data sudah dihapus";
                    } else {
                        keterangan = karyawan.getPesan();
                    }
                    
                    // Baris ini (Fitur Hapus belum diimplementasikan di Model) DIHAPUS
                    
                } else {
                    keterangan = "KTP masih kosong";
                }
                
            } else if (tombol.equals("Cari")) {
                if (!ktp.equals("")) {
                    // Panggil method bacaData(ktp) yang ada di Model
                    if (karyawan.bacaData(ktp)) { 
                        ktp = karyawan.getKtp();
                        nama = karyawan.getNama();
                        ruang = Integer.toString(karyawan.getRuang());
                        password = karyawan.getPassword();
                        keterangan = "<br>";
                    } else {
                        nama = "";
                        ruang = "1";
                        password = "";
                        keterangan = "KTP " + ktp + " tidak ada";
                    }
                    
                    // Baris ini (Fitur Cari belum diimplementasikan di Model) DIHAPUS
                    
                } else {
                    keterangan = "KTP harus diisi";
                }
                
            } else if (tombol.equals("Pilih")) {
                ktp = ktpDipilih;
                nama = "";
                ruang = "1";
                if (!ktpDipilih.equals("")) {
                    // Panggil method bacaData(ktp)
                    if (karyawan.bacaData(ktpDipilih)) { 
                        ktp = karyawan.getKtp();
                        nama = karyawan.getNama();
                        ruang = Integer.toString(karyawan.getRuang());
                        password = karyawan.getPassword();
                        keterangan = "<br>";
                    } else {
                        keterangan = "Tidak ada yang dipilih";
                    }
                }
            }

            // --- LOGIKA TABLE (LIHAT DATA) ---
            String kontenLihat = "";
            if (tombol.equals("Lihat") || tombol.equals("Sebelumnya") || tombol.equals("Berikutnya") || tombol.equals("Tampilkan") || tombol.equals("Pilih")) { // Tambahkan Pilih agar tabel tetap tampil
                kontenLihat = "<tr>";
                kontenLihat += "<td colspan='2' align='center'>";
                kontenLihat += "<table>";

                if (tombol.equals("Sebelumnya")) {
                    mulai -= jumlah;
                    if (mulai < 0) mulai = 0;
                }
                if (tombol.equals("Berikutnya")) {
                    mulai += jumlah;
                }

                Object[][] listKaryawan = null;
                if (karyawan.bacaData(mulai, jumlah)) { // Panggil method bacaData(int, int)
                    listKaryawan = karyawan.getList();
                } else {
                    keterangan = karyawan.getPesan();
                }

                if (listKaryawan != null) {
                    for (int i = 0; i < listKaryawan.length; i++) {
                        kontenLihat += "<tr>";
                        kontenLihat += "<td>";
                        if (i == 0) {
                            kontenLihat += "<input type='radio' checked name='ktpDipilih' value='" + listKaryawan[i][0].toString() + "'>";
                        } else {
                            kontenLihat += "<input type='radio' name='ktpDipilih' value='" + listKaryawan[i][0].toString() + "'>";
                        }
                        kontenLihat += "</td>";
                        kontenLihat += "<td>" + listKaryawan[i][0].toString() + "</td>"; // KTP
                        kontenLihat += "<td>" + listKaryawan[i][1].toString() + "</td>"; // Nama
                        kontenLihat += "</tr>";
                    }
                }

                kontenLihat += "</table>";
                kontenLihat += "</td>";
                kontenLihat += "</tr>";
                
                // Navigasi Paginasi
                kontenLihat += "<tr>";
                kontenLihat += "<td colspan='2' align='center'>";
                kontenLihat += "<table>";
                kontenLihat += "<tr>";
                kontenLihat += "<td align='center'><input type='submit' name='tombol' value='Sebelumnya' style='width: 100px'></td>";
                kontenLihat += "<td align='center'><input type='submit' name='tombol' value='Pilih' style='width: 60px'></td>";
                kontenLihat += "<td align='center'><input type='submit' name='tombol' value='Berikutnya' style='width: 100px'></td>";
                kontenLihat += "</tr>";
                kontenLihat += "<tr>";
                kontenLihat += "<td align='center'>Mulai <input type='text' name='mulai' value=" + mulai + " style='width: 40px'></td>";
                kontenLihat += "<td>Jumlah ";
                kontenLihat += "<select name='jumlah'>";
                for (int i = 1; i <= 10; i++) {
                    if (jumlah == (i * 10)) {
                        kontenLihat += "<option selected value=" + i * 10 + ">" + i * 10 + "</option>";
                    } else {
                        kontenLihat += "<option value=" + i * 10 + ">" + i * 10 + "</option>";
                    }
                }
                kontenLihat += "</select>";
                kontenLihat += "</td>";
                kontenLihat += "<td align='center'><input type='submit' name='tombol' value='Tampilkan' style='width: 90px'></td>";
                kontenLihat += "</tr>";
                kontenLihat += "</table>";
                kontenLihat += "</td>";
                kontenLihat += "</tr>";
            }

            // --- MEMBANGUN FORM HTML ---
            String konten = "<h2>Master Data Karyawan</h2>"
                    + "<form action='KaryawanController' method='post'>"
                    + "<table>"
                    + "<tr>"
                    + "<td align='right'>KTP</td>"
                    + "<td align='left'><input type='text' value='" + ktp + "' name='ktp' maxlength='15' size='15'><input type='submit' name='tombol' value='Cari'></td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td align='right'>Nama</td>"
                    + "<td align='left'><input type='text' value='" + nama + "' name='nama' maxlength='30' size='30'></td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td align='right'>Ruang</td>"
                    + "<td align='left'>"
                    + "<select name='ruang'>";
            
            // Loop Ruangan
            for(int r=1; r<=5; r++){
                 String selected = (Integer.parseInt(ruang) == r) ? "selected" : "";
                 konten += "<option value='"+r+"' "+selected+">Ruang "+r+"</option>";
            }

            konten += "</select>"
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td align='right'>Password</td>"
                    + "<td align='left'><input type='password' value='' name='password' maxlength='30' size='30'></td>" // Kosongkan field password untuk keamanan
                    + "</tr>"
                    + "<tr>"
                    + "<td colspan='2'><b>" + keterangan.replaceAll("\n", "<br>").replaceAll(";", ",") + "</b></td>" 
                    + "</tr>"
                    + "<tr>"
                    + "<td colspan='2' align='center'>"
                    + "<table>"
                    + "<tr>"
                    + "<td align='center'><input type='submit' name='tombol' value='Simpan' style='width: 100px'></td>"
                    + "<td align='center'><input type='submit' name='tombol' value='Hapus' style='width: 100px'></td>"
                    + "<td align='center'><input type='submit' name='tombol' value='Lihat' style='width: 100px'></td>"
                    + "</tr>"
                    + "</table>"
                    + "</td>"
                    + "</tr>"
                    + kontenLihat
                    + "</table>"
                    + "</form>";

            new MainForm().tampilkan(konten, request, response);

        } else {
            response.sendRedirect(".");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}