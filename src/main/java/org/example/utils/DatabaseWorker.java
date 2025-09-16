package org.example.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Reusable SwingWorker for database operations that prevents UI freezing.
 */
public class DatabaseWorker {

    /**
     * Execute a database operation in the background with full lifecycle callbacks
     */
    public static <T> void execute(
            Supplier<T> backgroundTask,
            Consumer<T> onSuccess,
            Consumer<Exception> onError,
            Runnable onStart,
            Runnable onFinish) {

        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() {
                return backgroundTask.get();
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    onSuccess.accept(result);
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    Exception exception = (cause instanceof Exception) ?
                        (Exception) cause : new Exception(cause);
                    onError.accept(exception);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    onError.accept(e);
                } finally {
                    if (onFinish != null) {
                        onFinish.run();
                    }
                }
            }
        };

        if (onStart != null) {
            onStart.run();
        }

        worker.execute();
    }

    /**
     * Simplified version without progress callbacks
     */
    public static <T> void execute(
            Supplier<T> backgroundTask,
            Consumer<T> onSuccess,
            Consumer<Exception> onError) {
        execute(backgroundTask, onSuccess, onError, null, null);
    }

    /**
     * Execute a database operation with default error handling
     */
    public static <T> void execute(
            Supplier<T> backgroundTask,
            Consumer<T> onSuccess) {
        execute(backgroundTask, onSuccess, DatabaseWorker::showDefaultError);
    }

    /**
     * Execute a database operation with progress indication
     */
    public static <T> void executeWithProgress(
            Supplier<T> backgroundTask,
            Consumer<T> onSuccess,
            Consumer<Exception> onError,
            JComponent parentComponent,
            String progressMessage) {

        // Create progress dialog
        JDialog progressDialog = createProgressDialog(parentComponent, progressMessage);

        execute(
            backgroundTask,
            result -> {
                progressDialog.dispose();
                onSuccess.accept(result);
            },
            error -> {
                progressDialog.dispose();
                onError.accept(error);
            },
            () -> progressDialog.setVisible(true),
            null
        );
    }

    /**
     * Execute a void database operation (like insert/update/delete)
     */
    public static void executeVoid(
            Runnable backgroundTask,
            Runnable onSuccess,
            Consumer<Exception> onError) {
        execute(
            () -> {
                backgroundTask.run();
                return null;
            },
            result -> onSuccess.run(),
            onError
        );
    }

    /**
     * Execute a database query and populate a JTable with the results
     */
    public static void executeForTable(
            Supplier<List<Object[]>> dataSupplier,
            JTable table,
            String[] columnNames,
            Consumer<Exception> onError,
            Runnable onStart,
            Runnable onFinish) {

        execute(
            dataSupplier,
            data -> {
                // Update table on EDT
                DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false; // Make table read-only by default
                    }
                };

                for (Object[] row : data) {
                    model.addRow(row);
                }

                table.setModel(model);
            },
            onError,
            onStart,
            onFinish
        );
    }

    /**
     * Simplified JTable update method
     */
    public static void executeForTable(
            Supplier<List<Object[]>> dataSupplier,
            JTable table,
            String[] columnNames) {
        executeForTable(dataSupplier, table, columnNames, DatabaseWorker::showDefaultError, null, null);
    }

    private static JDialog createProgressDialog(JComponent parent, String message) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent),
                                   "Processing", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(message);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        panel.add(label, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        return dialog;
    }

    private static void showDefaultError(Exception e) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(
                null,
                "Database operation failed: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            )
        );
    }
}
