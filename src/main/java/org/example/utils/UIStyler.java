package org.example.utils;

import javax.swing.*;
import java.awt.*;


public class UIStyler {

    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    public static final Color ACCENT_COLOR = new Color(231, 76, 60);
    public static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    public static final Color TEXT_COLOR = new Color(52, 73, 94);
    public static final Color CARD_COLOR = Color.WHITE;


    public static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);

    public static void styleButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(BOLD_FONT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    public static JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return card;
    }


    public static JTable createStyledTable(Object[][] data, String[] columns) {
        JTable table = new JTable(data, columns);
        table.setFont(MAIN_FONT);
        table.setRowHeight(30);
        return table;
    }

    public static JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
}
