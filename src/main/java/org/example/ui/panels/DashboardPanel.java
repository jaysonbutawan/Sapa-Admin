package org.example.ui.panels;

import org.example.utils.UIStyler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main dashboard panel with summary cards and quick actions
 */
public class DashboardPanel extends JPanel {
    private final JTabbedPane parentTabbedPane;
    private JLabel pendingUsersCount;
    private JLabel pendingSchoolsCount;
    private JLabel totalHospitalsCount;
    private JLabel totalBookingsCount;

    public DashboardPanel(JTabbedPane parentTabbedPane) {
        this.parentTabbedPane = parentTabbedPane;
        initializeComponents();

    }

    private void initializeComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UIStyler.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel summaryPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        summaryPanel.setBackground(UIStyler.BACKGROUND_COLOR);

        summaryPanel.add(createSummaryCard("User Management", "12", "Approve or reject user registrations", "Tap to open", 1, true));
        summaryPanel.add(createSummaryCard("School Management", "8", "Manage school registrations and students", "Tap to open", 2, true));
        summaryPanel.add(createSummaryCard("Hospital Management", "15", "Add and manage hospitals & departments", "Tap to open", 3, true));
        summaryPanel.add(createSummaryCard("Booking Overview", "142", "View all hospital training bookings", "Tap to open", 4, true));
        summaryPanel.add(createSummaryCard("Analytics & Reports", "", "View system statistics and reports", "Tap to open", -1, false));
        summaryPanel.add(createSummaryCard("System Settings", "", "Configure system preferences", "Tap to open", -1, false));

        // Quick Actions
        JPanel quickActionsPanel = createQuickActionsPanel();

        add(summaryPanel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(quickActionsPanel);
    }

    private JPanel createSummaryCard(String title, String count, String description, String action, int tabIndex, boolean trackCount) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIStyler.CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = UIStyler.createStyledLabel(title, UIStyler.TITLE_FONT, UIStyler.TEXT_COLOR);
        JLabel countLabel = UIStyler.createStyledLabel(count, new Font("Segoe UI", Font.BOLD, 24), UIStyler.PRIMARY_COLOR);
        JLabel descLabel = UIStyler.createStyledLabel(description, UIStyler.MAIN_FONT, UIStyler.TEXT_COLOR);
        JLabel actionLabel = UIStyler.createStyledLabel(action, new Font("Segoe UI", Font.ITALIC, 11), UIStyler.SECONDARY_COLOR);

        // Store references to count labels for refresh
        if (trackCount) {
            switch (title) {
                case "User Management":
                    pendingUsersCount = countLabel;
                    break;
                case "School Management":
                    pendingSchoolsCount = countLabel;
                    break;
                case "Hospital Management":
                    totalHospitalsCount = countLabel;
                    break;
                case "Booking Overview":
                    totalBookingsCount = countLabel;
                    break;
            }
        }

        card.add(titleLabel);
        if (!count.isEmpty()) {
            card.add(Box.createRigidArea(new Dimension(0, 5)));
            card.add(countLabel);
        }
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(descLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(actionLabel);

        // Add click listener to switch to appropriate tab
        if (tabIndex > 0) {
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    parentTabbedPane.setSelectedIndex(tabIndex);
                }
            });
        }

        return card;
    }

    private JPanel createQuickActionsPanel() {
        JPanel quickActionsPanel = new JPanel();
        quickActionsPanel.setLayout(new BoxLayout(quickActionsPanel, BoxLayout.Y_AXIS));
        quickActionsPanel.setBackground(UIStyler.BACKGROUND_COLOR);
        quickActionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel quickActionsLabel = UIStyler.createStyledLabel("Quick Actions", new Font("Segoe UI", Font.BOLD, 18), UIStyler.TEXT_COLOR);
        quickActionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionButtonsPanel.setBackground(UIStyler.BACKGROUND_COLOR);

        JButton reviewUsersButton = new JButton("Review Pending Users (12)");
        UIStyler.styleButton(reviewUsersButton);

        JButton reviewSchoolsButton = new JButton("Review Pending Schools (8)");
        UIStyler.styleButton(reviewSchoolsButton);

        actionButtonsPanel.add(reviewUsersButton);
        actionButtonsPanel.add(reviewSchoolsButton);

        // Add action listeners
        reviewUsersButton.addActionListener(e -> parentTabbedPane.setSelectedIndex(1));
        reviewSchoolsButton.addActionListener(e -> parentTabbedPane.setSelectedIndex(2));

        quickActionsPanel.add(quickActionsLabel);
        quickActionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        quickActionsPanel.add(actionButtonsPanel);

        return quickActionsPanel;
    }

    /**
     * Refresh the dashboard data including counts and quick action buttons
     */
    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Update counts (in a real application, these would be fetched from database)
                if (pendingUsersCount != null) {
                    pendingUsersCount.setText("12");
                }
                if (pendingSchoolsCount != null) {
                    pendingSchoolsCount.setText("8");
                }
                if (totalHospitalsCount != null) {
                    totalHospitalsCount.setText("15");
                }
                if (totalBookingsCount != null) {
                    totalBookingsCount.setText("142");
                }

                // Refresh the display
                revalidate();
                repaint();

                System.out.println("Dashboard data refreshed successfully");
            } catch (Exception e) {
                System.err.println("Error refreshing dashboard data: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
