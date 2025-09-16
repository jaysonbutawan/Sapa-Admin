package org.example.services;

import org.example.DatabaseConnection;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class BookingServices {

    public Object[][] fetchBookingOverview() {
        List<Object[]> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Admin_BookingOverviewView";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null;
             java.sql.ResultSet rs = stmt != null ? stmt.executeQuery() : null) {

            while (rs != null && rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getObject("booking_id");
                row[1] = rs.getObject("school_name");
                row[2] = rs.getObject("hospital_name");
                row[3] = rs.getObject("department");
                row[4] = rs.getObject("slot_date");
                row[5] = rs.getObject("time_range");
                row[6] = rs.getObject("student_count");
                row[7] = rs.getObject("appointment_status");
                bookings.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load booking overview: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return bookings.toArray(new Object[0][0]);
    }

    public boolean approveBooking(int appointmentId) {
        String sql = "{CALL ApproveBooking(?)}";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {

            if (stmt != null) {
                stmt.setInt(1, appointmentId);
                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0; // true if at least one row was updated
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Object[][] fetchStudentsByBooking(int appointmentId) {
        List<Object[]> students = new ArrayList<>();
        String sql = "{CALL Admin_GetStudentsByAppointment(?)}";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {

            if (stmt != null) {
                stmt.setInt(1, appointmentId);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[5];
                        row[0] = rs.getObject("student_id");
                        row[1] = rs.getObject("firstname");
                        row[2] = rs.getObject("lastname");
                        row[3] = rs.getObject("email");
                        row[4] = rs.getObject("school_name");
                        students.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to load students for booking: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return students.toArray(new Object[0][0]);
    }


}
