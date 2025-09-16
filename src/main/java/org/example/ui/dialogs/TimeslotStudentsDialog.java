package org.example.ui.dialogs;

import org.example.services.HospitalService;
import org.example.utils.UIStyler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Dialog to show students who booked a specific timeslot
 */
public class TimeslotStudentsDialog extends JDialog {
    private final HospitalService hospitalService;
    private final int timeSlotId;
    private DefaultTableModel tableModel;
    private JTable studentsTable;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    public TimeslotStudentsDialog(JFrame parent, int timeSlotId) {
        super(parent, "Students for Timeslot", true);
        this.timeSlotId = timeSlotId;
        this.hospitalService = new HospitalService();

        initializeComponents();
        setupLayout();
        loadStudentsData();

        setSize(900, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        // Create table columns
        String[] columns = {
            "Student ID", "First Name", "Last Name", "Email",
            "School", "Booking Date", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        studentsTable = UIStyler.createStyledTable(new Object[0][0], columns);
        studentsTable.setModel(tableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add mouse listener for double-click actions (optional)
        studentsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && studentsTable.getSelectedRow() != -1) {
                    showStudentDetails();
                }
            }
        });

        // Status components
        statusLabel = new JLabel("Loading students...");
        statusLabel.setFont(UIStyler.MAIN_FONT);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header panel with timeslot details
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content with table
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Booked Students"));
        add(scrollPane, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalStrut(10));
        statusPanel.add(progressBar);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        headerPanel.setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = UIStyler.createStyledLabel(
            "Timeslot Bookings",
            UIStyler.TITLE_FONT,
            UIStyler.TEXT_COLOR
        );

        // Timeslot details (will be loaded)
        JLabel detailsLabel = new JLabel("Loading timeslot details...");
        detailsLabel.setFont(UIStyler.MAIN_FONT);
        detailsLabel.setForeground(UIStyler.TEXT_COLOR.brighter());

        // Load timeslot details in background
        Thread thread = new Thread(() -> {
            String details = hospitalService.getTimeslotDetails(timeSlotId);
            SwingUtilities.invokeLater(() -> {
                detailsLabel.setText(details);
            });
        });
        thread.setDaemon(true);
        thread.start();

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(245, 245, 245));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(detailsLabel);

        headerPanel.add(textPanel, BorderLayout.WEST);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setFont(UIStyler.MAIN_FONT);
        closeButton.addActionListener(e -> dispose());
        headerPanel.add(closeButton, BorderLayout.EAST);

        return headerPanel;
    }

    private void loadStudentsData() {
        // Show loading state
        progressBar.setVisible(true);
        statusLabel.setText("Loading students...");
        statusLabel.setForeground(Color.BLUE);

        // Use threading to prevent UI freezing
        hospitalService.fetchStudentsByTimeslotAsync(
            timeSlotId,

            // On success
            data -> {
                updateTable(data);
                int studentCount = data.length;
                statusLabel.setText(studentCount + " student(s) found for this timeslot");
                statusLabel.setForeground(new Color(0, 128, 0));

                if (studentCount == 0) {
                    statusLabel.setText("No students have booked this timeslot yet");
                    statusLabel.setForeground(Color.GRAY);
                }
            },

            // On error
            exception -> {
                JOptionPane.showMessageDialog(this,
                    "Failed to load students: " + exception.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText("Error loading students");
                statusLabel.setForeground(Color.RED);
            },

            // On start
            () -> {
                progressBar.setVisible(true);
                statusLabel.setText("Loading students...");
            },

            // On finish
            () -> {
                progressBar.setVisible(false);
            }
        );
    }

    private void updateTable(Object[][] data) {
        // Clear existing data
        tableModel.setRowCount(0);

        // Add new data
        for (Object[] row : data) {
            tableModel.addRow(row);
        }

        // Auto-resize columns
        studentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Refresh table display
        studentsTable.revalidate();
        studentsTable.repaint();
    }

    private void showStudentDetails() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) return;

        // Get student information
        Object studentId = studentsTable.getValueAt(selectedRow, 0);
        Object firstName = studentsTable.getValueAt(selectedRow, 1);
        Object lastName = studentsTable.getValueAt(selectedRow, 2);
        Object email = studentsTable.getValueAt(selectedRow, 3);
        Object school = studentsTable.getValueAt(selectedRow, 4);
        Object bookingDate = studentsTable.getValueAt(selectedRow, 5);
        Object status = studentsTable.getValueAt(selectedRow, 6);

        // Create details message
        String details = String.format(
            "Student Details:\n\n" +
            "ID: %s\n" +
            "Name: %s %s\n" +
            "Email: %s\n" +
            "School: %s\n" +
            "Booking Date: %s\n" +
            "Status: %s",
            studentId, firstName, lastName, email, school, bookingDate, status
        );

        JOptionPane.showMessageDialog(this, details, "Student Information",
                                    JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Refresh the students data
     */
    public void refreshData() {
        loadStudentsData();
    }
}
