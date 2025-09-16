package org.example.ui.renderers;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom table cell renderer for school status highlighting
 * Separates the rendering logic from the dialog class
 */
public class SchoolStatusTableRenderer extends DefaultTableCellRenderer {

    // Color constants for different status types
    private static final Color APPROVED_COLOR = new Color(230, 255, 230);
    private static final Color PENDING_COLOR = new Color(255, 250, 205);
    private static final Color REJECTED_COLOR = new Color(255, 230, 230);
    private static final Color APPROVED_TEXT_COLOR = new Color(0, 120, 0);
    private static final Color PENDING_TEXT_COLOR = new Color(200, 120, 0);
    private static final Color REJECTED_TEXT_COLOR = new Color(150, 0, 0);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        String status = getStatusFromRow(table, row);

        if (!isSelected) {
            applyStatusStyling(c, status, column);
        } else {
            c.setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        }

        return c;
    }

    /**
     * Extract status from table row (assumes status is in column 5)
     */
    private String getStatusFromRow(JTable table, int row) {
        Object statusObj = table.getValueAt(row, 5);
        return statusObj != null ? statusObj.toString() : "";
    }

    /**
     * Apply color and font styling based on status
     */
    private void applyStatusStyling(Component component, String status, int column) {
        switch (status.toLowerCase()) {
            case "approved":
                component.setBackground(APPROVED_COLOR);
                if (column == 5) {
                    setForeground(APPROVED_TEXT_COLOR);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(Color.BLACK);
                }
                break;

            case "pending":
                component.setBackground(PENDING_COLOR);
                if (column == 5) {
                    setForeground(PENDING_TEXT_COLOR);
                } else {
                    setForeground(Color.BLACK);
                }
                break;

            case "rejected":
                component.setBackground(REJECTED_COLOR);
                if (column == 5) {
                    setForeground(REJECTED_TEXT_COLOR);
                } else {
                    setForeground(Color.BLACK);
                }
                break;

            default:
                component.setBackground(Color.WHITE);
                setForeground(Color.BLACK);
                break;
        }
    }
}
