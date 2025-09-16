package org.example;

import org.example.utils.DatabaseWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginUI extends JFrame {
    // Color scheme (use static final for efficiency)
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(52, 73, 94);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);

    // Fonts (cached once, not recreated repeatedly)
    private static final Font FONT_BOLD_24 = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_BOLD_14 = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_PLAIN_14 = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_PLAIN_12 = new Font("Segoe UI", Font.PLAIN, 12);

    // Components
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheck;
    private JButton loginButton;
    private JLabel errorLabel;

    public LoginUI() {
        setTitle("Admin Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // Avoid crash on platforms without window shaping support
        if (GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().isWindowTranslucencySupported(
                        GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT)) {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
        }

        // Create custom title bar
        JPanel titlePanel = createTitleBar();

        // Create main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Add components
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        // Add panels to frame
        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTitleBar() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setPreferredSize(new Dimension(getWidth(), 40));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

        JLabel titleLabel = new JLabel("Admin Portal");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(FONT_BOLD_14);

        // Close button
        JButton closeButton = new JButton("×");
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> System.exit(0));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(closeButton, BorderLayout.EAST);

        // Add mouse listener for dragging the window
        MouseAdapter ma = new MouseAdapter() {
            private Point initialClick;

            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = getLocation();
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                setLocation(location.x + xMoved, location.y + yMoved);
            }
        };

        titlePanel.addMouseListener(ma);
        titlePanel.addMouseMotionListener(ma);

        return titlePanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(FONT_BOLD_24);
        titleLabel.setForeground(TEXT_COLOR);

        JLabel subtitleLabel = new JLabel("Access your administration dashboard");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(FONT_PLAIN_14);
        subtitleLabel.setForeground(TEXT_COLOR.brighter());

        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BACKGROUND_COLOR);

        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(FONT_PLAIN_14);
        emailLabel.setForeground(TEXT_COLOR);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        emailField.setFont(FONT_PLAIN_14);

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(FONT_PLAIN_14);
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        passwordField.setFont(FONT_PLAIN_14);

        // Show password checkbox
        showPasswordCheck = new JCheckBox("Show password");
        showPasswordCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPasswordCheck.setBackground(BACKGROUND_COLOR);
        showPasswordCheck.setFont(FONT_PLAIN_12);
        showPasswordCheck.setForeground(TEXT_COLOR);
        showPasswordCheck.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '•');
        });

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(ERROR_COLOR);
        errorLabel.setFont(FONT_PLAIN_12);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(FONT_BOLD_14);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginButton.setBackground(SECONDARY_COLOR);
            }

            public void mouseExited(MouseEvent evt) {
                loginButton.setBackground(PRIMARY_COLOR);
            }
        });

        loginButton.addActionListener(e -> attemptLogin());

        // Enter key = login
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        };

        emailField.addKeyListener(enterKeyAdapter);
        passwordField.addKeyListener(enterKeyAdapter);

        // Add components
        formPanel.add(emailLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(emailField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(showPasswordCheck);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(errorLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(loginButton);

        return formPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(BACKGROUND_COLOR);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        return footerPanel;
    }

    private void attemptLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        if (!isValidEmail(email)) {
            errorLabel.setText("Please enter a valid email address.");
            return;
        }

        errorLabel.setText(" ");

        // Disable button and show loading state
        loginButton.setText("Logging in...");
        loginButton.setEnabled(false);

        // Create new thread to handle database operation
        Thread thread = new Thread(() -> {
            LoginResult result = LoginDao.loginAdmin(email, password);

            // Since Swing components should be updated only on EDT, wrap updates like this:
            javax.swing.SwingUtilities.invokeLater(() -> {
                try {
                    if (result.success) {
                        JOptionPane.showMessageDialog(this,
                                result.message != null ? result.message : "Login successful!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        this.dispose();
                        new AdminPanel().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                result.message != null ? result.message : "Invalid email or password.",
                                "Login Failed", JOptionPane.ERROR_MESSAGE);
                        errorLabel.setText(result.status != null ? result.status : "Login failed.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Login failed: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    errorLabel.setText("Login failed due to an error.");
                } finally {
                    // Always restore button state
                    loginButton.setText("Login");
                    loginButton.setEnabled(true);
                }
            });
        });

        thread.setDaemon(true);
        thread.start();
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
    }
}
