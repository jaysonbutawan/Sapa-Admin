package org.example.ui.dialogs;

import org.example.services.HospitalService;
import org.example.utils.UIStyler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Dialog for drilling down from departments to schools to students
 */
public class DepartmentDrillDownDialog {
    private final JFrame parent;
    private final HospitalService hospitalService;

    public DepartmentDrillDownDialog(JFrame parent, HospitalService hospitalService) {
        this.parent = parent;
        this.hospitalService = hospitalService;
    }

    /**
     * Show schools that have bookings under a specific department
     */
    public void showSchoolsByDepartment(int departmentId) {
        String departmentName = hospitalService.getDepartmentName(departmentId);
        Object[][] schoolData = hospitalService.fetchSchoolsByDepartment(departmentId);

        if (schoolData.length == 0) {
            JOptionPane.showMessageDialog(parent,
                "No schools have bookings in this department yet.",
                "No Data Found",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columns = {"School ID", "School Name", "Total Bookings", "Total Students"};

        DefaultTableModel model = new DefaultTableModel(schoolData, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = UIStyler.createStyledTable(schoolData, columns);
        table.setModel(model);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add double-click listener to show students for selected school
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    int schoolId = (Integer) table.getValueAt(row, 0);
                    String schoolName = (String) table.getValueAt(row, 1);
                    showStudentsBySchoolAndDepartment(schoolId, schoolName, departmentId, departmentName);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JDialog dialog = new JDialog(parent, "Schools with Bookings - " + departmentName, true);
        dialog.setLayout(new BorderLayout());

        // Header panel with information
        JPanel headerPanel = createHeaderPanel(
            "Schools with Bookings in " + departmentName,
            "Double-click on a school to view students with bookings",
            schoolData.length + " school(s) found"
        );
        dialog.add(headerPanel, BorderLayout.NORTH);

        dialog.add(scrollPane, BorderLayout.CENTER);

        // Footer with instructions
        JPanel footerPanel = new JPanel(new FlowLayout());
        JLabel instructionLabel = new JLabel("💡 Double-click a school to see its students");
        instructionLabel.setFont(UIStyler.MAIN_FONT);
        instructionLabel.setForeground(UIStyler.SECONDARY_COLOR);
        footerPanel.add(instructionLabel);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Show students from a specific school that have bookings in a specific department
     */
    public void showStudentsBySchoolAndDepartment(int schoolId, String schoolName, int departmentId, String departmentName) {
        Object[][] studentData = hospitalService.fetchStudentsBySchoolAndDepartment(schoolId, departmentId);

        if (studentData.length == 0) {
            JOptionPane.showMessageDialog(parent,
                "No students from " + schoolName + " have bookings in " + departmentName + ".",
                "No Data Found",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columns = {
            "Student ID", "Student Name", "Email", "Booking ID",
            "Date", "Start Time", "End Time", "Status", "Booking Date"
        };

        DefaultTableModel model = new DefaultTableModel(studentData, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = UIStyler.createStyledTable(studentData, columns);
        table.setModel(model);
        table.setRowHeight(25);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Student ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Student Name
        table.getColumnModel().getColumn(2).setPreferredWidth(180); // Email
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Booking ID
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Date
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Start Time
        table.getColumnModel().getColumn(6).setPreferredWidth(80);  // End Time
        table.getColumnModel().getColumn(7).setPreferredWidth(100); // Status
        table.getColumnModel().getColumn(8).setPreferredWidth(120); // Booking Date

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        JDialog dialog = new JDialog(parent, "Students - " + schoolName + " | " + departmentName, true);
        dialog.setLayout(new BorderLayout());

        // Header panel with summary information
        int totalStudents = studentData.length;
        String summaryText = totalStudents + " student booking(s) found";

        JPanel headerPanel = createHeaderPanel(
            "Students from " + schoolName,
            "Bookings in " + departmentName,
            summaryText
        );
        dialog.add(headerPanel, BorderLayout.NORTH);

        dialog.add(scrollPane, BorderLayout.CENTER);

        // Footer with export option (could be implemented later)
        JPanel footerPanel = new JPanel(new FlowLayout());
        JButton exportButton = new JButton("📋 Export to CSV");
        exportButton.setFont(UIStyler.MAIN_FONT);
        exportButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog,
                "Export functionality will be implemented soon!",
                "Feature Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
        });
        footerPanel.add(exportButton);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Create a styled header panel for dialogs
     */
    private JPanel createHeaderPanel(String title, String subtitle, String summary) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UIStyler.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIStyler.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(UIStyler.MAIN_FONT);
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel summaryLabel = new JLabel(summary);
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        summaryLabel.setForeground(Color.YELLOW);
        summaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(summaryLabel);

        return headerPanel;
    }
}
