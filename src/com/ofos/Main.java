package com.ofos;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Launch GUI on EDT
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
