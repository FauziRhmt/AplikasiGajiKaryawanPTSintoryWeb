package com.unpam.controller;

import com.unpam.model.Pekerjaan;
import com.unpam.view.MainForm;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "PekerjaanController", urlPatterns = {"/PekerjaanController"})
public class PekerjaanController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(true);

        // 🔐 Cek login
        String userName = (String) session.getAttribute("userName");
        if (userName == null || userName.equals("")) {
            response.sendRedirect(".");
            return;
        }

        Pekerjaan pekerjaan = new Pekerjaan();

        // Ambil parameter
        String tombol = getParam(request, "tombol");
        String kodePekerjaan = getParam(request, "kodePekerjaan");
        String namaPekerjaan = getParam(request, "namaPekerjaan");
        String jumlahTugas = getParam(request, "jumlahTugas", "2");
        String kodeDipilih = getParam(request, "kodePekerjaanDipilih");

        int mulai = parseInt(request.getParameter("mulai"), 0);
        int jumlah = parseInt(request.getParameter("jumlah"), 10);

        String keterangan = "<br>";

        // ================== PROSES TOMBOL ==================
        switch (tombol) {

            case "Simpan":
                if (!kodePekerjaan.equals("")) {
                    pekerjaan.setKodePekerjaan(kodePekerjaan);
                    pekerjaan.setNamaPekerjaan(namaPekerjaan);
                    pekerjaan.setJumlahTugas(Integer.parseInt(jumlahTugas));

                    if (pekerjaan.simpan()) {
                        kodePekerjaan = "";
                        namaPekerjaan = "";
                        jumlahTugas = "2";
                        keterangan = "✅ Data berhasil disimpan";
                    } else {
                        keterangan = "❌ " + pekerjaan.getPesan();
                    }
                } else {
                    keterangan = "❌ Kode pekerjaan harus diisi";
                }
                break;

            case "Hapus":
                if (!kodePekerjaan.equals("")) {
                    if (pekerjaan.hapus(kodePekerjaan)) {
                        kodePekerjaan = "";
                        namaPekerjaan = "";
                        jumlahTugas = "2";
                        keterangan = "✅ Data berhasil dihapus";
                    } else {
                        keterangan = "❌ " + pekerjaan.getPesan();
                    }
                } else {
                    keterangan = "❌ Kode pekerjaan masih kosong";
                }
                break;

            case "Cari":
                if (!kodePekerjaan.equals("")) {
                    if (pekerjaan.baca(kodePekerjaan)) {
                        kodePekerjaan = pekerjaan.getKodePekerjaan();
                        namaPekerjaan = pekerjaan.getNamaPekerjaan();
                        jumlahTugas = String.valueOf(pekerjaan.getJumlahTugas());
                        keterangan = "<br>";
                    } else {
                        keterangan = "❌ " + pekerjaan.getPesan();
                    }
                } else {
                    keterangan = "❌ Masukkan kode pekerjaan";
                }
                break;

            case "Pilih":
                if (!kodeDipilih.equals("") && pekerjaan.baca(kodeDipilih)) {
                    kodePekerjaan = pekerjaan.getKodePekerjaan();
                    namaPekerjaan = pekerjaan.getNamaPekerjaan();
                    jumlahTugas = String.valueOf(pekerjaan.getJumlahTugas());
                }
                break;

            case "Sebelumnya":
                mulai = Math.max(0, mulai - jumlah);
                break;

            case "Berikutnya":
                mulai += jumlah;
                break;
        }

        // ================== DATA LIST ==================
        String kontenLihat = "";
        if (tombol.equals("Lihat") || tombol.equals("Sebelumnya") || 
            tombol.equals("Berikutnya") || tombol.equals("Tampilkan")) {

            pekerjaan.bacaData(mulai, jumlah);
            Object[][] list = pekerjaan.getList();

            kontenLihat += "<table border='1' cellpadding='5'>";
            for (int i = 0; i < list.length; i++) {
                kontenLihat += "<tr>";
                kontenLihat += "<td><input type='radio' name='kodePekerjaanDipilih' value='" + list[i][0] + "'></td>";
                kontenLihat += "<td>" + list[i][0] + "</td>";
                kontenLihat += "<td>" + list[i][1] + "</td>";
                kontenLihat += "</tr>";
            }
            kontenLihat += "</table>";
        }

        // ================== FORM ==================
        String konten = "<h2>Master Data Pekerjaan</h2>"
                + "<form method='post'>"
                + "Kode Pekerjaan <input type='text' name='kodePekerjaan' value='" + kodePekerjaan + "'><br><br>"
                + "Nama Pekerjaan <input type='text' name='namaPekerjaan' value='" + namaPekerjaan + "'><br><br>"
                + "Jumlah Tugas <select name='jumlahTugas'>";

        for (int i = 2; i <= 6; i++) {
            konten += "<option " + (i == Integer.parseInt(jumlahTugas) ? "selected" : "") +
                    " value='" + i + "'>" + i + "</option>";
        }

        konten += "</select><br><br>"
                + "<b>" + keterangan + "</b><br><br>"
                + "<input type='submit' name='tombol' value='Simpan'> "
                + "<input type='submit' name='tombol' value='Hapus'> "
                + "<input type='submit' name='tombol' value='Cari'> "
                + "<input type='submit' name='tombol' value='Lihat'><br><br>"
                + kontenLihat
                + "</form>";

        new MainForm().tampilkan(konten, request, response);
    }

    // ================== HELPER ==================
    private String getParam(HttpServletRequest req, String name) {
        return getParam(req, name, "");
    }

    private String getParam(HttpServletRequest req, String name, String def) {
        String val = req.getParameter(name);
        return val == null ? def : val;
    }

    private int parseInt(String val, int def) {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            return def;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processRequest(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processRequest(req, res);
    }
}
