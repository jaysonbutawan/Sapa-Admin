package org.example.ui.panels;

import org.example.services.HospitalService;
import org.example.utils.DatabaseWorker;
import org.example.utils.UIStyler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Example panel showing how to use DatabaseWorker to prevent UI freezing
 * when loading data into a JTable
 */
public class AsyncHospitalPanel extends JPanel {
    private JTable hospitalTable;
    private DefaultTableModel tableModel;
    private JButton loadDataButton;
    private JButton addHospitalButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private HospitalService hospitalService;

    // Form fields for adding hospitals
    private JTextField nameField, addressField, contactField, descriptionField;

    public AsyncHospitalPanel() {
        this.hospitalService = new HospitalService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        // Create table with column headers
        String[] columnNames = {"ID", "Name", "Address", "Contact", "Description", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column is editable
            }
        };

        hospitalTable = new JTable(tableModel);
        hospitalTable.setFont(UIStyler.MAIN_FONT);
        hospitalTable.setRowHeight(25);
        hospitalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Create buttons
        loadDataButton = new JButton("Load Hospitals");
        addHospitalButton = new JButton("Add Hospital");

        // Create progress components
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(UIStyler.MAIN_FONT);

        // Create form fields
        nameField = new JTextField(20);
        addressField = new JTextField(20);
        contactField = new JTextField(20);
        descriptionField = new JTextField(20);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel with buttons and progress
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(loadDataButton);
        topPanel.add(addHospitalButton);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(progressBar);
        topPanel.add(statusLabel);

        // Form panel for adding hospitals
        JPanel formPanel = createFormPanel();

        // Combine top controls
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.add(topPanel, BorderLayout.NORTH);
        controlsPanel.add(formPanel, BorderLayout.CENTER);

        add(controlsPanel, BorderLayout.NORTH);
        add(new JScrollPane(hospitalTable), BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add New Hospital"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Address
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 3;
        panel.add(addressField, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);

        // Description
        gbc.gridx = 2; gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 3;
        panel.add(descriptionField, gbc);

        return panel;
    }

    private void setupEventHandlers() {
        // Load data button - demonstrates async data loading
        loadDataButton.addActionListener(e -> loadHospitalsAsync());

        // Add hospital button - demonstrates async database operations
        addHospitalButton.addActionListener(e -> addHospitalAsync());
    }

    /**
     * Example 1: Loading data asynchronously with progress indication
     */
    private void loadHospitalsAsync() {
        DatabaseWorker.execute(
            // Background task - runs on background thread
            () -> {
                updateStatus("Loading hospitals...");
                return hospitalService.fetchHospitals();
            },

            // Success callback - runs on EDT
            (Object[][] data) -> {
                updateTableData(data);
                updateStatus("Loaded " + data.length + " hospitals");
            },

            // Error callback - runs on EDT
            (Exception error) -> {
                updateStatus("Failed to load hospitals: " + error.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error loading hospitals: " + error.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            },

            // Start callback - runs on EDT before background task
            () -> {
                loadDataButton.setEnabled(false);
                progressBar.setVisible(true);
            },

            // Finish callback - runs on EDT after completion
            () -> {
                loadDataButton.setEnabled(true);
                progressBar.setVisible(false);
            }
        );
    }

    /**
     * Example 2: Adding data asynchronously with form validation
     */
    private void addHospitalAsync() {
        // Validate form first (on EDT)
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String contact = contactField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Name and Address are required fields.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        DatabaseWorker.execute(
            // Background task
            () -> {
                updateStatus("Adding hospital...");
                return hospitalService.addHospital(name, address, contact, description);
            },

            // Success callback
            (Boolean success) -> {
                if (success) {
                    clearForm();
                    updateStatus("Hospital added successfully");
                    // Automatically reload data
                    loadHospitalsAsync();
                } else {
                    updateStatus("Failed to add hospital");
                }
            },

            // Error callback
            (Exception error) -> {
                updateStatus("Error adding hospital: " + error.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error adding hospital: " + error.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            },

            // Start callback
            () -> {
                addHospitalButton.setEnabled(false);
                progressBar.setVisible(true);
            },

            // Finish callback
            () -> {
                addHospitalButton.setEnabled(true);
                progressBar.setVisible(false);
            }
        );
    }

    /**
     * Example 3: Using the simplified version with default error handling
     */
    public void refreshDataSimple() {
        DatabaseWorker.execute(
            () -> hospitalService.fetchHospitals(),
            this::updateTableData
        );
    }

    /**
     * Example 4: Using the progress dialog version
     */
    public void refreshDataWithProgressDialog() {
        DatabaseWorker.executeWithProgress(
            () -> hospitalService.fetchHospitals(),
            this::updateTableData,
            error -> JOptionPane.showMessageDialog(this,
                "Error: " + error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE),
            this,
            "Loading hospital data..."
        );
    }

    private void updateTableData(Object[][] data) {
        // Clear existing data
        tableModel.setRowCount(0);

        // Add new data
        for (Object[] row : data) {
            tableModel.addRow(row);
        }

        // Refresh table
        tableModel.fireTableDataChanged();
    }

    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }

    private void clearForm() {
        nameField.setText("");
        addressField.setText("");
        contactField.setText("");
        descriptionField.setText("");
    }
}
