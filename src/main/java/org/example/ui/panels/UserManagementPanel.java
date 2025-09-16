package org.example.ui.panels;

import org.example.services.UserService;
import org.example.ui.components.UserTableComponents;
import org.example.ui.dialogs.SchoolDialogs;
import org.example.utils.RefreshUtils;
import org.example.utils.UIStyler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Panel for managing users (pending and approved)
 */
public class UserManagementPanel extends JPanel  {
    private final JFrame parentFrame;
    private final UserService userService;
    private final SchoolDialogs schoolDialogs;

    // Table models for refresh functionality
    private DefaultTableModel pendingUsersModel;
    private DefaultTableModel approvedUsersModel;
    private JTable pendingUsersTable;
    private JTable approvedUsersTable;

    public UserManagementPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.userService = new UserService();
        this.schoolDialogs = new SchoolDialogs(parentFrame, new org.example.services.SchoolService());

        initializeComponents();

    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Add refresh controls at the top
        JPanel refreshPanel = RefreshUtils.createRefreshControlPanel(e -> refreshData());
        add(refreshPanel, BorderLayout.NORTH);

        JTabbedPane userTabs = new JTabbedPane();
        userTabs.setFont(UIStyler.MAIN_FONT);
        userTabs.addTab("Pending Users", createPendingUsersPanel());
        userTabs.addTab("Approved Users", createApprovedUsersPanel());

        add(userTabs, BorderLayout.CENTER);
    }

    private JPanel createPendingUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"User ID", "Full Name", "Email", "Added At", "Approve"};
        Object[][] rawData = userService.fetchUsersFromView(false);
        Object[][] data = new Object[rawData.length][5];
        for (int i = 0; i < rawData.length; i++) {
            System.arraycopy(rawData[i], 0, data[i], 0, 4);
            data[i][4] = "Approve";
        }

        pendingUsersModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        pendingUsersTable = UIStyler.createStyledTable(data, columns);
        pendingUsersTable.setModel(pendingUsersModel);
        pendingUsersTable.getColumn("Approve").setCellRenderer(new UserTableComponents.ButtonRenderer());
        pendingUsersTable.getColumn("Approve").setCellEditor(new UserTableComponents.ButtonEditor(parentFrame, pendingUsersModel));

        JScrollPane scrollPane = new JScrollPane(pendingUsersTable);
        panel.add(UIStyler.createStyledLabel("Pending Users", UIStyler.TITLE_FONT, UIStyler.TEXT_COLOR), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createApprovedUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"User ID", "Full Name", "Email", "Added At"};
        Object[][] data = userService.fetchUsersFromView(true);

        approvedUsersModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        approvedUsersTable = UIStyler.createStyledTable(data, columns);
        approvedUsersTable.setModel(approvedUsersModel);

        // Add mouse listener for double-click to show schools
        approvedUsersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && approvedUsersTable.getSelectedRow() != -1) {
                    int row = approvedUsersTable.getSelectedRow();
                    Object userId = approvedUsersTable.getValueAt(row, 0);
                    schoolDialogs.showUserSchoolsDialog(userId);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(approvedUsersTable);
        panel.add(UIStyler.createStyledLabel("Approved Users", UIStyler.TITLE_FONT, UIStyler.TEXT_COLOR), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }


    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Refresh pending users
                Object[][] pendingRawData = userService.fetchUsersFromView(false);
                Object[][] pendingData = new Object[pendingRawData.length][5];
                for (int i = 0; i < pendingRawData.length; i++) {
                    System.arraycopy(pendingRawData[i], 0, pendingData[i], 0, 4);
                    pendingData[i][4] = "Approve";
                }
                RefreshUtils.refreshTableModel(pendingUsersModel, pendingData);

                // Refresh approved users
                Object[][] approvedData = userService.fetchUsersFromView(true);
                RefreshUtils.refreshTableModel(approvedUsersModel, approvedData);

                System.out.println("User Management Panel data refreshed successfully");
            } catch (Exception e) {
                System.err.println("Error refreshing User Management Panel: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Failed to refresh user data: " + e.getMessage(),
                    "Refresh Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}
