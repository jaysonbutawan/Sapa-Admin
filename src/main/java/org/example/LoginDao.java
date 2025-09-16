package org.example;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDao {
    public static LoginResult loginAdmin(String email, String password) {
        Connection conn = null;
        CallableStatement stmt = null;
        String status = null;
        String message = null;
        int userId = -1;
        boolean isAuthenticated = false;
        try {
            conn = DatabaseConnection.Connect();
            if (conn != null) {
                stmt = conn.prepareCall("{ call LoginAdmin(?, ?, ?, ?, ?) }");
                stmt.setString(1, email);
                stmt.setString(2, password);
                stmt.registerOutParameter(3, java.sql.Types.VARCHAR);
                stmt.registerOutParameter(4, java.sql.Types.VARCHAR);
                stmt.registerOutParameter(5, java.sql.Types.INTEGER);

                System.out.println("[DEBUG] Email sent: '" + email + "'");
                System.out.println("[DEBUG] Password sent: '" + password + "'");

                stmt.execute();

                status = stmt.getString(3);
                message = stmt.getString(4);
                userId = stmt.getInt(5);

                System.out.println("[DEBUG] Login status: " + status);
                System.out.println("[DEBUG] Message: " + message);
                System.out.println("[DEBUG] User ID: " + userId);

                if ("success".equalsIgnoreCase(status)) {
                    isAuthenticated = true;
                }
            }
        } catch (SQLException e) {
            message = "Database error: " + e.getMessage();
            status = "error";
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
        return new LoginResult(isAuthenticated, status, message, userId);
    }


}
