package org.example.ui.panels;


import org.example.services.BookingServices;
import org.example.utils.UIStyler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BookingOverviewPanel extends JPanel {
    private DefaultTableModel bookingModel;
    private BookingServices bookingServices  = new BookingServices();;
    private JTable table;

    public BookingOverviewPanel() {
        initializeComponents();


    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel contentPanel = new JPanel(new BorderLayout());

        String[] columns = {"Booking ID", "School", "Hospital", "Department", "Date", "Time", "Students", "Status"};
        Object[][] data = bookingServices.fetchBookingOverview();
        if (data == null || data.length == 0) {
            data = new Object[][] {{"-", "No data available", "-", "-", "-", "-", "-", "-"}};
        }

        bookingModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = UIStyler.createStyledTable(data, columns);
        table.setModel(bookingModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add title label
        JLabel titleLabel = UIStyler.createStyledLabel("Booking Overview", UIStyler.TITLE_FONT, UIStyler.TEXT_COLOR);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Add table to center
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewStudentsButton = new JButton("View Students");
        viewStudentsButton.addActionListener(e -> viewStudentsForBooking());

        JButton approveButton = new JButton("Approve Booking");
        approveButton.addActionListener(e -> approveSelectedBooking());

        buttonPanel.add(viewStudentsButton);
        buttonPanel.add(approveButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }


    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            try {
                bookingModel.setRowCount(0);
                Object[][] refreshedData = bookingServices.fetchBookingOverview();
                if (refreshedData == null || refreshedData.length == 0) {
                    bookingModel.addRow(new Object[]{
                            "-", "No data available", "-", "-", "-", "-", "-", "-"
                    });
                }

                for (Object[] row : refreshedData) {
                    bookingModel.addRow(row);
                }

                table.revalidate();
                table.repaint();
                System.out.println("Booking overview data refreshed successfully");
            } catch (Exception e) {
                System.err.println("Error refreshing booking data: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    public void approveSelectedBooking() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a booking to approve.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the Booking ID from the table (first column)
        Object bookingIdObj = bookingModel.getValueAt(selectedRow, 0);

        if (bookingIdObj == null || "-".equals(bookingIdObj.toString())) {
            JOptionPane.showMessageDialog(this,
                    "Invalid booking selected.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingId = Integer.parseInt(bookingIdObj.toString());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to approve booking ID " + bookingId + "?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = bookingServices.approveBooking(bookingId); // <-- Call your service
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Booking approved successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshData(); // reload updated bookings
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to approve booking.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error approving booking: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    public void viewStudentsForBooking() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a booking to view students.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object bookingIdObj = bookingModel.getValueAt(selectedRow, 0);

        if (bookingIdObj == null || "-".equals(bookingIdObj.toString())) {
            JOptionPane.showMessageDialog(this,
                    "Invalid booking selected.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingId;
        try {
            bookingId = Integer.parseInt(bookingIdObj.toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Booking ID is not valid.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fetch students for the booking using HospitalService
        Object[][] students = bookingServices.fetchStudentsByBooking(bookingId);

        if (students == null || students.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "No students found for this booking.",
                    "No Data",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Prepare table to show students
        String[] columns = {"Student ID", "First Name", "Last Name", "Email", "School"};
        DefaultTableModel model = new DefaultTableModel(students, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable studentTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        JOptionPane.showMessageDialog(this, scrollPane, "Students in Booking ID " + bookingId,
                JOptionPane.INFORMATION_MESSAGE);
    }




}
