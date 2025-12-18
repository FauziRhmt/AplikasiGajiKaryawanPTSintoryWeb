/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.unpam.controller;

import com.unpam.model.Gaji;
import com.unpam.view.MainForm;
import java.io.IOException;
import java.io.OutputStream; // Import ini ditambahkan
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
@WebServlet(name = "LaporanGajiController", urlPatterns = {"/LaporanGajiController"})
public class LaporanGajiController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // PERBAIKAN: Typo pada ekstensi xlsx dan xls (sebelumnya x1sx dan x1s)
        String[][] formatTypeData = {
            {"PDF (Portable Document Format)", "pdf", "application/pdf"},
            {"XLSX (Microsoft Excel)", "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {"XLS (Microsoft Excel 97-2003)", "xls", "application/vnd.ms-excel"},
            {"DOCX (Microsoft Word)", "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {"ODT (OpenDocument Text)", "odt", "application/vnd.oasis.opendocument.text"},
            {"RTF (Rich Text Format)", "rtf", "text/rtf"}
        };

        HttpSession session = request.getSession(true);
        String userName = "";

        String tombol = request.getParameter("tombol");
        String opsi = request.getParameter("opsi");
        String ktp = request.getParameter("ktp");
        String ruang = request.getParameter("ruang");
        // PERBAIKAN: Typo nama parameter (sebelumnya formatIype)
        String formatType = request.getParameter("formatType");

        if (tombol == null) tombol = "";
        if (ktp == null) ktp = "";
        if (opsi == null) opsi = "";
        if (ruang == null) ruang = "0";
        
        // PERBAIKAN: Set default formatType jika null agar tidak error saat loop
        if (formatType == null) formatType = formatTypeData[0][0];

        String keterangan = "<br>";
        int noType = 0;

        for (int i = 0; i < formatTypeData.length; i++) {
            if (formatTypeData[i][0].equals(formatType)) {
                noType = i;
                break;
            }
        }

        try {
            if (session.getAttribute("userName") != null) {
                userName = session.getAttribute("userName").toString();
            }
        } catch (Exception ex) {}

        if (!(userName == null || userName.equals(""))) {
            boolean opsiSelected = false;

            if (tombol.equals("Cetak")) {
                Gaji gaji = new Gaji(); // Asumsi class Gaji ada

                int ruangDipilih = 0;
                try {
                    ruangDipilih = Integer.parseInt(ruang);
                } catch (NumberFormatException ex) {}

                // Generates the report. 
                // Arguments: options, ID (ktp), room selected, file format, and the physical path to the .jasper file
                boolean isSuccess = gaji.cetakLaporan(opsi, ktp, ruangDipilih, formatTypeData[noType][1], getServletConfig().getServletContext().getRealPath("reports/report1.jasper"));
                
                if (isSuccess) {
                    byte[] pdfasbytes = gaji.getPdfasbytes();

                    try (OutputStream outStream = response.getOutputStream()) {
                        // Sets headers so the browser knows to display or download the file
                        response.setHeader("Content-Disposition", "inline; filename=GajiReport." + formatTypeData[noType][1]);
                        response.setContentType(formatTypeData[noType][2]);

                        response.setContentLength(pdfasbytes.length);
                        outStream.write(pdfasbytes, 0, pdfasbytes.length);

                        outStream.flush();
                        // PERBAIKAN KRUSIAL: Return setelah mengirim file agar HTML di bawah tidak ikut tertulis ke dalam file
                        return; 
                    }
                } else {
                    keterangan += gaji.getPesan();
                }
            }
            
            // --- Bagian HTML Form ---
            String konten = "<h2>Mencetak Gaji</h2>";

            konten += "<form action='LaporanGajiController' method='post'>";
            konten += "<table>";
            konten += "<tr>";

            if (opsi.equalsIgnoreCase("KTP")) {
                konten += "<td align='right'><input type='radio' checked name='opsi' value='KTP'></td>";
                opsiSelected = true;
            } else {
                konten += "<td align='right'><input type='radio' name='opsi' value='KTP'></td>";
            }

            konten += "<td align='left'>KTP</td>";
            konten += "<td align='left'><input type='text' value='" + ktp + "' name='ktp' maxlength='15' size='15'></td>";
            konten += "</tr>";

            konten += "<tr>";
            if (opsi.equals("ruang")) {
                konten += "<td align='right'><input type='radio' checked name='opsi' value='ruang'></td>";
                opsiSelected = true;
            } else {
                konten += "<td align='right'><input type='radio' name='opsi' value='ruang'></td>";
            }

            konten += "<td align='left'>Ruang</td>";
            konten += "<td align='left'>";
            konten += "<select name='ruang'>";
            konten += "<option selected value=0>Semua</option>";

            for (int i = 1; i <= 14; i++) {
                if (i == Integer.parseInt(ruang)) {
                    konten += "<option selected value='" + i + "'>" + i + "</option>";
                } else {
                    konten += "<option value='" + i + "'>" + i + "</option>";
                }
            }
            konten += "</select>";
            konten += "</td>";
            konten += "</tr>";

            konten += "<tr>";
            // --- Option 3: Select All (Default) ---
            if (!opsiSelected) {
                konten += "<td align='right'><input type='radio' checked name='opsi' value='Semua'></td>";
            } else {
                konten += "<td align='right'><input type='radio' name='opsi' value='Semua'></td>";
            }

            konten += "<td align='left'>Semua</td>";
            konten += "<td><br></td>";
            konten += "</tr>";

            konten += "<tr>";
            konten += "<td colspan='3'><br></td>";
            konten += "</tr>";

            // --- Report Format Selector ---
            konten += "<tr>";
            konten += "<td>Format Laporan</td>";
            konten += "<td colspan=2>";
            konten += "<select name='formatType'>";

            for (String[] formatLaporan : formatTypeData) {
                // Pastikan formatType tidak null (sudah dihandle diatas)
                if (formatLaporan[0].equals(formatType)) {
                    konten += "<option selected value='" + formatLaporan[0] + "'>" + formatLaporan[0] + "</option>";
                } else {
                    konten += "<option value='" + formatLaporan[0] + "'>" + formatLaporan[0] + "</option>";
                }
            }
            konten += "</select>";
            konten += "</td>"; // Typo fixed: sebelumnya </td)
            konten += "</tr>";

            konten += "<tr>";
            konten += "<td colspan='3'><b>" + keterangan.replaceAll("\n", "<br>").replaceAll(";", ",") + "</b></td>";
            konten += "</tr>";

            // PERBAIKAN: Memperbaiki tag HTML tombol submit yang rusak parah
            konten += "<tr>";
            konten += "<td colspan='3' align='center'><input type='submit' name='tombol' value='Cetak' style='width: 100px'></td>";
            konten += "</tr>";

            konten += "</table>";
            konten += "</form>";

            new MainForm().tampilkan(konten, request, response);
        } else {
            response.sendRedirect(".");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}