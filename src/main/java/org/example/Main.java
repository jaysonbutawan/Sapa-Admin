package org.example;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(DatabaseConnection.getSystemLookAndFeel());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        Connection conn = DatabaseConnection.Connect();
        if (conn != null) {
            JOptionPane.showMessageDialog(null, "✅ Successfully connected to the database!", "Connection Status", JOptionPane.INFORMATION_MESSAGE);
            try { conn.close(); } catch (Exception e) {}
        } else {
        }
        LoginUI.main(args);
    }
}
