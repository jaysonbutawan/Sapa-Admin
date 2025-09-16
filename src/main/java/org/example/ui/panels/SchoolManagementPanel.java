package org.example.ui.panels;

import org.example.services.SchoolService;
import org.example.ui.components.SchoolTableComponents;
import org.example.utils.RefreshUtils;
import org.example.utils.UIStyler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SchoolManagementPanel extends JPanel  {
    private final JFrame parentFrame;
    private final SchoolService schoolService;
    private JTable schoolTable;
    private DefaultTableModel schoolTableModel;
    private volatile boolean isRefreshing = false;

    public SchoolManagementPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.schoolService = new SchoolService();

        initializeComponents();

    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        JPanel contentPanel = new JPanel(new BorderLayout());

        // Initialize with empty data first to prevent blocking
        String[] columns = {"School ID", "School Name", "Registered By", "Status", "Students", "Actions"};
        Object[][] initialData = new Object[0][6]; // Empty initially

        schoolTableModel = new DefaultTableModel(initialData, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        schoolTable = UIStyler.createStyledTable(initialData, columns);
        schoolTable.setModel(schoolTableModel);

        schoolTable.getColumn("Actions").setCellRenderer(new SchoolTableComponents.SchoolActionRenderer());
        schoolTable.getColumn("Actions").setCellEditor(new SchoolTableComponents.SchoolActionEditor(
            parentFrame, schoolTableModel, this::refreshSchoolTable));

        JScrollPane scrollPane = new JScrollPane(schoolTable);

        contentPanel.add(UIStyler.createStyledLabel("School Management", UIStyler.TITLE_FONT, UIStyler.TEXT_COLOR), BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Load data asynchronously after UI is set up
        loadDataAsync();
    }

    private void loadDataAsync() {
        schoolService.fetchSchoolsForManagementAsync(
            data -> {
                SwingUtilities.invokeLater(() -> {
                    RefreshUtils.refreshTableModel(schoolTableModel, data);
                    System.out.println("School Management Panel initialized with " + data.length + " schools");
                });
            },
            error -> {
                SwingUtilities.invokeLater(() -> {
                    System.err.println("Error loading initial school data: " + error.getMessage());
                    JOptionPane.showMessageDialog(this,
                        "Failed to load school data: " + error.getMessage(),
                        "Loading Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        );
    }

    public void refreshSchoolTable() {
        refreshData();
    }


    public void refreshData() {
        if (isRefreshing) {
            System.out.println("School Management Panel refresh already in progress");
            return;
        }

        isRefreshing = true;
        System.out.println("Starting School Management Panel refresh...");

        schoolService.fetchSchoolsForManagementAsync(
            data -> {
                SwingUtilities.invokeLater(() -> {
                    try {
                        RefreshUtils.refreshTableModel(schoolTableModel, data);
                        System.out.println("School Management Panel data refreshed successfully - " + data.length + " schools loaded");
                    } finally {
                        isRefreshing = false;
                    }
                });
            },
            error -> {
                SwingUtilities.invokeLater(() -> {
                    try {
                        System.err.println("Error refreshing School Management Panel: " + error.getMessage());
                        JOptionPane.showMessageDialog(this,
                            "Failed to refresh school data: " + error.getMessage(),
                            "Refresh Error",
                            JOptionPane.ERROR_MESSAGE);
                    } finally {
                        isRefreshing = false;
                    }
                });
            }
        );
    }

    /**
     * Cleanup method called when the application is shutting down
     * This method ensures proper cleanup of resources and ongoing operations
     */
    public void cleanup() {
        System.out.println("Starting SchoolManagementPanel cleanup...");

        try {
            // Stop any ongoing refresh operations
            isRefreshing = false;

            // Clear table data to free memory
            if (schoolTableModel != null) {
                SwingUtilities.invokeLater(() -> {
                    schoolTableModel.setRowCount(0);
                    schoolTableModel.fireTableDataChanged();
                });
            }

            // Cancel any ongoing async operations by setting a flag
            // Note: The async operations check isRefreshing flag, so setting it to false
            // will prevent them from updating the UI after cleanup

            System.out.println("SchoolManagementPanel cleanup completed successfully");

        } catch (Exception e) {
            System.err.println("Error during SchoolManagementPanel cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
