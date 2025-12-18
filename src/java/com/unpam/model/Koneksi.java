package com.unpam.model;

/**
 *
 * @author FAUZI RAHMAT
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Koneksi {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver"; 
    private static final String DATABASE = "jdbc:mysql://localhost:3306/dbaplikasigajikaryawan";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private Connection connection;
    private String pesanKesalahan;
    
    public String getPesanKesalahan(){
        return pesanKesalahan;
    }

    public Connection getConnection(){ 
        connection = null; 
        pesanKesalahan = "";
        
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex){
            pesanKesalahan = "JDBC Driver tidak ditemukan atau rusak\n" + ex;
        }

        if (pesanKesalahan.equals("")){ 
            try {
                // Menggunakan method 3 parameter (URL, User, Password) lebih aman dan rapi
                connection = DriverManager.getConnection(DATABASE, USER, PASSWORD);
            } catch (SQLException ex) {
                pesanKesalahan = "Koneksi ke " + DATABASE + " gagal\n" + ex;
            }
        }
        
        return connection;
    }
}