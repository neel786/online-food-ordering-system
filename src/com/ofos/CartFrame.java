package com.ofos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class CartFrame extends JFrame {
    private int userId;
    private List<MenuFrame.CartItem> cart;
    private DefaultTableModel model;
    private JButton btnPlaceOrder;

    public CartFrame(int userId, List<MenuFrame.CartItem> cart) {
        this.userId = userId;
        this.cart = cart;
        setTitle("Your Cart");
        setSize(600,350);
        setLocationRelativeTo(null);
        initComponents();
        loadCart();
    }

    private void initComponents() {
        model = new DefaultTableModel(new String[]{"Name","Price","Qty","Subtotal"},0){
            public boolean isCellEditable(int r,int c){ return false; }
        };
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        btnPlaceOrder = new JButton("Place Order");
        bottom.add(btnPlaceOrder);
        btnPlaceOrder.addActionListener(e -> placeOrder());
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadCart() {
        model.setRowCount(0);
        for(MenuFrame.CartItem it: cart){
            model.addRow(new Object[]{it.name, it.price, it.qty, it.price * it.qty});
        }
    }

    private void placeOrder() {
        double total = 0;
        for(MenuFrame.CartItem it: cart) total += it.price * it.qty;
        try (Connection conn = DBConnection.getConnection()){
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO orders (user_id,total) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setDouble(2, total);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if(keys.next()){
                    int orderId = keys.getInt(1);
                    try (PreparedStatement psi = conn.prepareStatement("INSERT INTO order_items (order_id,menu_item_id,quantity,price) VALUES (?,?,?,?)")) {
                        for(MenuFrame.CartItem it: cart){
                            psi.setInt(1, orderId);
                            psi.setInt(2, it.menuId);
                            psi.setInt(3, it.qty);
                            psi.setDouble(4, it.price);
                            psi.addBatch();
                        }
                        psi.executeBatch();
                    }
                }
                conn.commit();
                JOptionPane.showMessageDialog(this, "Order placed. Total: " + total);
                cart.clear();
                this.dispose();
            } catch(SQLException ex){
                conn.rollback();
                throw ex;
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed placing order: " + ex.getMessage());
        }
    }
}
