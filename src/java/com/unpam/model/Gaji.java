/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unpam.model;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// Import JasperReports yang sudah diperbaiki (Spasi dihapus)
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

/**
 *
 * @author faiza
 */
public class Gaji {

    private byte[] pdfasbytes;
    private String ktp;
    private Object[][] listGaji;
    private Object[][] list;
    private String pesan;

    // Pastikan class Koneksi.java sudah ada di package com.unpam.model
    private final Koneksi koneksi = new Koneksi(); 

    // ================= GETTER & SETTER =================
    public byte[] getPdfasbytes() {
        return pdfasbytes;
    }

    public String getPesan() {
        return pesan;
    }
    
    public String getKtp() {
        return ktp;
    }

    public void setKtp(String ktp) {
        this.ktp = ktp;
    }

    public Object[][] getListGaji() {
        return listGaji;
    }

    public void setListGaji(Object[][] listGaji) {
        this.listGaji = listGaji;
    }

    public Object[][] getList() {
        return list;
    }

    // ================= METHOD CETAK LAPORAN (BARU) =================
    // Method ini yang dipanggil oleh LaporanGajiController
    public boolean cetakLaporan(String opsi, String ktp, int ruang, String formatType, String jasperPath) {
        Connection con = koneksi.getConnection();

        if (con == null) {
            pesan = "Koneksi database gagal";
            return false;
        }

        try {
            // 1. Siapkan Parameter untuk dikirim ke file .jasper
            // Pastikan di file Jasper Report Anda ($P{ktp} dan $P{ruang}) sudah dibuat
            Map<String, Object> parameters = new HashMap<>();
            
            if ("KTP".equalsIgnoreCase(opsi)) {
                parameters.put("ktp", ktp);
            } else {
                parameters.put("ktp", null);
            }

            if ("ruang".equalsIgnoreCase(opsi)) {
                parameters.put("ruang", ruang);
            } else {
                parameters.put("ruang", 0);
            }

            // 2. Fill Report (Gabungkan template jasper dengan data database)
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, con);

            // 3. Export sesuai formatType yang dipilih user
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JRExporter exporter = null;

            if ("pdf".equalsIgnoreCase(formatType)) {
                // PDF punya handler khusus yang lebih simpel
                pdfasbytes = JasperExportManager.exportReportToPdf(jasperPrint);
                return true;
            } 
            
            // Switch case untuk format selain PDF
            switch (formatType.toLowerCase()) {
                case "xlsx":
                    exporter = new JRXlsxExporter();
                    break;
                case "xls":
                    exporter = new JRXlsExporter();
                    break;
                case "docx":
                    exporter = new JRDocxExporter();
                    break;
                case "odt":
                    exporter = new JROdtExporter();
                    break;
                case "rtf":
                    exporter = new JRRtfExporter();
                    break;
                default:
                    // Jika format tidak dikenali, default ke PDF
                    pdfasbytes = JasperExportManager.exportReportToPdf(jasperPrint);
                    return true;
            }

            // Eksekusi export untuk format non-PDF
            if (exporter != null) {
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
                exporter.exportReport();
                pdfasbytes = out.toByteArray();
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            pesan = "Gagal mencetak laporan: " + e.getMessage();
            return false;
        }
    }

    // ================= SIMPAN GAJI =================
    public boolean simpan() {
        boolean sukses = true;
        Connection con = koneksi.getConnection();

        if (con == null) {
            pesan = "Koneksi database gagal";
            return false;
        }

        try {
            String sql = "INSERT INTO tbgaji "
                    + "(ktp, kodepekerjaan, gajibersih, gajikotor, tunjangan) "
                    + "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            for (Object[] g : listGaji) {
                ps.setString(1, ktp);
                ps.setString(2, g[0].toString());
                ps.setDouble(3, Double.parseDouble(g[1].toString()));
                ps.setDouble(4, Double.parseDouble(g[2].toString()));
                ps.setDouble(5, Double.parseDouble(g[3].toString()));
                ps.executeUpdate();
            }

            ps.close();
        } catch (SQLException | NumberFormatException e) {
            sukses = false;
            pesan = "Gagal menyimpan data gaji\n" + e.getMessage();
        }

        return sukses;
    }

    // ================= HAPUS GAJI =================
    public boolean hapus(String ktp, String kodePekerjaan) {
        boolean sukses = true;
        Connection con = koneksi.getConnection();

        if (con == null) {
            pesan = "Koneksi database gagal";
            return false;
        }

        try {
            String sql = "DELETE FROM tbgaji WHERE ktp=? AND kodepekerjaan=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, ktp);
            ps.setString(2, kodePekerjaan);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            sukses = false;
            pesan = "Gagal menghapus data gaji\n" + e.getMessage();
        }

        return sukses;
    }

    // ================= BACA DATA GAJI =================
    public boolean bacaData(int mulai, int jumlah) {
        Connection con = koneksi.getConnection();

        if (con == null) {
            pesan = "Koneksi database gagal";
            return false;
        }

        try {
            String sql = "SELECT ktp, kodepekerjaan, gajibersih, gajikotor, tunjangan "
                    + "FROM tbgaji LIMIT ?, ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, mulai);
            ps.setInt(2, jumlah);

            ResultSet rs = ps.executeQuery();

            int i = 0;
            list = new Object[100][5]; // max sementara

            while (rs.next()) {
                list[i][0] = rs.getString("ktp");
                list[i][1] = rs.getString("kodepekerjaan");
                list[i][2] = rs.getDouble("gajibersih");
                list[i][3] = rs.getDouble("gajikotor");
                list[i][4] = rs.getDouble("tunjangan");
                i++;
            }

            // rapikan ukuran array
            Object[][] tmp = new Object[i][5];
            System.arraycopy(list, 0, tmp, 0, i);
            list = tmp;

            rs.close();
            ps.close();

        } catch (SQLException e) {
            pesan = "Gagal membaca data gaji\n" + e.getMessage();
            return false;
        }

        return true;
    }
}