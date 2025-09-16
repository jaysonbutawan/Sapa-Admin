package org.example.ui.components;

import org.example.services.UserService;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;

/**
 * Custom table components for User Management
 */
public class UserTableComponents {

    /**
     * Button renderer for approve button
     */
    public static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(46, 125, 50));
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    /**
     * Button editor for approve button with proper threading
     */
    public static class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;
        private final JFrame parent;
        private final DefaultTableModel model;
        private int row;
        private final UserService userService;
        private final ArrayList<CellEditorListener> listeners;

        public ButtonEditor(JFrame parent, DefaultTableModel model) {
            this.parent = parent;
            this.model = model;
            this.userService = new UserService();
            this.listeners = new ArrayList<>();

            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(46, 125, 50));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleApprovalClick();
                }
            });
        }

        private void handleApprovalClick() {
            if (isPushed) {
                Object userId = model.getValueAt(row, 0);
                Object userName = model.getValueAt(row, 1);

                int confirm = JOptionPane.showConfirmDialog(parent,
                        "Are you sure you want to approve user: " + userName + "?",
                        "Confirm User Approval",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Disable button during processing
                    button.setEnabled(false);
                    button.setText("Processing...");

                    final int currentRow = row; // capture row for safe removal

                    // Use threading to prevent UI freezing
                    Thread thread = new Thread(() -> {
                        try {
                            boolean success = userService.approveUser(userId);

                            // Update UI on EDT
                            SwingUtilities.invokeLater(() -> {
                                // Stop editing BEFORE removing the row
                                fireEditingStopped();

                                if (success) {
                                    JOptionPane.showMessageDialog(parent,
                                            "User '" + userName + "' has been approved successfully!",
                                            "User Approved",
                                            JOptionPane.INFORMATION_MESSAGE);

                                    // Safely remove the row
                                    if (currentRow < model.getRowCount()) {
                                        model.removeRow(currentRow);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(parent,
                                            "Failed to approve user '" + userName + "'. Please try again.",
                                            "Approval Failed",
                                            JOptionPane.ERROR_MESSAGE);
                                }

                                // Re-enable button
                                button.setEnabled(true);
                                button.setText("Approve");
                            });
                        } catch (Exception e) {
                            SwingUtilities.invokeLater(() -> {
                                fireEditingStopped();
                                JOptionPane.showMessageDialog(parent,
                                        "Error approving user: " + e.getMessage(),
                                        "Database Error",
                                        JOptionPane.ERROR_MESSAGE);

                                button.setEnabled(true);
                                button.setText("Approve");
                            });
                        }
                    });

                    thread.setDaemon(true);
                    thread.start();
                } else {
                    fireEditingStopped();
                }
            }
        }


        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        public void cancelCellEditing() {
            isPushed = false;
            super.cancelCellEditing();
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listeners.add(l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listeners.remove(l);
        }

        @Override
        protected void fireEditingStopped() {
            ChangeEvent event = new ChangeEvent(this);
            for (CellEditorListener listener : listeners) {
                listener.editingStopped(event);
            }
        }

        @Override
        protected void fireEditingCanceled() {
            ChangeEvent event = new ChangeEvent(this);
            for (CellEditorListener listener : listeners) {
                listener.editingCanceled(event);
            }
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            button.setEnabled(true);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }
    }
}
