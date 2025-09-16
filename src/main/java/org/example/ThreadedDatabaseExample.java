package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Example demonstrating how to use Thread-based database operations
 * to prevent UI freezing when loading data into a JTable
 */
public class ThreadedDatabaseExample extends JFrame {

    private JTable dataTable;
    private JButton loadDataButton;
    private JButton refreshButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public ThreadedDatabaseExample() {
        setTitle("Threaded Database Example - No UI Freezing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        // Create table with default model
        String[] columnNames = {"ID", "Name", "Email", "Department", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        dataTable = new JTable(model);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.getTableHeader().setReorderingAllowed(false);

        // Create buttons
        loadDataButton = new JButton("Load Data (Threaded)");
        refreshButton = new JButton("Refresh Table");

        // Create progress bar and status label
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setString("Loading data...");
        progressBar.setStringPainted(true);

        statusLabel = new JLabel("Ready to load data");
        statusLabel.setForeground(Color.BLUE);

        // Add action listeners
        loadDataButton.addActionListener(this::loadDataWithThread);
        refreshButton.addActionListener(this::refreshTableData);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Top panel with buttons and progress
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(loadDataButton);
        topPanel.add(refreshButton);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(progressBar);

        // Center panel with table
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Database Records"));

        // Bottom panel with status
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(new JLabel("Status: "));
        bottomPanel.add(statusLabel);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Example method that loads data using the Thread-based approach
     * This prevents UI freezing during long database operations
     */
    private void loadDataWithThread(ActionEvent e) {
        DatabaseConnection.performDatabaseOperation(
            () -> {
                return fetchDataFromDatabase();
            },

            data -> {
                updateTableWithData(data);
                statusLabel.setText("Data loaded successfully! " + data.size() + " records found.");
                statusLabel.setForeground(Color.GREEN);
            },

            exception -> {
                JOptionPane.showMessageDialog(this,
                    "Failed to load data: " + exception.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Error loading data: " + exception.getMessage());
                statusLabel.setForeground(Color.RED);
            },

            () -> {
                loadDataButton.setEnabled(false);
                refreshButton.setEnabled(false);
                progressBar.setVisible(true);
                statusLabel.setText("Loading data from database...");
                statusLabel.setForeground(Color.BLUE);
            },

            // On finish - Hide loading state (runs on EDT)
            () -> {
                loadDataButton.setEnabled(true);
                refreshButton.setEnabled(true);
                progressBar.setVisible(false);
            }
        );
    }

    /**
     * Alternative method using DatabaseWorker (SwingWorker approach)
     */
    private void refreshTableData(ActionEvent e) {
        org.example.utils.DatabaseWorker.executeForTable(
            // Data supplier (background thread)
            this::fetchDataAsObjectArray,

            // Target table
            dataTable,

            // Column names
            new String[]{"ID", "Name", "Email", "Department", "Status"}
        );

        statusLabel.setText("Table refreshed using DatabaseWorker!");
        statusLabel.setForeground(Color.GREEN);
    }

    /**
     * Simulates a database query that might take some time
     * This method will run in a background thread
     */
    private List<DataRecord> fetchDataFromDatabase() {
        List<DataRecord> records = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.Connect();
            if (conn != null) {
                // Example query - replace with your actual query
                String sql = "SELECT id, name, email, department, status FROM users LIMIT 100";
                stmt = conn.prepareStatement(sql);
                rs = stmt.executeQuery();

                while (rs.next()) {
                    DataRecord record = new DataRecord(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("status")
                    );
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        } finally {
            // Clean up resources
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }

        // Simulate processing time (remove this in real application)
        try {
            Thread.sleep(2000); // Simulate 2-second database operation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return records;
    }

    /**
     * Converts data to Object array format for DatabaseWorker.executeForTable
     */
    private List<Object[]> fetchDataAsObjectArray() {
        List<DataRecord> records = fetchDataFromDatabase();
        List<Object[]> data = new ArrayList<>();

        for (DataRecord record : records) {
            data.add(new Object[]{
                record.getId(),
                record.getName(),
                record.getEmail(),
                record.getDepartment(),
                record.getStatus()
            });
        }

        return data;
    }

    /**
     * Updates the JTable with fetched data (runs on EDT)
     */
    private void updateTableWithData(List<DataRecord> data) {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.setRowCount(0); // Clear existing data

        for (DataRecord record : data) {
            model.addRow(new Object[]{
                record.getId(),
                record.getName(),
                record.getEmail(),
                record.getDepartment(),
                record.getStatus()
            });
        }

        // Auto-resize columns
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    /**
     * Data record class for type safety
     */
    private static class DataRecord {
        private final int id;
        private final String name;
        private final String email;
        private final String department;
        private final String status;

        public DataRecord(int id, String name, String email, String department, String status) {
            this.id = id;
            this.name = name != null ? name : "";
            this.email = email != null ? email : "";
            this.department = department != null ? department : "";
            this.status = status != null ? status : "";
        }

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public String getStatus() { return status; }
    }

    public static void main(String[] args) {
        // Use the new getSystemLookAndFeel method
        try {
            UIManager.setLookAndFeel(DatabaseConnection.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new ThreadedDatabaseExample().setVisible(true);
        });
    }
}
