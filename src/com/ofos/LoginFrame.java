package com.ofos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnRegister;

    public LoginFrame() {
        setTitle("Online Food Ordering - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380,220);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; p.add(new JLabel("Username:"), c);
        c.gridx=1; txtUser = new JTextField(15); p.add(txtUser,c);

        c.gridx=0; c.gridy=1; p.add(new JLabel("Password:"), c);
        c.gridx=1; txtPass = new JPasswordField(15); p.add(txtPass,c);

        c.gridx=0; c.gridy=2; btnLogin = new JButton("Login"); p.add(btnLogin,c);
        c.gridx=1; btnRegister = new JButton("Register"); p.add(btnRegister,c);

        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> doRegister());

        add(p);
    }

    private void doLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());

        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Enter username/password");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id, role FROM users WHERE username=? AND password=?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int userId = rs.getInt("id");
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this, "Login successful as " + role);
                this.dispose();
                if("ADMIN".equals(role)){
                    AdminFrame af = new AdminFrame();
                    af.setVisible(true);
                } else {
                    MenuFrame mf = new MenuFrame(userId);
                    mf.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void doRegister() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());
        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Enter username/password");
            return;
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username,password,role) VALUES (?,?, 'USER')")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registered. You can login now.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration failed: " + ex.getMessage());
        }
    }
}
