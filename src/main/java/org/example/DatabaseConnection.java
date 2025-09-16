package org.example;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    private static final String IP = "192.168.71.1";
    private static final String DATABASE = "sapa_app_db";
    private static final String USER = "admin";
    private static final String PASS = "123";

    public static synchronized Connection Connect() {
        Connection connection = null ;
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");

                connection = DriverManager.getConnection("jdbc:mysql://" +IP+"/"
                        +DATABASE,USER,PASS);

            }catch (ClassNotFoundException | SQLException e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(  null,
                        "Database connection failed: " + e.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            return connection;
    }

    public static <T> void performDatabaseOperation(
            java.util.function.Supplier<T> operation,
            java.util.function.Consumer<T> onSuccess,
            java.util.function.Consumer<Exception> onError,
            Runnable onStart,
            Runnable onFinish) {

        if (onStart != null) {
            SwingUtilities.invokeLater(onStart);
        }

        Thread thread = new Thread(() -> {
            try {
                T result = operation.get();

                SwingUtilities.invokeLater(() -> {
                    try {
                        onSuccess.accept(result);
                    } finally {
                        if (onFinish != null) {
                            onFinish.run();
                        }
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        onError.accept(e);
                    } finally {
                        if (onFinish != null) {
                            onFinish.run();
                        }
                    }
                });
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    public static String getSystemLookAndFeel() {
        try {
            return UIManager.getSystemLookAndFeelClassName().getClass().getName();
        } catch (Exception e) {
            return UIManager.getCrossPlatformLookAndFeelClassName();
        }
    }
}
