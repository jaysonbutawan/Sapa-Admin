package org.example.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Utility class for refreshing UI components with new data
 */
public class RefreshUtils {

    /**
     * Refreshes a table model with new data
     * @param tableModel The table model to refresh
     * @param data The new data to populate the table with
     */
    public static void refreshTableModel(DefaultTableModel tableModel, Object[][] data) {
        if (tableModel == null) {
            throw new IllegalArgumentException("Table model cannot be null");
        }

        // Clear existing data
        tableModel.setRowCount(0);

        // Add new data
        if (data != null) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
        }

        // Notify listeners that the table data has changed
        tableModel.fireTableDataChanged();
    }

    /**
     * Safely refreshes a table model with new data, handling null cases
     * @param tableModel The table model to refresh
     * @param data The new data to populate the table with
     * @param fallbackMessage Message to show if data is null or empty
     */
    public static void safeRefreshTableModel(DefaultTableModel tableModel, Object[][] data, String fallbackMessage) {
        if (tableModel == null) {
            return;
        }

        tableModel.setRowCount(0);

        if (data != null && data.length > 0) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
        } else if (fallbackMessage != null) {
            // Add a single row with the fallback message
            Object[] emptyRow = new Object[tableModel.getColumnCount()];
            emptyRow[0] = fallbackMessage;
            tableModel.addRow(emptyRow);
        }

        tableModel.fireTableDataChanged();
    }

    /**
     * Creates a refresh control panel with a refresh button
     * @param refreshAction The action to perform when refresh button is clicked
     * @return A JPanel containing refresh controls
     */
    public static JPanel createRefreshControlPanel(ActionListener refreshAction) {
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(UIStyler.MAIN_FONT);
        refreshButton.setBackground(UIStyler.ACCENT_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(UIStyler.ACCENT_COLOR.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(UIStyler.ACCENT_COLOR);
            }
        });

        if (refreshAction != null) {
            refreshButton.addActionListener(refreshAction);
        }

        refreshPanel.add(new JLabel("Data Management: "));
        refreshPanel.add(refreshButton);

        return refreshPanel;
    }

    /**
     * Creates a refresh control panel with custom text and styling
     * @param refreshAction The action to perform when refresh button is clicked
     * @param labelText The text to display before the refresh button
     * @return A JPanel containing refresh controls
     */
    public static JPanel createRefreshControlPanel(ActionListener refreshAction, String labelText) {
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(UIStyler.MAIN_FONT);
        refreshButton.setBackground(UIStyler.ACCENT_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(UIStyler.ACCENT_COLOR.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(UIStyler.ACCENT_COLOR);
            }
        });

        if (refreshAction != null) {
            refreshButton.addActionListener(refreshAction);
        }

        JLabel label = new JLabel(labelText != null ? labelText : "Data Management: ");
        label.setFont(UIStyler.MAIN_FONT);

        refreshPanel.add(label);
        refreshPanel.add(refreshButton);

        return refreshPanel;
    }

}
