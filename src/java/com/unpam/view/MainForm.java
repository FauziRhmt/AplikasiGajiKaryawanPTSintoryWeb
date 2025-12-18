package com.unpam.view;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class MainForm {

    public void tampilkan(String konten,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        /* =========================
           MENU KIRI
           ========================= */
        String menu =
              "<br><b>Master Data</b><br>"
            + "<a href='KaryawanController?aksi=list'>Karyawan</a><br>"
            + "<a href='PekerjaanController?aksi=list'>Pekerjaan</a><br><br>"
            + "<b>Transaksi</b><br>"
            + "<a href='GajiController?aksi=input'>Gaji</a><br><br>"
            + "<b>Laporan</b><br>"
            + "<a href='GajiController?aksi=laporan'>Laporan Gaji</a><br><br>"
            + "<a href='LogoutController'>Logout</a><br><br>";

        /* =========================
           TOP MENU
           ========================= */
        String topMenu =
            "<nav>"
          + "<ul>"
          + "<li><a href='HomeController'>Home</a></li>"
          + "<li><a href='#'>Master Data</a>"
          +   "<ul>"
          +     "<li><a href='KaryawanController?aksi=list'>Karyawan</a></li>"
          +     "<li><a href='PekerjaanController?aksi=list'>Pekerjaan</a></li>"
          +   "</ul>"
          + "</li>"
          + "<li><a href='#'>Transaksi</a>"
          +   "<ul>"
          +     "<li><a href='GajiController?aksi=input'>Gaji</a></li>"
          +   "</ul>"
          + "</li>"
          + "<li><a href='#'>Laporan</a>"
          +   "<ul>"
          +     "<li><a href='GajiController?aksi=laporan'>Laporan Gaji</a></li>"
          +   "</ul>"
          + "</li>"
          + "<li><a href='LogoutController'>Logout</a></li>"
          + "</ul>"
          + "</nav>";

        /* =========================
           SESSION OVERRIDE (jika ada)
           ========================= */
        if (session.getAttribute("menu") != null) {
            menu = session.getAttribute("menu").toString();
        }
        if (session.getAttribute("topMenu") != null) {
            topMenu = session.getAttribute("topMenu").toString();
        }

        /* =========================
           OUTPUT HTML
           ========================= */
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Informasi Gaji Karyawan</title>");
        out.println("<link rel='stylesheet' href='style.css'>");
        out.println("<style>");
        out.println("body { background:#808080; font-family: Arial; }");
        out.println("table { border-collapse: collapse; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<center>");
        out.println("<table width='80%' bgcolor='#eeeeee'>");

        /* HEADER */
        out.println("<tr>");
        out.println("<td colspan='2' align='center'>");
        out.println("<h2>Informasi Gaji Karyawan</h2>");
        out.println("<h1>PT Sintory</h1>");
        out.println("<h4>Jl. Surya Kencana No. 99 Pamulang, Tangerang Selatan, Banten</h4>");
        out.println("</td>");
        out.println("</tr>");

        /* BODY */
        out.println("<tr height='400'>");

        // MENU KIRI
        out.println("<td width='200' valign='top' bgcolor='#eeffee'>");
        out.println("<div id='menu'>");
        out.println(menu);
        out.println("</div>");
        out.println("</td>");

        // KONTEN UTAMA
        out.println("<td valign='top' bgcolor='#ffffff'>");
        out.println(topMenu);
        out.println("<hr>");
        out.println(konten);
        out.println("</td>");

        out.println("</tr>");

        /* FOOTER */
        out.println("<tr>");
        out.println("<td colspan='2' align='center' bgcolor='#eeeeff'>");
        out.println("<small>");
        out.println("&copy; 2017 PT Sintory<br>");
        out.println("Jl. Surya Kencana No. 99 Pamulang, Tangerang Selatan, Banten");
        out.println("</small>");
        out.println("</td>");
        out.println("</tr>");

        out.println("</table>");
        out.println("</center>");

        out.println("</body>");
        out.println("</html>");
    }
}