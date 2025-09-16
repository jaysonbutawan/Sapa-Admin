package org.example.services;

import org.example.DatabaseConnection;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.ArrayList;


public class UserService {


    public Object[][] fetchUsersFromView(boolean approved) {
        List<Object[]> userList = new ArrayList<>();
        String sql = "{CALL FetchUsersByApproval(?)}";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setBoolean(1, approved);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[4];
                        row[0] = rs.getObject("user_id");
                        row[1] = rs.getObject("fullname");
                        row[2] = rs.getObject("email");
                        row[3] = rs.getObject("added_at");
                        userList.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load users: " + e.getMessage());
        }
        return userList.toArray(new Object[0][0]);
    }


    public boolean approveUser(Object userId) {
        if (userId == null) {
            System.err.println("Cannot approve user: userId is null");
            return false;
        }

        String sql = "{CALL ApproveUser(?)}";

        try (java.sql.Connection conn = DatabaseConnection.Connect()) {
            if (conn == null) {
                System.err.println("Failed to approve user: Database connection is null");
                return false;
            }

            try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, userId);

                System.out.println("[DEBUG] Attempting to approve user with ID: " + userId);

                int updated = stmt.executeUpdate();

                System.out.println("[DEBUG] Rows updated: " + updated);

                if (updated > 0) {
                    System.out.println("[SUCCESS] User " + userId + " approved successfully");
                    return true;
                } else {
                    System.err.println("[WARNING] No rows updated. User ID might not exist: " + userId);
                    return false;
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to approve user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }




    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
