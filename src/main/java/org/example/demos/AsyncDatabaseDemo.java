package org.example.demos;

import org.example.DatabaseConnection;
import org.example.utils.DatabaseWorker;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Example demonstrating how to use DatabaseWorker to prevent UI freezing
 * when loading data into a JTable from long-running database queries.
 */
public class AsyncDatabaseDemo extends JFrame {

    private JTable dataTable;
    private JButton loadDataButton;
    private JButton refreshButton;
    private JButton performOpButton; // New button for database operations
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public AsyncDatabaseDemo() {
        setTitle("Async Database Demo - No UI Freezing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        // Create table with loading message
        dataTable = new JTable();
        DefaultTableModel emptyModel = new DefaultTableModel(
            new String[]{"Loading...", "Please", "Wait"}, 0
        );
        dataTable.setModel(emptyModel);

        // Create buttons
        loadDataButton = new JButton("Load User Data");
        refreshButton = new JButton("Refresh Data");
        performOpButton = new JButton("Perform Database Operation"); // New button

        // Create progress bar and status label
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");

        statusLabel = new JLabel("Ready to load data");

        // Add action listeners
        loadDataButton.addActionListener(e -> loadUserData());
        refreshButton.addActionListener(e -> refreshData());
        performOpButton.addActionListener(e -> performDatabaseOperation()); // Connect the method
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(loadDataButton);
        topPanel.add(refreshButton);
        topPanel.add(performOpButton); // Add the new button
        add(topPanel, BorderLayout.NORTH);

        // Center panel with table
        add(new JScrollPane(dataTable), BorderLayout.CENTER);

        // Bottom panel with progress bar and status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Example 1: Load user data using DatabaseWorker with full control
     */
    private void loadUserData() {
        String[] columnNames = {"ID", "Name", "Email", "Role", "Created Date"};

        DatabaseWorker.executeForTable(
            // Background task - this runs on a background thread
            () -> {
                List<Object[]> data = new ArrayList<>();

                // Simulate a long-running query
                try (Connection conn = DatabaseConnection.Connect()) {
                    if (conn != null) {
                        // This could be any slow query - SELECT * FROM users, complex joins, etc.
                        String sql = "SELECT id, name, email, role, created_date FROM users ORDER BY created_date DESC";

                        try (PreparedStatement stmt = conn.prepareStatement(sql);
                             ResultSet rs = stmt.executeQuery()) {

                            while (rs.next()) {
                                Object[] row = {
                                    rs.getInt("id"),
                                    rs.getString("name"),
                                    rs.getString("email"),
                                    rs.getString("role"),
                                    rs.getTimestamp("created_date")
                                };
                                data.add(row);

                                // Simulate processing time for demonstration
                                Thread.sleep(10);
                            }
                        }
                    }
                } catch (SQLException | InterruptedException e) {
                    throw new RuntimeException("Failed to load user data", e);
                }

                return data;
            },

            // Table and column configuration
            dataTable,
            columnNames,

            // Error handling
            exception -> {
                JOptionPane.showMessageDialog(this,
                    "Failed to load data: " + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Error loading data");
            },

            // On start
            () -> {
                loadDataButton.setEnabled(false);
                refreshButton.setEnabled(false);
                progressBar.setIndeterminate(true);
                progressBar.setString("Loading user data...");
                statusLabel.setText("Querying database...");
            },

            // On finish
            () -> {
                loadDataButton.setEnabled(true);
                refreshButton.setEnabled(true);
                progressBar.setIndeterminate(false);
                progressBar.setString("Complete");
                statusLabel.setText("Data loaded successfully - " + dataTable.getRowCount() + " records");
            }
        );
    }

    /**
     * Example 2: Simple refresh using the simplified method
     */
    private void refreshData() {
        String[] columnNames = {"ID", "Status", "Last Updated"};

        // Using the simplified version for quick operations
        DatabaseWorker.executeForTable(
            () -> {
                List<Object[]> data = new ArrayList<>();

                try (Connection conn = DatabaseConnection.Connect()) {
                    if (conn != null) {
                        String sql = "SELECT id, status, last_updated FROM system_status";

                        try (PreparedStatement stmt = conn.prepareStatement(sql);
                             ResultSet rs = stmt.executeQuery()) {

                            while (rs.next()) {
                                Object[] row = {
                                    rs.getInt("id"),
                                    rs.getString("status"),
                                    rs.getTimestamp("last_updated")
                                };
                                data.add(row);
                            }
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to refresh data", e);
                }

                return data;
            },
            dataTable,
            columnNames
        );
    }

    /**
     * Example 3: Using DatabaseWorker for non-table operations
     *
     * This method demonstrates how to use DatabaseWorker for database operations that:
     * - Don't involve JTable updates (INSERT, UPDATE, DELETE, stored procedures)
     * - Need to run in background to prevent UI freezing
     * - Require proper error handling and user feedback
     *
     * Use cases:
     * - Creating/updating records
     * - Executing stored procedures
     * - Batch operations
     * - Database maintenance tasks
     * - Complex business logic operations
     */
    private void performDatabaseOperation() {
        DatabaseWorker.execute(
            // Background task - runs on background thread
            () -> {
                // Simulate various database operations
                try (Connection conn = DatabaseConnection.Connect()) {
                    if (conn == null) {
                        throw new SQLException("Could not establish database connection");
                    }

                    // Example 1: Insert operation
                    String insertSql = "INSERT INTO activity_log (action, timestamp, details) VALUES (?, NOW(), ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                        stmt.setString(1, "DEMO_OPERATION");
                        stmt.setString(2, "Demo database operation executed via DatabaseWorker");
                        int rowsAffected = stmt.executeUpdate();

                        if (rowsAffected > 0) {
                            // Simulate some processing time
                            Thread.sleep(1000);
                            return "Successfully inserted activity log. Rows affected: " + rowsAffected;
                        } else {
                            return "No rows were affected by the operation";
                        }
                    }

                } catch (SQLException e) {
                    throw new RuntimeException("Database operation failed: " + e.getMessage(), e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Operation was interrupted", e);
                }
            },

            // Success callback - runs on EDT
            result -> {
                JOptionPane.showMessageDialog(this,
                    result,
                    "Operation Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Database operation completed successfully");
            },

            // Error callback - runs on EDT
            exception -> {
                JOptionPane.showMessageDialog(this,
                    "Operation failed: " + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Database operation failed");
            },

            // On start - runs on EDT
            () -> {
                performOpButton.setEnabled(false);
                progressBar.setIndeterminate(true);
                progressBar.setString("Executing operation...");
                statusLabel.setText("Performing database operation...");
            },

            // On finish - runs on EDT
            () -> {
                performOpButton.setEnabled(true);
                progressBar.setIndeterminate(false);
                progressBar.setString("Ready");
            }
        );
    }

    /**
     * Alternative example: Batch operation
     */
    private void performBatchOperation() {
        DatabaseWorker.executeVoid(
            // Background task
            () -> {
                try (Connection conn = DatabaseConnection.Connect()) {
                    if (conn != null) {
                        conn.setAutoCommit(false); // Start transaction

                        String sql = "UPDATE user_status SET last_seen = NOW() WHERE active = 1";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.executeUpdate();
                        }

                        conn.commit(); // Commit transaction
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Batch operation failed", e);
                }
            },

            // Success callback
            () -> {
                JOptionPane.showMessageDialog(this, "Batch operation completed!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            },

            // Error callback
            exception -> {
                JOptionPane.showMessageDialog(this, "Batch operation failed: " + exception.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new AsyncDatabaseDemo().setVisible(true);
        });
    }
}
