package com.ofos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class MenuFrame extends JFrame {
    private int userId;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnAddToCart, btnViewCart;

    public MenuFrame(int userId) {
        this.userId = userId;
        setTitle("Menu - Browse & Order");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700,400);
        setLocationRelativeTo(null);
        initComponents();
        loadMenuItems();
    }

    private void initComponents() {
        model = new DefaultTableModel(new String[]{"ID","Name","Description","Price","Available"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        JPanel bottom = new JPanel();
        btnAddToCart = new JButton("Add to Cart");
        btnViewCart = new JButton("View Cart");
        bottom.add(btnAddToCart);
        bottom.add(btnViewCart);

        btnAddToCart.addActionListener(e -> addToCart());
        btnViewCart.addActionListener(e -> viewCart());

        add(sp, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadMenuItems() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,name,description,price,available FROM menu_items WHERE available=1")) {
            while(rs.next()){
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getBoolean("available")
                });
            }
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed loading menu: " + ex.getMessage());
        }
    }

    // Very simple cart (in-memory)
    private java.util.List<CartItem> cart = new java.util.ArrayList<>();

    private void addToCart() {
        int r = table.getSelectedRow();
        if(r<0){ JOptionPane.showMessageDialog(this, "Select an item"); return; }
        int id = (int)model.getValueAt(r,0);
        String name = (String)model.getValueAt(r,1);
        double price = (double)model.getValueAt(r,3);
        String qtyStr = JOptionPane.showInputDialog(this, "Quantity:", "1");
        if(qtyStr==null) return;
        int qty = 1;
        try { qty = Integer.parseInt(qtyStr); } catch(Exception e){ JOptionPane.showMessageDialog(this,"Invalid number"); return; }
        cart.add(new CartItem(id, name, price, qty));
        JOptionPane.showMessageDialog(this, "Added to cart");
    }

    private void viewCart() {
        if(cart.isEmpty()){ JOptionPane.showMessageDialog(this, "Cart is empty"); return; }
        CartFrame cf = new CartFrame(userId, cart);
        cf.setVisible(true);
    }

    // Inner simple cart item class
    static class CartItem {
        int menuId;
        String name;
        double price;
        int qty;
        CartItem(int menuId,String name,double price,int qty){ this.menuId=menuId; this.name=name; this.price=price; this.qty=qty; }
    }
}
