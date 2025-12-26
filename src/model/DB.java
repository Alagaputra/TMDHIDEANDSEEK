package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DB {
    private Connection con;
    private Statement stm;
    
    // Sesuaikan nama database kamu di sini
    private String url = "jdbc:mysql://localhost/dbhide_seek"; 
    private String user = "root";
    private String pass = ""; // Password default XAMPP biasanya kosong

    public DB() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        con = DriverManager.getConnection(url, user, pass);
        stm = con.createStatement();
    }

    public ResultSet selectQuery(String query) throws Exception {
        return stm.executeQuery(query);
    }

    public void updateQuery(String query) throws Exception {
        stm.executeUpdate(query);
    }
    
    public void close() throws Exception {
        if (stm != null) stm.close();
        if (con != null) con.close();
    }
}