/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.unpam.controller;

import com.unpam.model.Karyawan;
import com.unpam.model.Enkripsi;
import com.unpam.view.MainForm;
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
@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(true);
        String userName = "";

        // 1. Cek apakah user sudah login sebelumnya
        try {
            if (session.getAttribute("userName") != null) {
                userName = session.getAttribute("userName").toString();
            }
        } catch (Exception ex) { }

        // 2. Jika User Belum Login (userName kosong atau null)
        if ((userName == null) || userName.equals("")) {
            String userId = request.getParameter("userId");
            String password = request.getParameter("password");

            // --- Form Login HTML ---
            String konten = "<br><form action='LoginController' method='post'>"
                    + "<table>"
                    + "<tr>"
                    + "<td>User ID (KTP)</td><td><input type='text' name='userId'></td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td>Password</td><td><input type='password' name='password'></td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td colspan='2' align='center'><input type='submit' value='Login'></td>"
                    + "</tr>"
                    + "</table>"
                    + "</form>";
            
            String pesan = "";
            
            // --- Logika Proses Login ---
            if (userId == null) {
                // Saat pertama kali halaman dibuka (belum ada aksi submit)
                pesan = ""; 
            } else if (userId.equals("")) {
                pesan = "<br><br><font style='color: red'>User ID harus diisi</font>";
            } else {
                Karyawan karyawan = new Karyawan();
                Enkripsi enkripsi = new Enkripsi();

                // Default pesan jika gagal
                pesan = "<br><br><font style='color: red'>User ID atau password salah</font>";

                // PANGGIL METHOD MODEL YANG SUDAH DIPERBAIKI
                if (karyawan.bacaData(userId)) {
                    String passwordEncrypted = "";

                    try {
                        passwordEncrypted = enkripsi.hashMD5(password);
                    } catch (Exception ex) { }

                    // Cek kecocokan password
                    if (passwordEncrypted.equals(karyawan.getPassword())) {
                        pesan = "";
                        
                        // Set Session User
                        String namaUser = (karyawan.getNama() == null || karyawan.getNama().equals("")) ? "No Name" : karyawan.getNama();
                        session.setAttribute("userName", namaUser);
                        
                        // --- MEMBUAT SIDE MENU (Link Biasa) ---
                        String menu = "<br><b>Master Data</b><br>"
                                + "<a href='KaryawanController'>Karyawan</a><br>"
                                + "<a href='PekerjaanController'>Pekerjaan</a><br><br>"
                                + "<b>Transaksi</b><br>"
                                + "<a href='GajiController'>Gaji</a><br><br>"
                                + "<b>Laporan</b><br>"
                                + "<a href='LaporanGajiController'>Gaji</a><br><br>"
                                + "<a href='LogoutController'>Logout</a><br><br>";
                        session.setAttribute("menu", menu);
                        
                        // --- MEMBUAT TOP MENU (Dropdown CSS) ---
                        String topMenu = "<nav><ul>"
                                + "<li><a href='.'>Home</a></li>"
                                + "<li><a href='#'>Master Data</a>"
                                + "<ul>"
                                + "<li><a href='KaryawanController'>Karyawan</a></li>"
                                + "<li><a href='PekerjaanController'>Pekerjaan</a></li>"
                                + "</ul>"
                                + "</li>"
                                + "<li><a href='#'>Transaksi</a>"
                                + "<ul>"
                                + "<li><a href='GajiController'>Gaji</a></li>"
                                + "</ul>"
                                + "</li>"
                                + "<li><a href='#'>Laporan</a>"
                                + "<ul>"
                                + "<li><a href='LaporanGajiController'>Gaji</a></li>"
                                + "</ul>"
                                + "</li>"
                                + "<li><a href='LogoutController'>Logout</a></li>"
                                + "</ul>"
                                + "</nav>";
                        session.setAttribute("topMenu", topMenu);

                        // Set timeout session (15 menit)
                        session.setMaxInactiveInterval(15 * 60); 
                        
                        // Hilangkan form login karena sudah berhasil
                        konten = ""; 
                        
                        // Refresh halaman agar menu muncul
                         response.sendRedirect("."); 
                         return; 
                    }
                } else {
                    // Jika User ID tidak ditemukan atau Error Database
                    String pesanErrorDB = karyawan.getPesan();
                    if (pesanErrorDB != null && pesanErrorDB.length() >= 3) {
                         // Hanya tampilkan pesan teknis jika bukan sekedar "User tidak ditemukan"
                         if (!pesanErrorDB.substring(0, 3).equals("User")) {
                             pesan = "<br><br><font style='color: red'>" + pesanErrorDB.replace("\n", "<br>") + "</font>";
                         }
                    }
                }
            }
            
            // Tampilkan (Form + Pesan Error)
            new MainForm().tampilkan(konten + pesan, request, response);
            
        } else {
            // 3. Jika User Sudah Login, redirect ke halaman Home
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
