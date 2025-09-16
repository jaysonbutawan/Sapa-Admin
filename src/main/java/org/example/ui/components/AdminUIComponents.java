package org.example.ui.components;

import org.example.utils.UIStyler;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * Reusable UI components for the admin panel
 */
public class AdminUIComponents {

    /**
     * Create the header panel for the admin interface
     */
    public static JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyler.PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = UIStyler.createStyledLabel("Administration Panel", UIStyler.HEADER_FONT, Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(UIStyler.PRIMARY_COLOR);

        JLabel userLabel = UIStyler.createStyledLabel("Admin User", new Font("Segoe UI", Font.PLAIN, 14), Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(UIStyler.MAIN_FONT);
        logoutButton.setBackground(UIStyler.SECONDARY_COLOR);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);

        userPanel.add(userLabel);
        userPanel.add(logoutButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Create the status bar for the admin interface
     */
    public static JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.setBackground(new Color(240, 240, 240));

        JLabel statusLabel = UIStyler.createStyledLabel("Ready", UIStyler.MAIN_FONT, UIStyler.TEXT_COLOR);
        JLabel dateLabel = UIStyler.createStyledLabel(new Date().toString(), UIStyler.MAIN_FONT, UIStyler.TEXT_COLOR);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(dateLabel, BorderLayout.EAST);

        return statusPanel;
    }
}
