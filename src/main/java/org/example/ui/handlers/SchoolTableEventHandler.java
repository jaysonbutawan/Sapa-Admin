package org.example.ui.handlers;

import org.example.services.SchoolService;
import org.example.ui.dialogs.SchoolDialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Handles table interactions for school-related tables
 * Separates event handling logic from dialog classes
 */
public class SchoolTableEventHandler {
    private final SchoolService schoolService;
    private final SchoolDialogs schoolDialogs;

    public SchoolTableEventHandler(SchoolService schoolService, SchoolDialogs schoolDialogs) {
        this.schoolService = schoolService;
        this.schoolDialogs = schoolDialogs;
    }

    /**
     * Add mouse listeners to the table for interaction
     */
    public void addTableListeners(JTable table) {
        table.addMouseListener(createMouseClickListener(table));
        table.addMouseMotionListener(createMouseMotionListener(table));
    }

    /**
     * Creates mouse click listener for double-click events
     */
    private MouseAdapter createMouseClickListener(JTable table) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleDoubleClick(table);
                }
            }
        };
    }

    /**
     * Creates mouse motion listener for cursor changes
     */
    private MouseMotionAdapter createMouseMotionListener(JTable table) {
        return new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateCursorBasedOnRow(table, e);
            }
        };
    }

    /**
     * Handles double-click events on table rows
     */
    private void handleDoubleClick(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        Object schoolId = table.getValueAt(selectedRow, 0);
        String status = getStatusFromRow(table, selectedRow);

        if ("approved".equalsIgnoreCase(status)) {
            handleApprovedSchoolClick(schoolId, table);
        } else {
            showNonApprovedSchoolMessage(status, table);
        }
    }

    /**
     * Handles click on approved school - shows students if available
     */
    private void handleApprovedSchoolClick(Object schoolId, JTable table) {
        Object[][] studentData = schoolService.fetchStudentsForSchool(schoolId);
        if (studentData.length > 0) {
            schoolDialogs.showSchoolStudentsDialog(schoolId);
        } else {
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(table),
                "This approved school has no students added yet.",
                "No Students Found",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Shows message for non-approved schools
     */
    private void showNonApprovedSchoolMessage(String status, JTable table) {
        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(table),
            "Only approved schools can be viewed for student details.\nThis school's status: " + status,
            "School Not Approved",
            JOptionPane.WARNING_MESSAGE
        );
    }

    /**
     * Updates cursor based on row status
     */
    private void updateCursorBasedOnRow(JTable table, MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        if (row >= 0) {
            String status = getStatusFromRow(table, row);
            if ("approved".equalsIgnoreCase(status)) {
                table.setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
                table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    /**
     * Extract status from table row (assumes status is in column 5)
     */
    private String getStatusFromRow(JTable table, int row) {
        Object statusObj = table.getValueAt(row, 5);
        return statusObj != null ? statusObj.toString() : "";
    }
}
