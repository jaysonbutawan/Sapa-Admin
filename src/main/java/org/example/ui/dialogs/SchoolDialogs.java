package org.example.ui.dialogs;

import org.example.services.SchoolService;
import org.example.ui.factories.DialogFactory;
import org.example.ui.handlers.SchoolTableEventHandler;
import org.example.ui.renderers.SchoolStatusTableRenderer;
import org.example.utils.SchoolStatisticsCalculator;
import org.example.utils.UIStyler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Refactored dialog classes for School-related popup windows
 * Now uses separate classes for different responsibilities following clean architecture principles
 */
public class SchoolDialogs {
    private final JFrame parent;
    private final SchoolService schoolService;
    private final SchoolTableEventHandler eventHandler;

    public SchoolDialogs(JFrame parent, SchoolService schoolService) {
        this.parent = parent;
        this.schoolService = schoolService;
        this.eventHandler = new SchoolTableEventHandler(schoolService, this);
    }

    /**
     * Show appointments dialog for a specific school
     */
    public void showSchoolAppointmentsDialog(Object schoolId) {
        Object[][] data = schoolService.fetchAppointmentsForSchool(schoolId);
        String[] columns = {"Appointment ID", "Hospital", "Department", "Date", "Time", "Status"};

        JTable table = UIStyler.createStyledTable(data, columns);
        table.setRowHeight(24);

        JScrollPane scrollPane = DialogFactory.createTableScrollPane(table);
        JDialog dialog = DialogFactory.createStandardDialog(parent,
            "Appointments for School ID: " + schoolId, 700, 300);

        dialog.add(scrollPane);

        if (data.length == 0) {
            JLabel noDataLabel = DialogFactory.createNoDataLabel("No appointments for this school.");
            dialog.add(noDataLabel, BorderLayout.SOUTH);
        }

        dialog.setVisible(true);
    }

    /**
     * Show students dialog for a specific school
     */
    public void showSchoolStudentsDialog(Object schoolId) {
        Object[][] data = schoolService.fetchStudentsForSchool(schoolId);
        String[] columns = {"Student ID", "Name", "Email", "Added At"};

        JTable table = UIStyler.createStyledTable(data, columns);
        table.setRowHeight(24);

        JScrollPane scrollPane = DialogFactory.createTableScrollPane(table);
        JDialog dialog = DialogFactory.createStandardDialog(parent,
            "Students for School ID: " + schoolId, 600, 300);

        dialog.add(scrollPane);

        JLabel countLabel = DialogFactory.createCountLabel("Total students: " + data.length);
        dialog.add(countLabel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /**
     * Show user's schools dialog with enhanced interaction
     * Now uses separated components for better maintainability
     */
    public void showUserSchoolsDialog(Object userId) {
        Object[][] data = schoolService.fetchSchoolsForUser(userId);
        String[] columns = {"ID", "Code", "Name", "Address", "Contact", "Status", "Created At", "Approved At"};

        // Create table with custom model
        DefaultTableModel tableModel = createNonEditableTableModel(data, columns);
        JTable table = createStyledSchoolTable(tableModel);

        // Add event handling
        eventHandler.addTableListeners(table);

        // Create dialog with components
        JDialog dialog = DialogFactory.createStandardDialog(parent,
            "Schools Submitted by User ID: " + userId, 950, 450);

        setupDialogLayout(dialog, table, data);
        dialog.setVisible(true);
    }

    /**
     * Creates a non-editable table model
     */
    private DefaultTableModel createNonEditableTableModel(Object[][] data, String[] columns) {
        return new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /**
     * Creates a styled table for schools with custom renderer
     */
    private JTable createStyledSchoolTable(DefaultTableModel tableModel) {
        JTable table = new JTable(tableModel);
        table.setFont(UIStyler.MAIN_FONT);
        table.setRowHeight(24);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, new SchoolStatusTableRenderer());
        return table;
    }

    /**
     * Sets up the dialog layout with appropriate components
     */
    private void setupDialogLayout(JDialog dialog, JTable table, Object[][] data) {
        JScrollPane scrollPane = DialogFactory.createTableScrollPane(table);

        // Add instruction panel
        JPanel instructionPanel = DialogFactory.createInstructionPanel(
            "Double-click on approved schools (green rows) to view students");
        dialog.add(instructionPanel, BorderLayout.NORTH);

        // Add main content
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Add footer based on data availability
        addDialogFooter(dialog, data);
    }

    /**
     * Adds appropriate footer to dialog based on data
     */
    private void addDialogFooter(JDialog dialog, Object[][] data) {
        if (data.length == 0) {
            JLabel noDataLabel = DialogFactory.createNoDataLabel("No schools submitted by this user.");
            dialog.add(noDataLabel, BorderLayout.SOUTH);
        } else {
            SchoolStatisticsCalculator.SchoolStats stats = SchoolStatisticsCalculator.calculateStats(data);
            JPanel summaryPanel = DialogFactory.createSummaryPanel(stats.getFormattedSummary());
            dialog.add(summaryPanel, BorderLayout.SOUTH);
        }
    }
}
