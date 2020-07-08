/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interface1;

import code.Db;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.swing.ImageIcon;

/**
 *
 * @author KSK
 */
public class NewJFrame2 extends javax.swing.JFrame {

    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public NewJFrame2() {
        initComponents();
        con = Db.conn();
        setIconImage(Toolkit.getDefaultToolkit().getImage(NewJFrame2.class.getResource("logo1.png")));
        ImageIcon logo = new ImageIcon(getClass().getResource("logo1.png"));
        Image im1 = logo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        Image im2 = logo.getImage().getScaledInstance(270, 270, Image.SCALE_SMOOTH);
        ImageIcon Logo1 = new ImageIcon(im1);
        ImageIcon Logo2 = new ImageIcon(im2);
        this.logo.setIcon(Logo1);
        this.main_logo.setIcon(Logo2);
    }

    int total_available;

    public void clear() {
        this.item_code.setText(null);
        this.item_name.setText(null);
        this.selling_price.setText(null);
        this.commission.setText(null);
        this.payment_fee.setText(null);
        this.vat.setText(null);
        this.shipping_cost.setText(null);
        this.image.setIcon(null);
    }

    public void store() {
        String item_code = this.item_code.getText();
        String sql = "select * from limit_id where item_code='" + item_code + "'";
        try {
            pst = (PreparedStatement) con.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String limit_code = rs.getString("limit_code");
                String sql2 = "select * from products where item_code='" + item_code + "' limit limit_code,1";
                String replase_sql = sql2.replaceAll("limit_code", limit_code);
                String sql3 = replase_sql;
                try {
                    pst = (PreparedStatement) con.prepareStatement(sql3);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        String product_id = rs.getString("id");
                        int sold = Integer.parseInt(rs.getString("sold"));
                        int available = Integer.parseInt(rs.getString("available"));
                        if (available == 0) {
                            int Limit_code = Integer.parseInt(limit_code);
                            Limit_code++;
                            String sql4 = "update limit_id set limit_code='" + Limit_code + "' where item_code='" + item_code + "'";
                            try {
                                pst = (PreparedStatement) con.prepareStatement(sql4);
                                pst.execute();
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, e);
                            }
                            store();
                        } else {
                            sold++;
                            available--;
                            String sql5 = "update products set sold='" + sold + "',available='" + available + "' where id='" + product_id + "'";
                            try {
                                pst = (PreparedStatement) con.prepareStatement(sql5);
                                pst.execute();
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, e);
                            }
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        selling();
    }

    public void new_sale() throws SQLException {
        String item_code = this.item_code.getText();
        String sql = "Select * from selling where item_code=?";
        try {
            pst = (PreparedStatement) con.prepareStatement(sql);
            pst.setString(1, item_code);
            rs = pst.executeQuery();
            if (rs.next()) {
                String sql2 = "select * from selling where item_code='" + item_code + "' ORDER BY id DESC limit 1";
                pst = (PreparedStatement) con.prepareStatement(sql2);
                rs = pst.executeQuery();
                while (rs.next()) {
                    this.selling_price.setText(rs.getString("selling_price"));
                    this.commission.setText(rs.getString("commission"));
                    this.payment_fee.setText(rs.getString("payment_fee"));
                    this.vat.setText(rs.getString("vat"));
                    this.shipping_cost.setText(rs.getString("shipping_cost"));
                }
            } else {
                this.selling_price.setText(null);
                this.commission.setText(null);
                this.payment_fee.setText(null);
                this.vat.setText(null);
                this.shipping_cost.setText(null);
                JOptionPane.showMessageDialog(null, "Enter The New Value");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void selling() {
        String item_code = this.item_code.getText();
        String item_name = this.item_name.getText();
        float selling_price = Float.parseFloat(this.selling_price.getText());
        float commission = Float.parseFloat(this.commission.getText());
        float payment_fee = Float.parseFloat(this.payment_fee.getText());
        float vat = Float.parseFloat(this.vat.getText());
        float shipping_cost = Float.parseFloat(this.shipping_cost.getText());

        float Commission = ((selling_price / 100) * (commission + payment_fee));
        float Vat = (Commission / 100) * vat;

        String sql3 = "select limit_code from limit_id where item_code='" + item_code + "'";
        try {
            pst = (PreparedStatement) con.prepareStatement(sql3);
            rs = pst.executeQuery();
            while (rs.next()) {
                String limit_code = rs.getString("limit_code");
                String sql4 = "select * from products where item_code='" + item_code + "' limit limit_code,1";
                String replace_sql = sql4.replaceAll("limit_code", limit_code);
                String sql5 = replace_sql;
                try {
                    pst = (PreparedStatement) con.prepareStatement(sql5);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        float Price_of_unit = Float.parseFloat(rs.getString("price_of_unit"));
                        float profit = (selling_price) - (Commission + Vat + shipping_cost + Price_of_unit);
                        float benefit = profit / (Price_of_unit / 100);

                        String sql = "insert into selling(item_code,item_name,selling_price,commission,payment_fee,vat,shipping_cost,profit,benefit)values('" + item_code + "','" + item_name + "','" + selling_price + "','" + commission + "','" + payment_fee + "','" + vat + "','" + shipping_cost + "','" + profit + "','" + benefit + "')";
                        try {
                            pst = (PreparedStatement) con.prepareStatement(sql);
                            pst.execute();
                            total_available--;
                            JOptionPane.showMessageDialog(null, "Product Selling Done\n\nProfit  =  Rs." + profit + "\nBenefit  =  " + benefit + "%\nAvailable  =  " + total_available + "");
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e + "uu");
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        clear();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        item_code = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        item_name = new javax.swing.JTextField();
        selling_price = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        commission = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        payment_fee = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        vat = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        shipping_cost = new javax.swing.JTextField();
        image = new javax.swing.JLabel();
        main_logo = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        Panel = new javax.swing.JPanel();
        logout = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        home = new javax.swing.JLabel();
        product = new javax.swing.JLabel();
        report = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        sell = new javax.swing.JLabel();
        Return = new javax.swing.JLabel();
        cancel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("KSK (Pvt) Ltd");
        setUndecorated(true);
        setPreferredSize(new java.awt.Dimension(1000, 500));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 249, 242));
        jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel1MouseMoved(evt);
            }
        });
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        item_code.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        item_code.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        item_code.setToolTipText("Enter the item code here");
        item_code.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        item_code.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                item_codeKeyPressed(evt);
            }
        });
        jPanel1.add(item_code, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 130, 250, 30));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Item Code             ");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 130, -1, 30));

        item_name.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        item_name.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(item_name, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 170, 250, 30));

        selling_price.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        selling_price.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(selling_price, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 210, 250, 30));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Item Name            ");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 170, -1, 30));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Selling Price (Rs.)   ");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 210, -1, 30));

        commission.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        commission.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(commission, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 250, 250, 30));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setText("Commission (%)    ");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 250, -1, 30));

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setText("Payment Fee (%) ");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 290, -1, 30));

        payment_fee.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        payment_fee.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(payment_fee, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 290, 250, 30));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("VAT (%)                ");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 330, -1, 30));

        vat.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        vat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(vat, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 330, 250, 30));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setText("Shipping Cost (Rs.)");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 370, -1, 30));

        shipping_cost.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        shipping_cost.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPanel1.add(shipping_cost, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 370, 250, 30));

        image.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        image.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel1.add(image, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 130, 270, 270));
        jPanel1.add(main_logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 130, 270, 270));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 50)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("KSK (Pvt) Ltd");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 20, 400, -1));

        Panel.setBackground(new java.awt.Color(19, 28, 74));
        Panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                PanelMouseMoved(evt);
            }
        });
        Panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logout.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        logout.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logout.setText("          Log Out");
        logout.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                logoutMouseMoved(evt);
            }
        });
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutMouseClicked(evt);
            }
        });
        Panel.add(logout, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 440, 200, 40));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/interface1/log out1.png"))); // NOI18N
        Panel.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 420, 280, 80));

        home.setBackground(new java.awt.Color(0, 249, 242));
        home.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        home.setForeground(new java.awt.Color(255, 255, 255));
        home.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        home.setText("Home");
        home.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Panel.add(home, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 140, 40));

        product.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        product.setForeground(new java.awt.Color(255, 255, 255));
        product.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        product.setText("Product");
        product.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        product.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                productMouseMoved(evt);
            }
        });
        product.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                productMouseClicked(evt);
            }
        });
        Panel.add(product, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, 140, 40));

        report.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        report.setForeground(new java.awt.Color(255, 255, 255));
        report.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        report.setText("Report");
        report.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        report.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                reportMouseMoved(evt);
            }
        });
        report.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reportMouseClicked(evt);
            }
        });
        Panel.add(report, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 300, 140, 40));

        logo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Panel.add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 20, 100, 100));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Account");
        jLabel8.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Panel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 360, 140, 40));

        jPanel1.add(Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 500));

        sell.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        sell.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sell.setText("Sell");
        sell.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sell.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                sellMouseMoved(evt);
            }
        });
        sell.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sellMouseClicked(evt);
            }
        });
        jPanel1.add(sell, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 440, 140, 40));

        Return.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        Return.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Return.setText("Return");
        Return.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Return.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                ReturnMouseMoved(evt);
            }
        });
        Return.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ReturnMouseClicked(evt);
            }
        });
        jPanel1.add(Return, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 440, 150, 40));

        cancel.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        cancel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cancel.setText("Cancel");
        cancel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cancel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                cancelMouseMoved(evt);
            }
        });
        cancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cancelMouseClicked(evt);
            }
        });
        jPanel1.add(cancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 440, 150, 40));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1000, 500));

        setSize(new java.awt.Dimension(1000, 500));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void item_codeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_item_codeKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            String item_code = this.item_code.getText();
            String sql = "select * from products where item_code=?";
            try {
                pst = (PreparedStatement) con.prepareStatement(sql);
                pst.setString(1, item_code);
                rs = pst.executeQuery();
                if (rs.next()) {
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        item_name.setText(rs.getString("item_name"));
                        byte data[] = rs.getBytes("product_image");
                        ImageIcon image = new ImageIcon(data);
                        Image mi1 = image.getImage().getScaledInstance(this.image.getWidth(), this.image.getHeight(), Image.SCALE_SMOOTH);
                        ImageIcon mi2 = new ImageIcon(mi1);
                        this.image.setIcon(mi2);
                        String sql2 = "select sum(available) as Available from products where item_code='" + item_code + "'";
                        try {
                            pst = (PreparedStatement) con.prepareStatement(sql2);
                            rs = pst.executeQuery();
                            while (rs.next()) {
                                total_available = Integer.parseInt(rs.getString("Available"));
                                if (total_available == 0) {
                                    JOptionPane.showMessageDialog(null, "Sold Out", "Message", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    new_sale();
                                }
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e);
                        }

                    }
                } else {
                    clear();
                    JOptionPane.showMessageDialog(null, "Not Available Your Item Code");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }

        }
    }//GEN-LAST:event_item_codeKeyPressed

    private void PanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PanelMouseMoved
        product.setBackground(Color.WHITE);
        report.setBackground(Color.WHITE);
        sell.setBackground(Color.WHITE);
        Return.setBackground(Color.WHITE);
        cancel.setBackground(Color.WHITE);
        logout.setForeground(Color.WHITE);
    }//GEN-LAST:event_PanelMouseMoved

    private void productMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productMouseMoved
        Color color = new Color(0, 249, 242);
        product.setBackground(color);
    }//GEN-LAST:event_productMouseMoved

    private void reportMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reportMouseMoved
        Color color = new Color(0, 249, 242);
        report.setBackground(color);
    }//GEN-LAST:event_reportMouseMoved

    private void productMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productMouseClicked
        NewJFrame4 NJ4 = new NewJFrame4();
        NJ4.required_frame(1);
        NJ4.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_productMouseClicked

    private void reportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reportMouseClicked
        NewJFrame4 NJ4 = new NewJFrame4();
        NJ4.required_frame(2);
        NJ4.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_reportMouseClicked

    private void sellMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sellMouseClicked
        if (item_code.getText().isEmpty()
                || item_name.getText().isEmpty()
                || selling_price.getText().isEmpty()
                || commission.getText().isEmpty()
                || payment_fee.getText().isEmpty()
                || vat.getText().isEmpty()
                || shipping_cost.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Plase Check The Your Values", "Message", JOptionPane.WARNING_MESSAGE);
        } else {
            String sql = "select * from products where item_code=? and item_name=?";
            try {
                pst = (PreparedStatement) con.prepareStatement(sql);
                pst.setString(1, item_code.getText());
                pst.setString(2, item_name.getText());
                rs = pst.executeQuery();
                if (rs.next()) {
                    store();
                } else {
                    JOptionPane.showMessageDialog(null, "Dont ,March\nYour Item code OR Item Name", "Message", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }

        }
    }//GEN-LAST:event_sellMouseClicked

    private void ReturnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReturnMouseClicked
        if (this.item_code.getText().isEmpty()
                || item_name.getText().isEmpty()
                || selling_price.getText().isEmpty()
                || commission.getText().isEmpty()
                || payment_fee.getText().isEmpty()
                || vat.getText().isEmpty()
                || shipping_cost.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Plase Check The Your Values", "Message", JOptionPane.WARNING_MESSAGE);
        } else {
            String Item_code = this.item_code.getText();
            String Item_name = this.item_name.getText();
            String sql = "select * from products where item_code=? and item_name=?";
            try {
                pst = (PreparedStatement) con.prepareStatement(sql);
                pst.setString(1, Item_code);
                pst.setString(2, Item_name);
                rs = pst.executeQuery();
                if (rs.next()) {
                    String sql1 = "select * from selling where item_code=?";
                    try {
                        pst = (PreparedStatement) con.prepareStatement(sql1);
                        pst.setString(1, Item_code);
                        rs = pst.executeQuery();
                        if (rs.next()) {
                            int x = JOptionPane.showInternalConfirmDialog(null, "Do You Want To Return This Item?");
                            if (x == 0) {
                                String item_code = this.item_code.getText();
                                String sql2 = "delete from selling where item_code='" + item_code + "' order by id desc limit 1";
                                try {
                                    pst = (PreparedStatement) con.prepareStatement(sql2);
                                    pst.execute();
                                    JOptionPane.showMessageDialog(null, "Return Done!");
                                } catch (Exception e) {
                                    JOptionPane.showConfirmDialog(null, e);
                                }
                                clear();
                            }

                        } else {
                            JOptionPane.showMessageDialog(null, "This Item Has Never Been\nSold Before", "Message", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Dont ,March\nYour Item code OR Item Name", "Message", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_ReturnMouseClicked

    private void cancelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelMouseClicked
        clear();
    }//GEN-LAST:event_cancelMouseClicked

    private void sellMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sellMouseMoved
        Color color = new Color(19, 28, 74);
        sell.setBackground(color);
    }//GEN-LAST:event_sellMouseMoved

    private void ReturnMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReturnMouseMoved
        Color color = new Color(19, 28, 74);
        Return.setBackground(color);
    }//GEN-LAST:event_ReturnMouseMoved

    private void cancelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelMouseMoved
        Color color = new Color(19, 28, 74);
        cancel.setBackground(color);
    }//GEN-LAST:event_cancelMouseMoved

    private void jPanel1MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseMoved
        sell.setBackground(Color.WHITE);
        Return.setBackground(Color.WHITE);
        cancel.setBackground(Color.WHITE);
        logout.setForeground(Color.WHITE);
    }//GEN-LAST:event_jPanel1MouseMoved

    private void logoutMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseMoved
        logout.setForeground(Color.red);
    }//GEN-LAST:event_logoutMouseMoved

    private void logoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutMouseClicked
        System.exit(0);
    }//GEN-LAST:event_logoutMouseClicked

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
            java.util.logging.Logger.getLogger(NewJFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame2().setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panel;
    private javax.swing.JLabel Return;
    private javax.swing.JLabel cancel;
    private javax.swing.JTextField commission;
    private javax.swing.JLabel home;
    private javax.swing.JLabel image;
    private javax.swing.JTextField item_code;
    private javax.swing.JTextField item_name;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel logout;
    private javax.swing.JLabel main_logo;
    private javax.swing.JTextField payment_fee;
    private javax.swing.JLabel product;
    private javax.swing.JLabel report;
    private javax.swing.JLabel sell;
    private javax.swing.JTextField selling_price;
    private javax.swing.JTextField shipping_cost;
    private javax.swing.JTextField vat;
    // End of variables declaration//GEN-END:variables
}
