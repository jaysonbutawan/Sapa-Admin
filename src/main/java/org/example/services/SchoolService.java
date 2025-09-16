package org.example.services;

import org.example.DatabaseConnection;
import javax.swing.JOptionPane;
import java.util.List;
import java.util.ArrayList;

public class SchoolService {

    public Object[][] fetchSchoolsForManagement() {
        List<Object[]> schoolList = new ArrayList<>();
        String sql = "SELECT * from AdminSchoolView";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[6];
                        row[0] = rs.getObject("school_id");
                        row[1] = rs.getObject("school_name");
                        row[2] = rs.getObject("full_name");
                        row[3] = rs.getObject("status");
                        row[4] = rs.getObject("student_count");
                        row[5] = "Actions";
                        schoolList.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return schoolList.toArray(new Object[0][0]);
    }

    public void fetchSchoolsForManagementAsync(
            java.util.function.Consumer<Object[][]> onSuccess,
            java.util.function.Consumer<Exception> onError) {

        org.example.utils.ThreadUtils.executeInBackground(
            this::fetchSchoolsForManagement,
            onSuccess,
            onError
        );
    }

    /**
     * Approve a school - now with background execution
     */
    /**
     * Approve a school - synchronous version (same style as rejectSchool)
     */
    public boolean approveSchool(Object schoolId) {
        String sql = "{CALL ApproveSchool(?)}";
        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setObject(1, schoolId);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("School approved successfully: " + schoolId);
                    return true;
                } else {
                    showErrorMessage("No school found with ID: " + schoolId);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to approve school: " + e.getMessage());
            return false;
        }
        return false;
    }


    public boolean rejectSchool(Object schoolId) {
        String sql = "{CALL RejectSchool(?)}";
        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setObject(1, schoolId);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("School rejected successfully: " + schoolId);
                    return true;
                } else {
                    showErrorMessage("No school found with ID: " + schoolId);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to reject school: " + e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * Fetch appointments for a specific school
     */
    public Object[][] fetchAppointmentsForSchool(Object schoolId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT * FROM Appointments(?)";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setObject(1, schoolId);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[6];
                        row[0] = rs.getObject("appointment_id");
                        row[1] = rs.getObject("hospital_name");
                        row[2] = rs.getObject("section_name");
                        row[3] = rs.getObject("date");
                        row[4] = rs.getObject("time");
                        row[5] = rs.getObject("status");
                        list.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load appointments: " + e.getMessage());
        }
        return list.toArray(new Object[0][0]);
    }

    /**
     * Fetch students for a specific school
     */
    public Object[][] fetchStudentsForSchool(Object schoolId) {
        List<Object[]> list = new ArrayList<>();
        String sql = "CALL AdminGetStudentsBySchool(?)";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setObject(1, schoolId);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[4];
                        row[0] = rs.getObject("student_id");
                        row[1] = rs.getObject("full_name");
                        row[2] = rs.getObject("email");
                        row[3] = rs.getObject("added_at");
                        list.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load students: " + e.getMessage());
        }
        return list.toArray(new Object[0][0]);
    }

    /**
     * Fetch schools submitted by a specific user
     */
    public Object[][] fetchSchoolsForUser(Object userId) {
        List<Object[]> schoolList = new ArrayList<>();
        String sql = "{CALL FetchSchoolsForUser(?)}";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setObject(1, userId);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[8];
                        row[0] = rs.getObject("school_id");
                        row[1] = rs.getObject("school_code");
                        row[2] = rs.getObject("school_name");
                        row[3] = rs.getObject("school_address");
                        row[4] = rs.getObject("contact_info");
                        row[5] = rs.getObject("status");
                        row[6] = rs.getObject("created_at");
                        row[7] = rs.getObject("approved_at");
                        schoolList.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load schools for user: " + e.getMessage());
        }
        return schoolList.toArray(new Object[0][0]);
    }

    private void showErrorMessage(String message) {
        // Only show error message if we're on EDT
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // Schedule on EDT
            javax.swing.SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE)
            );
        }
    }
}
