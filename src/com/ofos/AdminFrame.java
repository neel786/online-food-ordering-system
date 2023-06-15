package com.ofos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminFrame extends JFrame {
    private DefaultTableModel model;
    private JTextField txtName, txtPrice;
    private JTextArea txtDesc;
    private JButton btnAdd, btnRefresh, btnToggle;

    public AdminFrame() {
        setTitle("Admin - Manage Menu");
        setSize(700,450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
        loadItems();
    }

    private void initComponents() {
        model = new DefaultTableModel(new String[]{"ID","Name","Description","Price","Available"},0){
            public boolean isCellEditable(int r,int c){ return false; }
        };
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel right = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx=0; c.gridy=0; right.add(new JLabel("Name:"),c);
        c.gridx=1; txtName = new JTextField(15); right.add(txtName,c);
        c.gridx=0; c.gridy=1; right.add(new JLabel("Price:"),c);
        c.gridx=1; txtPrice = new JTextField(15); right.add(txtPrice,c);
        c.gridx=0; c.gridy=2; right.add(new JLabel("Description:"),c);
        c.gridx=1; txtDesc = new JTextArea(4,15); right.add(new JScrollPane(txtDesc),c);

        c.gridx=0; c.gridy=3; btnAdd = new JButton("Add Item"); right.add(btnAdd,c);
        c.gridx=1; btnRefresh = new JButton("Refresh"); right.add(btnRefresh,c);
        c.gridx=0; c.gridy=4; btnToggle = new JButton("Toggle Available"); right.add(btnToggle,c);

        btnAdd.addActionListener(e -> addItem());
        btnRefresh.addActionListener(e -> loadItems());
        btnToggle.addActionListener(e -> toggleSelectedAvailable(table));

        add(right, BorderLayout.EAST);
    }

    private void loadItems() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id,name,description,price,available FROM menu_items")) {
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
            JOptionPane.showMessageDialog(this, "Load failed: " + ex.getMessage());
        }
    }

    private void addItem() {
        String name = txtName.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String desc = txtDesc.getText().trim();
        if(name.isEmpty() || priceStr.isEmpty()){ JOptionPane.showMessageDialog(this,"Enter name and price"); return; }
        double price=0;
        try { price = Double.parseDouble(priceStr); } catch(Exception e){ JOptionPane.showMessageDialog(this,"Invalid price"); return; }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO menu_items (name,description,price,available) VALUES (?,?,?,1)")) {
            ps.setString(1, name);
            ps.setString(2, desc);
            ps.setDouble(3, price);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item added");
            loadItems();
            txtName.setText(""); txtPrice.setText(""); txtDesc.setText("");
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Add failed: " + ex.getMessage());
        }
    }

    private void toggleSelectedAvailable(JTable table) {
        int r = table.getSelectedRow();
        if(r<0){ JOptionPane.showMessageDialog(this, "Select a row"); return; }
        int id = (int)model.getValueAt(r,0);
        boolean avail = (boolean)model.getValueAt(r,4);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE menu_items SET available=? WHERE id=?")) {
            ps.setBoolean(1, !avail);
            ps.setInt(2, id);
            ps.executeUpdate();
            loadItems();
        } catch(SQLException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Toggle failed: " + ex.getMessage());
        }
    }
}
