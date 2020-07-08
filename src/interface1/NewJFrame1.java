package interface1;

import code.Db;
import com.mysql.jdbc.Connection;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.log4j.pattern.IntegerPatternConverter;

public class NewJFrame1 extends javax.swing.JFrame {

    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    Blob blob = null;

    public NewJFrame1() {
        initComponents();
        con = Db.conn();
        setIconImage(Toolkit.getDefaultToolkit().getImage(NewJFrame1.class.getResource("d.png")));
        table();
        ImageIcon Logo = new ImageIcon(getClass().getResource("logo1.png"));
        Image im1 = Logo.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        ImageIcon im2 = new ImageIcon(im1);
        logo.setIcon(im2);

    }

    public void get_image() {
        JFileChooser file = new JFileChooser();
        FileNameExtensionFilter filter=new FileNameExtensionFilter("PNG", "png");
        file.setFileFilter(filter);
        int state = file.showOpenDialog(null);
        if (state == JFileChooser.APPROVE_OPTION) {
            File path = file.getSelectedFile();
            String Path = path.getAbsolutePath();
            ImageIcon image1 = new ImageIcon(Path);
            Image set_size = image1.getImage().getScaledInstance(this.image.getWidth(), this.image.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon Image = new ImageIcon(set_size);
            this.image.setIcon(Image);
        } else {

        }

    }

    public void product_check() {
        Date date = order_date.getDate();
        if (item_code.getText().isEmpty()
                || item_name.getText().isEmpty()
                || date == null
                || qty.getText().isEmpty()
                || price_of_unit.getText().isEmpty()
                || image.getIcon() == null) {
            JOptionPane.showMessageDialog(null, "Plase Check The Your Values", "Message", JOptionPane.WARNING_MESSAGE);
        } else {
            String sql4 = "select * from products where item_code=? or item_name=? ";
            try {
                pst = con.prepareStatement(sql4);
                pst.setString(1, item_code.getText());
                pst.setString(2, item_name.getText());
                rs = pst.executeQuery();
                if (rs.next()) {
                    String sql5 = "select * from products where item_code=? and item_name=? ";
                    try {
                        pst = con.prepareStatement(sql5);
                        pst.setString(1, item_code.getText());
                        pst.setString(2, item_name.getText());
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            product_added();
                        } else {
                            JOptionPane.showMessageDialog(null, "Duplicate values", "Message", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);
                    }

                } else {
                    product_added();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    public Blob blob_convertor() throws IOException, SQLException {
        Icon ic = image.getIcon();
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        ic.paintIcon(null, g, 0, 0);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", bos);
        byte data[] = bos.toByteArray();
        Blob blob = new SerialBlob(data);
        return blob;
    }

    public void product_added() throws FileNotFoundException, IOException, SQLException {

        String itemcode = item_code.getText();
        String itemname = item_name.getText();
        SimpleDateFormat dt = new SimpleDateFormat("yy-MM-dd");
        String orderdate = dt.format(order_date.getDate());
        int Price = Integer.parseInt(price_of_unit.getText());
        int Qty = Integer.parseInt(qty.getText());
        int total_price = Price * Qty;
        blob = blob_convertor();

        String sql = "insert into products(item_code,item_name,order_date,price_of_unit,qty,sold,available,product_image,total_price)values('" + itemcode + "','" + itemname + "','" + orderdate + "','" + Price + "','" + Qty + "',0,'" + Qty + "',?,'" + total_price + "')";
        try {
            pst = con.prepareStatement(sql);
            pst.setBlob(1, blob);
            pst.execute();

            String sql2 = "select item_code from limit_id where item_code=?";
            try {
                pst = con.prepareStatement(sql2);
                pst.setString(1, itemcode);
                rs = pst.executeQuery();
                if (rs.next()) {
                    int x = 0;
                } else {
                    String sql3 = "insert into limit_id(item_code,limit_code)value('" + itemcode + "',0)";
                    try {
                        pst = con.prepareStatement(sql3);
                        pst.execute();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }

            JOptionPane.showMessageDialog(null, "Successful");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        clear();
        table();
    }

    public void table() {
        String sql = "select id,item_code,Item_name,order_date,price_of_unit,qty,sold,available from products";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            products_table.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void table_select_value() {
        int row_number = products_table.getSelectedRow();
        id.setText(products_table.getValueAt(row_number, 0).toString());
        item_code.setText(products_table.getValueAt(row_number, 1).toString());
        item_name.setText(products_table.getValueAt(row_number, 2).toString());
        String date = products_table.getValueAt(row_number, 3).toString();
        try {
            Date date1 = new SimpleDateFormat("yy-MM-dd").parse(date);
            order_date.setDate(date1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        price_of_unit.setText(products_table.getValueAt(row_number, 4).toString());
        qty.setText(products_table.getValueAt(row_number, 5).toString());

        String sql = "select product_image from products where id='" + id.getText() + "' ";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                byte image[] = rs.getBytes("product_image");
                ImageIcon im1 = new ImageIcon(image);
                Image im2 = im1.getImage().getScaledInstance(this.image.getWidth(), this.image.getHeight(), Image.SCALE_SMOOTH);
                ImageIcon im3 = new ImageIcon(im2);
                this.image.setIcon(im3);
            }
        } catch (Exception e) {
        }
    }

    public void clear() {
        id.setText(null);
        item_code.setText(null);
        item_name.setText(null);
        order_date.setDate(null);
        price_of_unit.setText(null);
        qty.setText(null);
        search.setText(null);
        image.setIcon(null);
        table();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        products_table = new javax.swing.JTable();
        add_new_bt = new javax.swing.JButton();
        item_code = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        item_name = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        order_date = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        qty = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        price_of_unit = new javax.swing.JTextField();
        update = new javax.swing.JButton();
        delete = new javax.swing.JButton();
        clear = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        search = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        id = new javax.swing.JLabel();
        choose_image = new javax.swing.JButton();
        image = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        back = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("KSK (Pvt) Ltd");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 249, 242));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        products_table.setBackground(new java.awt.Color(102, 255, 255));
        products_table.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        products_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
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
        products_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                products_tableMouseClicked(evt);
            }
        });
        products_table.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                products_tableKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                products_tableKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(products_table);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 370, 760, 110));

        add_new_bt.setBackground(new java.awt.Color(0, 255, 255));
        add_new_bt.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        add_new_bt.setText("Add New +");
        add_new_bt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add_new_btActionPerformed(evt);
            }
        });
        jPanel1.add(add_new_bt, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 330, 140, -1));

        item_code.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jPanel1.add(item_code, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 130, 140, 30));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Item Code      ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, 30));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Item Name     ");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, 30));

        item_name.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jPanel1.add(item_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 170, 140, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setText("Order Date    ");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, 30));

        order_date.setDateFormatString("yy-MM-dd");
        order_date.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jPanel1.add(order_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 210, 140, 30));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setText("Qty                ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, -1, 30));

        qty.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        qty.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                qtyKeyTyped(evt);
            }
        });
        jPanel1.add(qty, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 250, 140, 30));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("Price              ");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, -1, 30));

        price_of_unit.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        price_of_unit.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        price_of_unit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                price_of_unitKeyTyped(evt);
            }
        });
        jPanel1.add(price_of_unit, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 290, 140, 30));

        update.setBackground(new java.awt.Color(0, 255, 255));
        update.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        update.setText("Update");
        update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateActionPerformed(evt);
            }
        });
        jPanel1.add(update, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 170, 100, -1));

        delete.setBackground(new java.awt.Color(0, 255, 255));
        delete.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        delete.setText("Delete");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        jPanel1.add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 210, 100, -1));

        clear.setBackground(new java.awt.Color(0, 255, 255));
        clear.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        clear.setText("Clear");
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });
        jPanel1.add(clear, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 130, 100, -1));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Products");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 20, 200, -1));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        search.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        search.setToolTipText("item code");
        search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchKeyReleased(evt);
            }
        });
        jPanel2.add(search, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 170, 30));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 190, 60));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("ID");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, 30));

        id.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jPanel1.add(id, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 100, 140, 20));

        choose_image.setBackground(new java.awt.Color(0, 255, 255));
        choose_image.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        choose_image.setText("Choose Image");
        choose_image.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                choose_imageActionPerformed(evt);
            }
        });
        jPanel1.add(choose_image, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 330, -1, -1));

        image.setBackground(new java.awt.Color(255, 255, 255));
        image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel1.add(image, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 60, 250, 250));
        jPanel1.add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 60, 250, 250));

        back.setBackground(new java.awt.Color(51, 0, 255));
        back.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        back.setText("Back");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });
        jPanel1.add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 330, 100, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 500));

        setSize(new java.awt.Dimension(816, 539));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyReleased
        String key = search.getText();
        String sql = "select id,item_code,Item_name,order_date,price_of_unit,qty,sold,available from products where item_Code like '%" + key + "%'";
        try {
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();
            products_table.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_searchKeyReleased

    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
        clear();
    }//GEN-LAST:event_clearActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        int x = JOptionPane.showConfirmDialog(null, "Do You Want To Delete This Item?");
        if (x == 0) {
            String Id = id.getText();
            String itemcode = item_code.getText();
            SimpleDateFormat dt = new SimpleDateFormat("yy-MM-dd");
            String orderdate = dt.format(order_date.getDate());
            String sql = "delete from products where id='" + Id + "'";
            try {
                pst = con.prepareStatement(sql);
                pst.execute();
                JOptionPane.showMessageDialog(null, "Delete");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
            table();
            clear();
        }
    }//GEN-LAST:event_deleteActionPerformed

    private void updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateActionPerformed
        int x = JOptionPane.showConfirmDialog(null, "Do You Want To Update This Item?");
        if (x == 0) {
            String Id = id.getText();
            String itemcode = item_code.getText();
            String itemname = item_name.getText();
            SimpleDateFormat dt = new SimpleDateFormat("yy-MM-dd");
            String orderdate = dt.format(order_date.getDate());
            String Price = price_of_unit.getText();
            String Qty = qty.getText();

            try {
                blob = blob_convertor();
            } catch (IOException ex) {
                Logger.getLogger(NewJFrame1.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(NewJFrame1.class.getName()).log(Level.SEVERE, null, ex);
            }

            String Available=null;
            String sql = "select sold from products where id='" + Id + "'";
            try {
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();
                while (rs.next()) {
                    int sold = Integer.parseInt(rs.getString("sold"));
                    int qty = Integer.parseInt(Qty);
                    Available = Integer.toString(qty - sold);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
            String sql1 = "update products set item_code='" + itemcode + "',item_name='" + itemname + "',order_date='" + orderdate + "',price_of_unit='" + Price + "',qty='" + Qty + "',available='" + Available + "'where id='" + Id + "'";
            try {
                pst = con.prepareStatement(sql1);
                pst.execute();
                String sql2 = "update products set product_image=? where item_code='" + itemcode + "'";
                try {
                    pst = con.prepareStatement(sql2);
                    pst.setBlob(1, blob);
                    pst.execute();
                    JOptionPane.showMessageDialog(null, "Update");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
            clear();
            table();
        }

    }//GEN-LAST:event_updateActionPerformed

    private void add_new_btActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_add_new_btActionPerformed
        product_check();
    }//GEN-LAST:event_add_new_btActionPerformed

    private void products_tableKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_products_tableKeyReleased
        table_select_value();
    }//GEN-LAST:event_products_tableKeyReleased

    private void products_tableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_products_tableKeyPressed
        table_select_value();
    }//GEN-LAST:event_products_tableKeyPressed

    private void products_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_products_tableMouseClicked
        table_select_value();
    }//GEN-LAST:event_products_tableMouseClicked

    private void choose_imageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_choose_imageActionPerformed
        get_image();
    }//GEN-LAST:event_choose_imageActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        NewJFrame2 NJ1 = new NewJFrame2();
        NJ1.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_backActionPerformed

    private void qtyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_qtyKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
    }//GEN-LAST:event_qtyKeyTyped

    private void price_of_unitKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_price_of_unitKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
    }//GEN-LAST:event_price_of_unitKeyTyped

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
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add_new_bt;
    private javax.swing.JButton back;
    private javax.swing.JButton choose_image;
    private javax.swing.JButton clear;
    private javax.swing.JButton delete;
    private javax.swing.JLabel id;
    private javax.swing.JLabel image;
    private javax.swing.JTextField item_code;
    private javax.swing.JTextField item_name;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel logo;
    private com.toedter.calendar.JDateChooser order_date;
    private javax.swing.JTextField price_of_unit;
    private javax.swing.JTable products_table;
    private javax.swing.JTextField qty;
    private javax.swing.JTextField search;
    private javax.swing.JButton update;
    // End of variables declaration//GEN-END:variables
}
