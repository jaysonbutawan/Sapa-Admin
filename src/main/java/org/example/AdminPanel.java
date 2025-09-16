package org.example;

import org.example.ui.components.AdminUIComponents;
import org.example.ui.panels.*;
import org.example.utils.UIStyler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * AdminPanel - Main admin dashboard and management UI.
 * Refactored to use modular architecture with separated concerns.
 * Updated with UI monitoring and background threading to prevent freezing.
 */
public class AdminPanel extends JFrame {
    // --- UI Components ---
    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private UserManagementPanel userManagementPanel;
    private SchoolManagementPanel schoolManagementPanel;
    private HospitalManagementPanel hospitalManagementPanel;
    private BookingOverviewPanel bookingOverviewPanel;

    // --- Constructor ---
    public AdminPanel() {
        setTitle("Admin Panel Dashboard");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initializeComponents();
        setupShutdownHandling();


    }

    private void initializeComponents() {
        // Create header with global refresh button
        JPanel headerPanel = createHeaderWithRefresh();
        add(headerPanel, BorderLayout.NORTH);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIStyler.MAIN_FONT);

        // Initialize panels
        dashboardPanel = new DashboardPanel(tabbedPane);
        userManagementPanel = new UserManagementPanel(this);
        schoolManagementPanel = new SchoolManagementPanel(this);
        hospitalManagementPanel = new HospitalManagementPanel(this);
        bookingOverviewPanel = new BookingOverviewPanel();

        // Add tabs
        tabbedPane.addTab("Dashboard", dashboardPanel);
        tabbedPane.addTab("User Management", userManagementPanel);
        tabbedPane.addTab("School Management", schoolManagementPanel);
        tabbedPane.addTab("Hospital Management", hospitalManagementPanel);
        tabbedPane.addTab("Booking Overview", bookingOverviewPanel);
        add(tabbedPane, BorderLayout.CENTER);

        // Create status bar
        add(AdminUIComponents.createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderWithRefresh() {
        JPanel headerPanel = new JPanel(new BorderLayout());

        // Original header
        JPanel originalHeader = AdminUIComponents.createHeader();
        headerPanel.add(originalHeader, BorderLayout.CENTER);

        // Global refresh controls
        JPanel globalRefreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        globalRefreshPanel.setBackground(UIStyler.PRIMARY_COLOR);

        // Add global refresh button
        JButton globalRefreshButton = new JButton("Refresh Current Tab");
        globalRefreshButton.setFont(UIStyler.MAIN_FONT);
        globalRefreshButton.setBackground(UIStyler.ACCENT_COLOR);
        globalRefreshButton.setForeground(Color.WHITE);
        globalRefreshButton.setFocusPainted(false);
        globalRefreshButton.setBorderPainted(false);
        globalRefreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect
        globalRefreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                globalRefreshButton.setBackground(UIStyler.ACCENT_COLOR.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                globalRefreshButton.setBackground(UIStyler.ACCENT_COLOR);
            }
        });

        // Add action listener to refresh the currently active tab
        globalRefreshButton.addActionListener(e -> refreshCurrentTab());

        globalRefreshPanel.add(globalRefreshButton);
        headerPanel.add(globalRefreshPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * Refresh the currently active tab's data
     */
    private void refreshCurrentTab() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        String tabTitle = tabbedPane.getTitleAt(selectedIndex);

        switch (tabTitle) {
            case "School Management":
                refreshSchoolTable();
                break;
            case "User Management":
                if (userManagementPanel != null) {
                    userManagementPanel.refreshData();
                }
                break;
            case "Hospital Management":
                if (hospitalManagementPanel != null) {
                    hospitalManagementPanel.refreshData();
                }
                break;
            case "Booking Overview":
                if (bookingOverviewPanel != null) {
                    bookingOverviewPanel.refreshData();
                }
                break;
            case "Dashboard":
                if (dashboardPanel != null) {
                    dashboardPanel.refreshData();
                }
                break;
            default:
                System.out.println("No refresh action defined for tab: " + tabTitle);
        }
    }

    private void setupShutdownHandling() {
        // Handle window closing to properly shutdown refresh manager
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        AdminPanel.this,
                        "Are you sure you want to exit the application?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    shutdownApplication();
                }
            }
        });
    }

    private void shutdownApplication() {
        try {
            // Cleanup panels
            if (schoolManagementPanel != null) {
                schoolManagementPanel.cleanup();
            }
            if (userManagementPanel != null && userManagementPanel instanceof Cleanupable) {
                ((Cleanupable) userManagementPanel).cleanup();
            }
            if (hospitalManagementPanel != null && hospitalManagementPanel instanceof Cleanupable) {
                ((Cleanupable) hospitalManagementPanel).cleanup();
            }
            if (bookingOverviewPanel != null && bookingOverviewPanel instanceof Cleanupable) {
                ((Cleanupable) bookingOverviewPanel).cleanup();
            }
            if (dashboardPanel != null && dashboardPanel instanceof Cleanupable) {
                ((Cleanupable) dashboardPanel).cleanup();
            }


            System.out.println("Application shutdown completed successfully");
            System.exit(0);
        } catch (Exception ex) {
            System.err.println("Error during shutdown: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }


    public void refreshSchoolTable() {
        if (schoolManagementPanel != null) {
            schoolManagementPanel.refreshSchoolTable();
        }
    }

    // Interface for panels that need cleanup
    public interface Cleanupable {
        void cleanup();
    }
}
