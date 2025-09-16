package org.example.ui.components;

import org.example.services.SchoolService;
import org.example.ui.dialogs.SchoolDialogs;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

/**
 * Custom table components for School Management
 */
public class SchoolTableComponents {

    /**
     * Renderer for school action buttons
     */
    public static class SchoolActionRenderer extends JPanel implements TableCellRenderer {
        public SchoolActionRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            String status = table.getValueAt(row, 3).toString();
            int studentCount = 0;
            try {
                Object countObj = table.getValueAt(row, 4);
                if (countObj != null) studentCount = Integer.parseInt(countObj.toString());
            } catch (Exception ex) {
                // studentCount remains 0 if parsing fails
            }

            if ("Pending".equalsIgnoreCase(status)) {
                add(new JLabel("Approve / Reject"));
            } else if ("Approved".equalsIgnoreCase(status)) {
                if (studentCount > 0) {
                    add(new JLabel("View Appointments / Students"));
                } else {
                    add(new JLabel("View Appointments"));
                }
            } else {
                add(new JLabel("-"));
            }
            return this;
        }
    }

    /**
     * Editor for school action buttons
     */
    public static class SchoolActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final JButton approveBtn, rejectBtn, viewAppointmentsBtn, viewStudentsBtn;
        private final JFrame parent;
        private final DefaultTableModel model;
        private int row;
        private final SchoolService schoolService;
        private final SchoolDialogs schoolDialogs;
        private final Runnable refreshCallback;

        public SchoolActionEditor(JFrame parent, DefaultTableModel model, Runnable refreshCallback) {
            this.parent = parent;
            this.model = model;
            this.refreshCallback = refreshCallback;
            this.schoolService = new SchoolService();
            this.schoolDialogs = new SchoolDialogs(parent, schoolService);

            panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            approveBtn = new JButton("Approve");
            rejectBtn = new JButton("Reject");
            viewAppointmentsBtn = new JButton("View Appointments");
            viewStudentsBtn = new JButton("View Students");

            approveBtn.addActionListener(e -> handleApprove());
            rejectBtn.addActionListener(e -> handleReject());
            viewAppointmentsBtn.addActionListener(e -> handleViewAppointments());
            viewStudentsBtn.addActionListener(e -> handleViewStudents());
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            panel.removeAll();
            String status = table.getValueAt(row, 3).toString();
            int studentCount = 0;
            try {
                Object countObj = table.getValueAt(row, 4);
                if (countObj != null) studentCount = Integer.parseInt(countObj.toString());
            } catch (Exception ex) {
                // studentCount remains 0 if parsing fails
            }

            if ("Pending".equalsIgnoreCase(status)) {
                panel.add(approveBtn);
                panel.add(rejectBtn);
            } else if ("Approved".equalsIgnoreCase(status)) {
                panel.add(viewAppointmentsBtn);
                if (studentCount > 0) {
                    panel.add(viewStudentsBtn);
                }
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }

        private void handleApprove() {
            Object schoolId = model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(parent, "Approve this school?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = schoolService.approveSchool(schoolId);
                if (success) {
                    JOptionPane.showMessageDialog(parent, "School approved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    model.setValueAt("Approved", row, 3);
                    if (refreshCallback != null) refreshCallback.run();
                } else {
                    JOptionPane.showMessageDialog(parent, "Failed to approve school.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            fireEditingStopped();
        }

        private void handleReject() {
            Object schoolId = model.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(parent, "Reject this school?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = schoolService.rejectSchool(schoolId);
                if (success) {
                    JOptionPane.showMessageDialog(parent, "School rejected!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    model.setValueAt("Rejected", row, 3);
                    if (refreshCallback != null) refreshCallback.run();
                } else {
                    JOptionPane.showMessageDialog(parent, "Failed to reject school.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            fireEditingStopped();
        }

        private void handleViewAppointments() {
            Object schoolId = model.getValueAt(row, 0);
            schoolDialogs.showSchoolAppointmentsDialog(schoolId);
            fireEditingStopped();
        }

        private void handleViewStudents() {
            Object schoolId = model.getValueAt(row, 0);
            schoolDialogs.showSchoolStudentsDialog(schoolId);
            fireEditingStopped();
        }
    }
}
