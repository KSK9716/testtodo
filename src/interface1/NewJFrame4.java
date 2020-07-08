/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interface1;

import code.Db;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author KSK
 */
public class NewJFrame4 extends javax.swing.JFrame {

    int x = 0;
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public NewJFrame4() {
        initComponents();
        con = Db.conn();
        setIconImage(Toolkit.getDefaultToolkit().getImage(NewJFrame4.class.getResource("logo1.png")));
    }

    public void required_frame(int frame_number) {
        x = frame_number;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        user_name = new javax.swing.JTextField();
        password = new javax.swing.JPasswordField();
        login = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("KSK (Pvt) Ltd");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 249, 242));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("KSK (Pvt) Ltd");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 400, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("User Name");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Password");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));
        jPanel1.add(user_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 90, 190, -1));

        password.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                passwordKeyPressed(evt);
            }
        });
        jPanel1.add(password, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 140, 190, -1));

        login.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        login.setText("Login");
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });
        jPanel1.add(login, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 170, -1, -1));

        cancel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });
        jPanel1.add(cancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 170, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel4.setText("<html><u>Go To The Manage  User Account</u></html>");
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 220, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 400, 250));

        setSize(new java.awt.Dimension(416, 289));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        NewJFrame5 NJ5 = new NewJFrame5();
        NJ5.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jLabel4MouseClicked

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        System.exit(0);
    }//GEN-LAST:event_cancelActionPerformed

    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
        String display_user_name = this.user_name.getText();
        String display_password = this.password.getText();
        String sql = "select * from user_account order by id desc limit 1";
        try {
            pst = (PreparedStatement) con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String user_name = rs.getString("user_name");
                String password = rs.getString("password");
                if (user_name.equals(display_user_name) && password.equals(display_password)) {
                    if (x == 0) {
                        NewJFrame2 NJ2 = new NewJFrame2();
                        NJ2.setVisible(true);
                        this.dispose();
                    } else if (x == 1) {
                        NewJFrame1 NJ1 = new NewJFrame1();
                        NJ1.setVisible(true);
                        this.dispose();
                    } else {
                        NewJFrame3 NJ3 = new NewJFrame3();
                        NJ3.setVisible(true);
                        this.dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Wrong User Name OR Password", "Message", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_loginActionPerformed

    private void passwordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_passwordKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String display_user_name = this.user_name.getText();
        String display_password = this.password.getText();
        String sql = "select * from user_account order by id desc limit 1";
        try {
            pst = (PreparedStatement) con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String user_name = rs.getString("user_name");
                String password = rs.getString("password");
                if (user_name.equals(display_user_name) && password.equals(display_password)) {
                    if (x == 0) {
                        NewJFrame2 NJ2 = new NewJFrame2();
                        NJ2.setVisible(true);
                        this.dispose();
                    } else if (x == 1) {
                        NewJFrame1 NJ1 = new NewJFrame1();
                        NJ1.setVisible(true);
                        this.dispose();
                    } else {
                        NewJFrame3 NJ3 = new NewJFrame3();
                        NJ3.setVisible(true);
                        this.dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Wrong User Name OR Password", "Message", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        }
    }//GEN-LAST:event_passwordKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                /*if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }*/
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame4.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame4.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame4.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame4.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame4().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton login;
    private javax.swing.JPasswordField password;
    private javax.swing.JTextField user_name;
    // End of variables declaration//GEN-END:variables
}
