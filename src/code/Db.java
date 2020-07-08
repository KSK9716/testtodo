/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import com.mysql.jdbc.Connection;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author KSK
 */
public class Db {
    public static Connection conn(){
        Connection con=null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con=(Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/ksk","root","");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        return con;
    }
    
}
