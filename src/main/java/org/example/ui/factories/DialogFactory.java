package org.example.ui.factories;

import org.example.utils.UIStyler;

import javax.swing.*;
import java.awt.*;

/**
 * Factory class for creating standardized dialog components
 * Centralizes dialog creation logic for consistency
 */
public class DialogFactory {

    /**
     * Creates a standard dialog with common settings
     */
    public static JDialog createStandardDialog(JFrame parent, String title, int width, int height) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(parent);
        return dialog;
    }

    /**
     * Creates a table scroll pane with standard settings
     */
    public static JScrollPane createTableScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    /**
     * Creates an instruction panel for user guidance
     */
    public static JPanel createInstructionPanel(String instructionText) {
        JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        instructionPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel instructionLabel = new JLabel("💡 " + instructionText);
        instructionLabel.setFont(UIStyler.BOLD_FONT);
        instructionLabel.setForeground(new Color(0, 100, 0));

        instructionPanel.add(instructionLabel);
        return instructionPanel;
    }

    /**
     * Creates a summary panel for displaying statistics
     */
    public static JPanel createSummaryPanel(String summaryText) {
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel summaryLabel = new JLabel(summaryText);
        summaryLabel.setFont(UIStyler.MAIN_FONT);

        summaryPanel.add(summaryLabel);
        return summaryPanel;
    }

    /**
     * Creates a "no data" label for empty results
     */
    public static JLabel createNoDataLabel(String message) {
        JLabel noDataLabel = new JLabel(message, SwingConstants.CENTER);
        noDataLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        noDataLabel.setForeground(Color.GRAY);
        return noDataLabel;
    }

    /**
     * Creates a count label for displaying totals
     */
    public static JLabel createCountLabel(String text) {
        JLabel countLabel = new JLabel(text);
        countLabel.setFont(UIStyler.MAIN_FONT);
        return countLabel;
    }
}
