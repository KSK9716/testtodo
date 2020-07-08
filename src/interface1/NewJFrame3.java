/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interface1;

import code.Db;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.io.File;
import java.io.FileOutputStream;
import javax.print.event.PrintJobEvent;
import javax.swing.JOptionPane;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author KSK
 */
public class NewJFrame3 extends javax.swing.JFrame {

    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public NewJFrame3() {
        con = Db.conn();
        initComponents();
    }

    public void sales_report() {
        String sql1 = "select count(id)as total_order from selling";
        try {
            pst = (PreparedStatement) con.prepareStatement(sql1);
            rs = pst.executeQuery();
            while (rs.next()) {
                int total_order = rs.getInt("total_order");
                String sql2 = "select sum(last_report_total_sales) as sum from sales_report";
                try {
                    pst = (PreparedStatement) con.prepareStatement(sql2);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        int last_report_order_count = rs.getInt("sum");
                        String display_total_order = Integer.toString(total_order - last_report_order_count);
                        this.report_type.setText("Total Sales = ");
                        this.total.setText(display_total_order);
                        String sql3 = "select item_code,item_name,selling_price,commission,payment_fee,vat,shipping_cost,profit,benefit from selling order by id desc limit display_total_order";
                        String replace_sql3 = sql3.replaceAll("display_total_order", display_total_order);
                        try {
                            pst = (PreparedStatement) con.prepareStatement(replace_sql3);
                            rs = pst.executeQuery();
                            report_table.setModel(DbUtils.resultSetToTableModel(rs));
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void order_report() {
        String sql = "select count(id) as count from products";
        try {
            pst = (PreparedStatement) con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                int total_order = rs.getInt("count");
                String sql1 = "select sum(last_report_total_order)as sum from order_report";
                try {
                    pst = (PreparedStatement) con.prepareStatement(sql1);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        int last_report_total_order = rs.getInt("sum");
                        String display_total_order = Integer.toString(total_order - last_report_total_order);
                        this.report_type.setText("Total Order = ");
                        this.total.setText(display_total_order);
                        String sql2 = "select item_code,item_name,order_date,price_of_unit,qty,total_price from products order by id desc limit display_total_order";
                        String replace_sql2 = sql2.replaceAll("display_total_order", display_total_order);
                        try {
                            pst = (PreparedStatement) con.prepareStatement(replace_sql2);
                            rs = pst.executeQuery();
                            report_table.setModel(DbUtils.resultSetToTableModel(rs));
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void get_sales_report() {
        LocalDate date = LocalDate.now();
        String full_date = date.toString();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd");
        int Date = Integer.parseInt(date.format(dtf));
        String total_sales = this.total.getText();
        Cell cell = null;
        String sql = "select report_close_date from sales_report order by report_close_date desc limit 1";
        try {
            pst = (PreparedStatement) con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String last_report_close_date = rs.getString("report_close_date");
                if (full_date.equals(last_report_close_date)) {
                    JOptionPane.showMessageDialog(null, "Please Try Again Next Time", "Message", JOptionPane.WARNING_MESSAGE);
                } else {
                    if (Date == 01 || Date == 30) {
                        int columns = report_table.getColumnCount();
                        int rows = report_table.getRowCount();
                        System.out.println(rows);
                        DefaultTableModel dt = (DefaultTableModel) report_table.getModel();

                        XSSFWorkbook wb = new XSSFWorkbook();
                        XSSFSheet ws = wb.createSheet();
                        XSSFRow row = null;

                        String array[] = {full_date, "", "", "KSK", "", "", "Total Sales : " + total_sales};
                        row = ws.createRow(0);
                        for (int x = 0; x < array.length; x++) {
                            cell = row.createCell(x);
                            cell.setCellValue(array[x]);
                        }

                        //set columns
                        row = ws.createRow(2);
                        for (int x = 0; x < columns; x++) {
                            String column_name = dt.getColumnName(x);
                            cell = row.createCell(x);
                            cell.setCellValue(column_name);
                        }
                        //set rows
                        for (int y = 0; y < rows; y++) {
                            row = ws.createRow(y + 3);
                            for (int z = 0; z < columns; z++) {
                                Object row_value = dt.getValueAt(y, z);
                                cell = row.createCell(z);
                                if (row_value instanceof String) {
                                    cell.setCellValue((String)row_value);
                                }
                                else if(row_value instanceof Integer){
                                    cell.setCellValue((Integer)row_value);
                                }
                                else{
                                    cell.setCellValue((Float)row_value);
                                }

                            }
                        }

                        try {
                            JFileChooser excel = new JFileChooser();
                            excel.setSelectedFile(new File("sales report " + full_date));
                            int x = excel.showSaveDialog(null);
                            if (x == JFileChooser.APPROVE_OPTION) {
                                File f = excel.getSelectedFile();
                                FileOutputStream file = new FileOutputStream(f + ".xlsx");
                                wb.write(file);

                                String sql1 = "insert into sales_report(last_report_total_sales,report_close_date) value('" + total_sales + "','" + full_date + "')";
                                try {
                                    pst = (PreparedStatement) con.prepareStatement(sql1);
                                    pst.execute();
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(null, e);
                                }
                                while (dt.getRowCount() > 0) {
                                    dt.removeRow(0);
                                }
                                JOptionPane.showMessageDialog(null, "Sales Report Close Done!");
                            }

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e);
                        }

                    } else {
                        JOptionPane.showMessageDialog(null, "Report Close\nDate Of  01 OR 15", "Message", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void get_order_report() {
        LocalDate date = LocalDate.now();
        String Date = date.toString();
        String order_report_number = null;
        String sql = "select count(id) as count from order_report";
        try {
            pst = (PreparedStatement) con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                order_report_number = rs.getString("count");
            }
        } catch (Exception e) {
        }
        Cell cell = null;
        int total_order = Integer.parseInt(this.total.getText());
        if (total_order >= 6) {
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet ws = wb.createSheet();
            XSSFRow row = null;

            int column_count = report_table.getColumnCount();
            int row_count = report_table.getRowCount();

            String array[] = {Date, "", "KSK", "", "", "Total Order : " + total_order};
            row = ws.createRow(0);
            for (int x = 0; x < array.length; x++) {
                cell = row.createCell(x);
                cell.setCellValue(array[x]);
            }

            row = ws.createRow(2);
            for (int x = 0; x < column_count; x++) {
                String column_name = report_table.getColumnName(x);
                cell = row.createCell(x);
                cell.setCellValue(column_name);
            }
            for (int y = 3; y < row_count; y++) {
                row = ws.createRow(y);
                for (int z = 0; z < column_count; z++) {
                    String row_value = report_table.getValueAt(y, z).toString();
                    cell = row.createCell(z);
                    cell.setCellValue(row_value);
                }
            }
            try {
                JFileChooser file = new JFileChooser();
                file.setSelectedFile(new File("order report " + order_report_number));
                int x = file.showSaveDialog(null);
                if (x == JFileChooser.APPROVE_OPTION) {
                    File f = file.getSelectedFile();
                    FileOutputStream File = new FileOutputStream(f + ".xlsx");
                    wb.write(File);
                    String sql2 = "insert into order_report(last_report_total_order)value('" + total_order + "')";
                    try {
                        pst = (PreparedStatement) con.prepareStatement(sql2);
                        pst.execute();
                        DefaultTableModel dt = (DefaultTableModel) report_table.getModel();
                        while (dt.getRowCount() > 0) {
                            dt.removeRow(0);
                        }
                        this.total.setText("0");
                        JOptionPane.showMessageDialog(null, "Order Report Close Done!");
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            } catch (Exception e) {
            }
        } else {
            JOptionPane.showMessageDialog(null, "Miniumum Of 50 Orders Are Required to\nClose The Order Report", "Message", JOptionPane.WARNING_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        sr = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        report_table = new javax.swing.JTable();
        or = new javax.swing.JRadioButton();
        sales_report_close = new javax.swing.JButton();
        report_type = new javax.swing.JLabel();
        total = new javax.swing.JLabel();
        back = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("KSK (Pvt) Ltd");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Report Summary");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 20, 300, -1));

        buttonGroup1.add(sr);
        sr.setText("Sales Report");
        sr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                srMouseClicked(evt);
            }
        });
        jPanel1.add(sr, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        report_table.setBackground(new java.awt.Color(51, 255, 255));
        report_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        report_table.setGridColor(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(report_table);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 760, 300));

        buttonGroup1.add(or);
        or.setText("Order Report");
        or.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                orMouseClicked(evt);
            }
        });
        jPanel1.add(or, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        sales_report_close.setBackground(new java.awt.Color(0, 255, 255));
        sales_report_close.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        sales_report_close.setText("Get Report Close");
        sales_report_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sales_report_closeActionPerformed(evt);
            }
        });
        jPanel1.add(sales_report_close, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        report_type.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        report_type.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jPanel1.add(report_type, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 130, 150, 30));

        total.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        total.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel1.add(total, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 130, 100, 30));

        back.setBackground(new java.awt.Color(51, 0, 255));
        back.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        back.setText("Back");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });
        jPanel1.add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 130, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/interface1/logo3.png"))); // NOI18N
        jLabel2.setText("jLabel2");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 500));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 500));

        setSize(new java.awt.Dimension(816, 539));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void srMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_srMouseClicked
        sales_report();
    }//GEN-LAST:event_srMouseClicked

    private void orMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_orMouseClicked
        order_report();
    }//GEN-LAST:event_orMouseClicked

    private void sales_report_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sales_report_closeActionPerformed

        if (sr.isSelected() == true) {
            get_sales_report();
        } else if (or.isSelected() == true) {
            get_order_report();
        } else {
            JOptionPane.showMessageDialog(null, "Please Select The Report Type", "Message", JOptionPane.WARNING_MESSAGE);
        }

    }//GEN-LAST:event_sales_report_closeActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        NewJFrame2 NJ1 = new NewJFrame2();
        NJ1.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_backActionPerformed

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
            java.util.logging.Logger.getLogger(NewJFrame3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame3().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton back;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton or;
    private javax.swing.JTable report_table;
    private javax.swing.JLabel report_type;
    private javax.swing.JButton sales_report_close;
    private javax.swing.JRadioButton sr;
    private javax.swing.JLabel total;
    // End of variables declaration//GEN-END:variables
}
