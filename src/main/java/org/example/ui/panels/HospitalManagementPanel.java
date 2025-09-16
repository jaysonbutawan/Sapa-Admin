package org.example.ui.panels;

import org.example.services.HospitalService;
import org.example.models.HospitalItem;
import org.example.models.DepartmentItem;
import org.example.models.SlotDateItem;
import org.example.ui.dialogs.DepartmentDrillDownDialog;
import org.example.utils.UIStyler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Panel for managing hospitals, departments, dates, and time slots
 */
public class HospitalManagementPanel extends JPanel  {
    private final JFrame parentFrame;
    private final HospitalService hospitalService;
    private final DepartmentDrillDownDialog drillDownDialog;

    // Table models for refresh functionality
    private DefaultTableModel hospitalModel;
    private DefaultTableModel departmentModel;
    private DefaultTableModel dateModel;
    private DefaultTableModel timeSlotModel;

    // Combo box references for refresh functionality
    private JComboBox<HospitalItem> hospitalCombo;
    private JComboBox<DepartmentItem> departmentCombo;
    private JComboBox<SlotDateItem> slotDateCombo;

    public HospitalManagementPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.hospitalService = new HospitalService();
        this.drillDownDialog = new DepartmentDrillDownDialog(parentFrame, hospitalService);

        initializeComponents();

    }

    private void initializeComponents() {
        setLayout(new BorderLayout());


        JTabbedPane hospitalTabs = new JTabbedPane();
        hospitalTabs.setFont(UIStyler.MAIN_FONT);

        hospitalTabs.addTab("Hospitals", createHospitalsPanel());
        hospitalTabs.addTab("Departments", createDepartmentsPanel());
        hospitalTabs.addTab("Available Dates", createAvailableDatesPanel());
        hospitalTabs.addTab("Time Slots", createTimeSlotsPanel());

        add(hospitalTabs, BorderLayout.CENTER);
    }

    private JPanel createHospitalsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add Hospital Form
        JPanel addHospitalPanel = createAddHospitalForm();

        // Hospital List Table
        Object[][] hospitalData = hospitalService.fetchHospitals();
        String[] hospitalColumns = {"ID", "Hospital Name", "Address", "Contact", "Description", "Actions"};

        hospitalModel = new DefaultTableModel(hospitalData, hospitalColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column is editable
            }
        };

        JTable hospitalTable = UIStyler.createStyledTable(hospitalData, hospitalColumns);
        hospitalTable.setModel(hospitalModel);

        JScrollPane hospitalScrollPane = new JScrollPane(hospitalTable);

        // Split pane for form and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, addHospitalPanel, hospitalScrollPane);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.3);

        panel.add(UIStyler.createStyledLabel("Hospital Management", UIStyler.TITLE_FONT, UIStyler.TEXT_COLOR), BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAddHospitalForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Hospital"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Hospital Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Hospital Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField hospitalNameField = new JTextField(20);
        formPanel.add(hospitalNameField, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField addressField = new JTextField(20);
        formPanel.add(addressField, gbc);

        // Contact Info
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Contact Info:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField contactField = new JTextField(20);
        formPanel.add(contactField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descScrollPane, gbc);

        // Add Button
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JButton addHospitalBtn = new JButton("Add Hospital");
        UIStyler.styleButton(addHospitalBtn);
        formPanel.add(addHospitalBtn, gbc);

        addHospitalBtn.addActionListener(e -> {
            String name = hospitalNameField.getText().trim();
            String address = addressField.getText().trim();
            String contact = contactField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (name.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "Hospital name and address are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (hospitalService.addHospital(name, address, contact, description)) {
                JOptionPane.showMessageDialog(parentFrame, "Hospital added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                hospitalNameField.setText("");
                addressField.setText("");
                contactField.setText("");
                descriptionArea.setText("");

            }
        });

        return formPanel;
    }

    private JPanel createDepartmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add Department Form
        JPanel addDeptPanel = createAddDepartmentForm();

        // Department List Table
        Object[][] deptData = hospitalService.fetchDepartments();
        String[] deptColumns = {"ID", "Hospital", "Department Name", "Price per Student", "Actions"};

        departmentModel = new DefaultTableModel(deptData, deptColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only Actions column is editable
            }
        };

        JTable deptTable = UIStyler.createStyledTable(deptData, deptColumns);
        deptTable.setModel(departmentModel);

        // Add double-click listener to show schools for selected department
        deptTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && deptTable.getSelectedRow() != -1) {
                    int row = deptTable.getSelectedRow();
                    int departmentId = (Integer) deptTable.getValueAt(row, 0);
                    showSchoolsByDepartment(departmentId);
                }
            }
        });

        JScrollPane deptScrollPane = new JScrollPane(deptTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, addDeptPanel, deptScrollPane);
        splitPane.setDividerLocation(150);
        splitPane.setResizeWeight(0.25);

        // Create header panel with instructions
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = UIStyler.createStyledLabel("Department Management", UIStyler.TITLE_FONT, UIStyler.TEXT_COLOR);
        JLabel instructionLabel = new JLabel("💡 Double-click on a department to view schools with bookings");
        instructionLabel.setFont(UIStyler.MAIN_FONT);
        instructionLabel.setForeground(UIStyler.SECONDARY_COLOR);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(instructionLabel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAddDepartmentForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Department"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Hospital Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Hospital:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hospitalCombo = new JComboBox<>();
        hospitalService.loadHospitalsCombo(hospitalCombo);
        formPanel.add(hospitalCombo, gbc);

        // Department Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Department Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField deptNameField = new JTextField(20);
        formPanel.add(deptNameField, gbc);

        // Price per Student
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Price per Student:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField priceField = new JTextField(20);
        priceField.setText("0.00");
        formPanel.add(priceField, gbc);

        // Add Button
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JButton addDeptBtn = new JButton("Add Department");
        UIStyler.styleButton(addDeptBtn);
        formPanel.add(addDeptBtn, gbc);

        addDeptBtn.addActionListener(e -> {
            HospitalItem selectedHospital = (HospitalItem) hospitalCombo.getSelectedItem();
            String deptName = deptNameField.getText().trim();
            String priceText = priceField.getText().trim();

            if (selectedHospital == null || deptName.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "Please select a hospital and enter department name!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                if (hospitalService.addDepartment(selectedHospital.getId(), deptName, price)) {
                    JOptionPane.showMessageDialog(parentFrame, "Department added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    deptNameField.setText("");
                    priceField.setText("0.00");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentFrame, "Please enter a valid price!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return formPanel;
    }

    private JPanel createAvailableDatesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add Date Form
        JPanel addDatePanel = createAddDateForm();

        // Dates List Table
        Object[][] dateData = hospitalService.fetchAvailableDates();
        String[] dateColumns = {"ID", "Hospital", "Department", "Available Date", "Actions"};

        dateModel = new DefaultTableModel(dateData, dateColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only Actions column is editable
            }
        };

        JTable dateTable = UIStyler.createStyledTable(dateData, dateColumns);
        dateTable.setModel(dateModel);

        JScrollPane dateScrollPane = new JScrollPane(dateTable);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, addDatePanel, dateScrollPane);
        splitPane.setDividerLocation(150);
        splitPane.setResizeWeight(0.25);

        panel.add(UIStyler.createStyledLabel("Available Dates Management", UIStyler.TITLE_FONT, UIStyler.TEXT_COLOR), BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAddDateForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Available Date"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Department Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        departmentCombo = new JComboBox<>();
        hospitalService.loadDepartmentsCombo(departmentCombo);
        formPanel.add(departmentCombo, gbc);

        // Available Date
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Available Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField dateField = new JTextField(20);
        dateField.setToolTipText("Format: YYYY-MM-DD");
        formPanel.add(dateField, gbc);

        // Add Button
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JButton addDateBtn = new JButton("Add Date");
        UIStyler.styleButton(addDateBtn);
        formPanel.add(addDateBtn, gbc);

        addDateBtn.addActionListener(e -> {
            DepartmentItem selectedDept = (DepartmentItem) departmentCombo.getSelectedItem();
            String dateText = dateField.getText().trim();

            if (selectedDept == null || dateText.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "Please select a department and enter a date!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (hospitalService.addAvailableDate(selectedDept.getId(), dateText)) {
                JOptionPane.showMessageDialog(parentFrame, "Available date added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dateField.setText("");
            }
        });

        return formPanel;
    }

    private JPanel createTimeSlotsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Add Time Slot Form
        JPanel addTimeSlotPanel = createAddTimeSlotForm();

        // Time Slots List Table
        Object[][] timeSlotData = hospitalService.fetchTimeSlots();
        String[] timeSlotColumns = {"ID", "Hospital", "Department", "Date", "Start Time", "End Time", "Capacity", "Actions"};

        timeSlotModel = new DefaultTableModel(timeSlotData, timeSlotColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions column is editable
            }
        };

        JTable timeSlotTable = UIStyler.createStyledTable(timeSlotData, timeSlotColumns);
        timeSlotTable.setModel(timeSlotModel);

        // Add mouse listener to show students when clicking on a timeslot row
        timeSlotTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = timeSlotTable.getSelectedRow();
                if (row != -1) {
                    // Get the timeslot ID from the first column
                    Object timeSlotIdObj = timeSlotTable.getValueAt(row, 0);
                    if (timeSlotIdObj != null) {
                        try {
                            int timeSlotId = Integer.parseInt(timeSlotIdObj.toString());

                            // Single click shows info tooltip, double click opens dialog
                            if (e.getClickCount() == 1) {
                                showTimeslotTooltip(timeSlotTable, e, timeSlotId);
                            } else if (e.getClickCount() == 2) {
                                showTimeslotStudents(timeSlotId);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(parentFrame,
                                "Invalid timeslot ID: " + timeSlotIdObj,
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        JScrollPane timeSlotScrollPane = new JScrollPane(timeSlotTable);

        // Add instruction label for users
        JLabel instructionLabel = new JLabel("💡 Click on a timeslot to see booking info, double-click to view students");
        instructionLabel.setFont(UIStyler.MAIN_FONT);
        instructionLabel.setForeground(UIStyler.TEXT_COLOR.brighter());
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(instructionLabel, BorderLayout.NORTH);
        tablePanel.add(timeSlotScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, addTimeSlotPanel, tablePanel);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.3);

        panel.add(UIStyler.createStyledLabel("Time Slots Management", UIStyler.TITLE_FONT, UIStyler.TEXT_COLOR), BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAddTimeSlotForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Time Slot"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Slot Date Selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Available Date:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        slotDateCombo = new JComboBox<>();
        hospitalService.loadSlotDatesCombo(slotDateCombo);
        formPanel.add(slotDateCombo, gbc);

        // Start Time
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField startTimeField = new JTextField(20);
        startTimeField.setToolTipText("Format: HH:MM (e.g., 09:00)");
        formPanel.add(startTimeField, gbc);

        // End Time
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField endTimeField = new JTextField(20);
        endTimeField.setToolTipText("Format: HH:MM (e.g., 17:00)");
        formPanel.add(endTimeField, gbc);

        // Capacity
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField capacityField = new JTextField(20);
        capacityField.setText("50");
        capacityField.setToolTipText("Maximum number of students for this time slot");
        formPanel.add(capacityField, gbc);

        // Add Button
        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JButton addTimeSlotBtn = new JButton("Add Time Slot");
        UIStyler.styleButton(addTimeSlotBtn);
        formPanel.add(addTimeSlotBtn, gbc);

        addTimeSlotBtn.addActionListener(e -> {
            SlotDateItem selectedSlotDate = (SlotDateItem) slotDateCombo.getSelectedItem();
            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();
            String capacityText = capacityField.getText().trim();

            if (selectedSlotDate == null || startTime.isEmpty() || endTime.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame,
                    "Please select a date and enter start/end times!",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int capacity = Integer.parseInt(capacityText);
                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(parentFrame,
                        "Capacity must be a positive number!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Disable button to prevent multiple submissions
                addTimeSlotBtn.setEnabled(false);
                addTimeSlotBtn.setText("Adding...");

                // Use threaded operation to prevent UI freezing
                Thread thread = new Thread(() -> {
                    boolean success = hospitalService.addTimeSlot(selectedSlotDate.getId(), startTime, endTime, capacity);

                    // Update UI on EDT
                    SwingUtilities.invokeLater(() -> {
                        addTimeSlotBtn.setEnabled(true);
                        addTimeSlotBtn.setText("Add Time Slot");

                        if (success) {
                            JOptionPane.showMessageDialog(parentFrame,
                                "Time slot added successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                            startTimeField.setText("");
                            endTimeField.setText("");
                            capacityField.setText("50");

                            // Refresh the time slots table
                            refreshTimeSlotTable();
                        } else {
                            JOptionPane.showMessageDialog(parentFrame,
                                "Failed to add time slot. Please check the data and try again.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                });
                thread.start();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentFrame,
                    "Please enter a valid capacity number!",
                    "Error", JOptionPane.ERROR_MESSAGE);
                addTimeSlotBtn.setEnabled(true);
                addTimeSlotBtn.setText("Add Time Slot");
            }
        });

        return formPanel;
    }

    /**
     * Show a tooltip with basic timeslot information
     */
    private void showTimeslotTooltip(JTable table, MouseEvent e, int timeSlotId) {
        // Create a simple popup with timeslot info
        Point point = e.getPoint();
        int row = table.getSelectedRow();

        String hospital = table.getValueAt(row, 1).toString();
        String department = table.getValueAt(row, 2).toString();
        String date = table.getValueAt(row, 3).toString();
        String startTime = table.getValueAt(row, 4).toString();
        String endTime = table.getValueAt(row, 5).toString();
        String capacity = table.getValueAt(row, 6).toString();

        String tooltip = String.format(
            "<html><b>Timeslot %d</b><br/>" +
            "Hospital: %s<br/>" +
            "Department: %s<br/>" +
            "Date: %s<br/>" +
            "Time: %s - %s<br/>" +
            "Capacity: %s<br/>" +
            "<i>Double-click to view booked students</i></html>",
            timeSlotId, hospital, department, date, startTime, endTime, capacity
        );

        table.setToolTipText(tooltip);
    }

    /**
     * Open dialog to show students who booked the specific timeslot
     */
    private void showTimeslotStudents(int timeSlotId) {
        // Create and show the timeslot students dialog
        org.example.ui.dialogs.TimeslotStudentsDialog dialog =
            new org.example.ui.dialogs.TimeslotStudentsDialog(parentFrame, timeSlotId);
        dialog.setVisible(true);
    }


    private void showSchoolsByDepartment(int departmentId) {
        drillDownDialog.showSchoolsByDepartment(departmentId);
    }

    /**
     * Refresh all data in the Hospital Management Panel
     * This includes all tables (hospitals, departments, dates, time slots) and combo boxes
     */
    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Refresh hospital table
                if (hospitalModel != null) {
                    Object[][] hospitalData = hospitalService.fetchHospitals();
                    hospitalModel.setDataVector(hospitalData, new String[]{"ID", "Hospital Name", "Address", "Contact", "Description", "Actions"});
                    hospitalModel.fireTableDataChanged();
                }

                // Refresh department table
                if (departmentModel != null) {
                    Object[][] deptData = hospitalService.fetchDepartments();
                    departmentModel.setDataVector(deptData, new String[]{"ID", "Hospital", "Department Name", "Price per Student", "Actions"});
                    departmentModel.fireTableDataChanged();
                }

                // Refresh available dates table
                if (dateModel != null) {
                    Object[][] dateData = hospitalService.fetchAvailableDates();
                    dateModel.setDataVector(dateData, new String[]{"ID", "Hospital", "Department", "Available Date", "Actions"});
                    dateModel.fireTableDataChanged();
                }

                // Refresh time slots table
                if (timeSlotModel != null) {
                    Object[][] timeSlotData = hospitalService.fetchTimeSlots();
                    timeSlotModel.setDataVector(timeSlotData, new String[]{"ID", "Hospital", "Department", "Date", "Start Time", "End Time", "Capacity", "Actions"});
                    timeSlotModel.fireTableDataChanged();
                }

                // Refresh combo boxes
                if (hospitalCombo != null) {
                    hospitalService.loadHospitalsCombo(hospitalCombo);
                }

                if (departmentCombo != null) {
                    hospitalService.loadDepartmentsCombo(departmentCombo);
                }

                if (slotDateCombo != null) {
                    hospitalService.loadSlotDatesCombo(slotDateCombo);
                }

                System.out.println("Hospital Management Panel data refreshed successfully");

            } catch (Exception e) {
                System.err.println("Error refreshing Hospital Management Panel data: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame,
                    "Error refreshing data: " + e.getMessage(),
                    "Refresh Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Refresh only the time slots table
     */
    private void refreshTimeSlotTable() {
        if (timeSlotModel != null) {
            Thread thread = new Thread(() -> {
                Object[][] timeSlotData = hospitalService.fetchTimeSlots();
                SwingUtilities.invokeLater(() -> {
                    timeSlotModel.setDataVector(timeSlotData,
                        new String[]{"ID", "Hospital", "Department", "Date", "Start Time", "End Time", "Capacity", "Actions"});
                    timeSlotModel.fireTableDataChanged();
                });
            });
            thread.start();
        }
    }

    /**
     * Get the system look and feel for the application
     */
    public static String getSystemLookAndFeel() {
        return UIManager.getSystemLookAndFeelClassName();
    }

    /**
     * Apply system look and feel to the application
     */
    public static void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(getSystemLookAndFeel());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
            // Fall back to cross-platform look and feel if system LAF fails
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Failed to set cross-platform look and feel: " + ex.getMessage());
            }
        }
    }

    /**
     * Generic method to perform database operations in a background thread
     * This prevents UI freezing during long-running database queries
     */
    public static void performDatabaseOperation(
            Runnable databaseOperation,
            Consumer<Boolean> onSuccess,
            Consumer<Exception> onError,
            JComponent triggerComponent) {

        // Disable the trigger component to prevent multiple operations
        if (triggerComponent != null) {
            triggerComponent.setEnabled(false);
        }

        Thread thread = new Thread(() -> {
            try {
                databaseOperation.run();
                SwingUtilities.invokeLater(() -> {
                    if (triggerComponent != null) {
                        triggerComponent.setEnabled(true);
                    }
                    if (onSuccess != null) {
                        onSuccess.accept(true);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    if (triggerComponent != null) {
                        triggerComponent.setEnabled(true);
                    }
                    if (onError != null) {
                        onError.accept(e);
                    }
                });
            }
        });
        thread.start();
    }
}
