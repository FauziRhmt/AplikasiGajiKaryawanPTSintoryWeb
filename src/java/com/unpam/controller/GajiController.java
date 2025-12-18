package com.unpam.controller;

import com.unpam.model.Gaji;
import com.unpam.model.Karyawan;
import com.unpam.model.Pekerjaan;
import com.unpam.view.MainForm;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "GajiController", urlPatterns = {"/GajiController"})
public class GajiController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(true);

        // ================= OBJECT MODEL =================
        Karyawan karyawan = new Karyawan();
        Pekerjaan pekerjaan = new Pekerjaan();
        Gaji gaji = new Gaji();

        // ================= PARAMETER =================
        String tombol = getParam(request, "tombol");
        String tombolKaryawan = getParam(request, "tombolKaryawan");
        String tombolPekerjaan = getParam(request, "tombolPekerjaan");

        String ktp = getParam(request, "ktp");
        String ktpDipilih = getParam(request, "ktpDipilih");

        String kodePekerjaan = getParam(request, "kodePekerjaan");
        String kodePekerjaanDipilih = getParam(request, "kodePekerjaanDipilih");

        String namaKaryawan = "";
        String ruang = "";
        String namaPekerjaan = "";
        String jumlahTugas = "";

        String gajibersih = getParam(request, "gajibersih");
        String gajikotor = getParam(request, "gajikotor");
        String tunjangan = getParam(request, "tunjangan");

        int mulai = parseInt(request.getParameter("mulai"), 0);
        int jumlah = parseInt(request.getParameter("jumlah"), 10);

        String keterangan = "<br>";

        // ================= CEK LOGIN =================
        String userName = "";
        try {
            userName = session.getAttribute("userName").toString();
        } catch (Exception e) {
        }

        if (userName.equals("")) {
            response.sendRedirect("LoginController");
            return;
        }

        // ================= KARYAWAN =================
        if (tombolKaryawan.equals("Cari")) {
            if (!ktp.equals("") && karyawan.bacaData(ktp)) {
                namaKaryawan = karyawan.getNama();
                ruang = String.valueOf(karyawan.getRuang());
            } else {
                keterangan = "KTP tidak ditemukan";
            }
        }

        if (tombolKaryawan.equals("Pilih") && !ktpDipilih.equals("")) {
            if (karyawan.bacaData(ktpDipilih)) {
                ktp = karyawan.getKtp();
                namaKaryawan = karyawan.getNama();
                ruang = String.valueOf(karyawan.getRuang());
            }
        }

        // ================= PEKERJAAN =================
        if (tombolPekerjaan.equals("Cari")) {
            if (!kodePekerjaan.equals("") && pekerjaan.baca(kodePekerjaan)) {
                namaPekerjaan = pekerjaan.getNamaPekerjaan();
                jumlahTugas = String.valueOf(pekerjaan.getJumlahTugas());
            } else {
                keterangan = "Kode pekerjaan tidak ditemukan";
            }
        }

        if (tombolPekerjaan.equals("Pilih") && !kodePekerjaanDipilih.equals("")) {
            if (pekerjaan.baca(kodePekerjaanDipilih)) {
                kodePekerjaan = pekerjaan.getKodePekerjaan();
                namaPekerjaan = pekerjaan.getNamaPekerjaan();
                jumlahTugas = String.valueOf(pekerjaan.getJumlahTugas());
            }
        }

        // ================= SIMPAN / HAPUS =================
        if (tombol.equals("Simpan")) {
            if (!ktp.equals("") && !kodePekerjaan.equals("")) {
                gaji.setKtp(ktp);
                gaji.setListGaji(new Object[][]{
                    {kodePekerjaan, gajibersih, gajikotor, tunjangan}
                });
                if (gaji.simpan()) {
                    keterangan = "Data gaji berhasil disimpan";
                } else {
                    keterangan = gaji.getPesan();
                }
            }
        }

        if (tombol.equals("Hapus")) {
            if (gaji.hapus(ktp, kodePekerjaan)) {
                keterangan = "Data gaji berhasil dihapus";
            } else {
                keterangan = gaji.getPesan();
            }
        }
        
        

        // ================= TAMPILAN =================
        String konten = "<h2>Input Gaji Karyawan</h2>"
                + "<form method='post' action='GajiController'>"
                + "KTP <input type='text' name='ktp' value='" + ktp + "'>"
                + "<input type='submit' name='tombolKaryawan' value='Cari'><br>"
                + "Nama <input readonly value='" + namaKaryawan + "'><br>"
                + "Ruang <input readonly value='" + ruang + "'><br><hr>"
                + "Kode Pekerjaan <input type='text' name='kodePekerjaan' value='" + kodePekerjaan + "'>"
                + "<input type='submit' name='tombolPekerjaan' value='Cari'><br>"
                + "Nama Pekerjaan <input readonly value='" + namaPekerjaan + "'><br>"
                + "Jumlah Tugas <input readonly value='" + jumlahTugas + "'><br><hr>"
                + "Gaji Bersih <input name='gajibersih' value='" + gajibersih + "'><br>"
                + "Gaji Kotor <input name='gajikotor' value='" + gajikotor + "'><br>"
                + "Tunjangan <input name='tunjangan' value='" + tunjangan + "'><br><br>"
                + "<input type='submit' name='tombol' value='Simpan'> "
                + "<input type='submit' name='tombol' value='Hapus'>"
                + "<br><b>" + keterangan + "</b>"
                + "</form>";

        new MainForm().tampilkan(konten, request, response);
    }

    private String getParam(HttpServletRequest r, String nama) {
        String v = r.getParameter(nama);
        return v == null ? "" : v;
    }

    private int parseInt(String v, int def) {
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            return def;
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
}
