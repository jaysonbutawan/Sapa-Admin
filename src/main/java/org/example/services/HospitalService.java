package org.example.services;

import org.example.DatabaseConnection;
import org.example.models.HospitalItem;
import org.example.models.DepartmentItem;
import org.example.models.SlotDateItem;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;


public class HospitalService {

    public Object[][] fetchHospitals() {
        List<Object[]> hospitalList = new ArrayList<>();
        String sql = "SELECT * FROM Admin_view_hospitals";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[6];
                        row[0] = rs.getObject("hospital_id");
                        row[1] = rs.getObject("hospital_name");
                        row[2] = rs.getObject("hospital_address");
                        row[3] = rs.getObject("contact_info");
                        row[4] = rs.getObject("descriptions");
                        row[5] = "Actions";
                        hospitalList.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load hospitals: " + e.getMessage());
        }
        return hospitalList.toArray(new Object[0][0]);
    }

    public boolean addHospital(String name, String address, String contact, String description) {
        String sql = "{CALL add_hospital(?, ?, ?, ?)}";
        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setString(1, name);
                stmt.setString(2, address);
                stmt.setString(3, contact.isEmpty() ? null : contact);
                stmt.setString(4, description.isEmpty() ? null : description);
                int inserted = stmt.executeUpdate();
                return inserted > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to add hospital: " + e.getMessage());
        }
        return false;
    }


    public Object[][] fetchDepartments() {
        List<Object[]> deptList = new ArrayList<>();
        String sql = "SELECT * FROM view_departments";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[5];
                        row[0] = rs.getObject("department_id");
                        row[1] = rs.getObject("hospital_name");
                        row[2] = rs.getObject("section_name");
                        row[3] = rs.getObject("price_per_student");
                        row[4] = "Actions";
                        deptList.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load departments: " + e.getMessage());
        }
        return deptList.toArray(new Object[0][0]);
    }

    /**
     * Add a new department
     */
    public boolean addDepartment(int hospitalId, String sectionName, double price) {
        String sql = "{CALL Add_Department(?, ?, ?)}";
        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {

            if (stmt != null) {
                stmt.setInt(1, hospitalId);
                stmt.setString(2, sectionName);
                stmt.setDouble(3, price);

                int inserted = stmt.executeUpdate();
                return inserted > 0;
            }

        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            showErrorMessage("Failed to add department: " + errorMsg);
        }
        return false;
    }

    /**
     * Fetch available dates
     */
    public Object[][] fetchAvailableDates() {
        List<Object[]> dateList = new ArrayList<>();
        String sql = "SELECT * FROM admin_view_slot_dates";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[5];
                        row[0] = rs.getObject("slot_date_id");
                        row[1] = rs.getObject("hospital_name");
                        row[2] = rs.getObject("section_name");
                        row[3] = rs.getObject("slot_date");
                        row[4] = "Actions";
                        dateList.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load available dates: " + e.getMessage());
        }
        return dateList.toArray(new Object[0][0]);
    }


    public boolean addAvailableDate(int departmentId, String date) {
        String sql = "{CALL AddAvailableDate(?, ?)}";
        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.CallableStatement stmt = conn != null ? conn.prepareCall(sql) : null) {
            if (stmt != null) {
                stmt.setInt(1, departmentId);

                java.sql.Date sqlDate = java.sql.Date.valueOf(date);
                stmt.setDate(2, sqlDate);

                int inserted = stmt.executeUpdate();
                return inserted > 0;
            }
        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            showErrorMessage("Failed to add department: " + errorMsg);
        }
        return false;
    }


    public Object[][] fetchTimeSlots() {
        List<Object[]> timeSlotList = new ArrayList<>();
        String sql = "SELECT * FROM admin_view_time_slots";
        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[8];
                        row[0] = rs.getObject("time_slot_id");
                        row[1] = rs.getObject("hospital_name");
                        row[2] = rs.getObject("section_name");
                        row[3] = rs.getObject("slot_date");
                        row[4] = rs.getObject("start_time");
                        row[5] = rs.getObject("end_time");
                        row[6] = rs.getObject("capacity");
                        row[7] = "Actions";
                        timeSlotList.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load time slots: " + e.getMessage());
        }
        return timeSlotList.toArray(new Object[0][0]);
    }


    public boolean addTimeSlot(int slotDateId, String startTime, String endTime, int capacity) {
        String sql = "{CALL AddTimeSlot(?, ?, ?, ?)}";
        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setInt(1, slotDateId);
                stmt.setString(2, startTime);
                stmt.setString(3, endTime);
                stmt.setInt(4, capacity);
                int inserted = stmt.executeUpdate();
                return inserted > 0;
            }
        } catch (SQLException e) {
            String errorMsg = e.getMessage();
            showErrorMessage("Failed to add department: " + errorMsg);
        }
        return false;
    }

    public void loadHospitalsCombo(JComboBox<HospitalItem> combo) {
        combo.removeAllItems();
        String sql = "SELECT * FROM admin_hospital_combo_view";
        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        combo.addItem(new HospitalItem(rs.getInt("hospital_id"), rs.getString("hospital_name")));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadDepartmentsCombo(JComboBox<DepartmentItem> combo) {
        combo.removeAllItems();
        String sql = "SELECT * FROM admin_view_departments_combo";
        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        combo.addItem(new DepartmentItem(rs.getInt("department_id"), rs.getString("display_name")));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSlotDatesCombo(JComboBox<SlotDateItem> combo) {
        combo.removeAllItems();
        String sql = "SELECT * From admin_view_slot_dates_combo";
        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        combo.addItem(new SlotDateItem(rs.getInt("slot_date_id"), rs.getString("display_name")));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Object[][] fetchSchoolsByDepartment(int departmentId) {
        List<Object[]> schoolList = new ArrayList<>();
        String sql = "{CALL admin_Get_Schools_By_Department(?)}";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setInt(1, departmentId);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[4];
                        row[0] = rs.getObject("school_id");
                        row[1] = rs.getObject("school_name");
                        row[2] = rs.getObject("total_appointments");
                        row[3] = rs.getObject("total_students");
                        schoolList.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load schools for department: " + e.getMessage());
        }
        return schoolList.toArray(new Object[0][0]);
    }


    public Object[][] fetchStudentsBySchoolAndDepartment(int schoolId, int departmentId) {
        List<Object[]> studentList = new ArrayList<>();
        String sql = "{CALL Admin_Fetch_Students_By_School_And_Department(?, ?)}";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setInt(1, schoolId);
                stmt.setInt(2, departmentId);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[9];
                        row[0] = rs.getObject("student_id");
                        row[1] = rs.getObject("student_name");
                        row[2] = rs.getObject("email");
                        row[3] = rs.getObject("appointment_id");
                        row[4] = rs.getObject("slot_date");
                        row[5] = rs.getObject("start_time");
                        row[6] = rs.getObject("end_time");
                        row[7] = rs.getObject("appointment_status");
                        row[8] = rs.getObject("booking_date");
                        studentList.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load students for school and department: " + e.getMessage());
        }
        return studentList.toArray(new Object[0][0]);
    }

    public String getDepartmentName(int departmentId) {
        String sql = "{CALL Admin_Get_Department_Name(?)}";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setInt(1, departmentId);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs != null && rs.next()) {
                        return rs.getString("department_full_name");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Department";
    }


    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }



    public Object[][] fetchStudentsByTimeslot(int timeSlotId) {
        List<Object[]> studentList = new ArrayList<>();
        String sql = "{CALL FetchStudentsByTimeslot(?)}";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setInt(1, timeSlotId);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    while (rs != null && rs.next()) {
                        Object[] row = new Object[7];
                        row[0] = rs.getObject("student_id");
                        row[1] = rs.getObject("firstname");
                        row[2] = rs.getObject("lastname");
                        row[3] = rs.getObject("email");
                        row[4] = rs.getObject("school_name");
                        row[5] = rs.getObject("request_date");   // from Appointments
                        row[6] = rs.getObject("appointment_status"); // from Appointments
                        studentList.add(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to load students for timeslot: " + e.getMessage());
        }
        return studentList.toArray(new Object[0][0]);
    }

    /**
     * Threaded version to fetch students by timeslot using DatabaseConnection.performDatabaseOperation
     */
    public void fetchStudentsByTimeslotAsync(int timeSlotId,
                                           java.util.function.Consumer<Object[][]> onSuccess,
                                           java.util.function.Consumer<Exception> onError,
                                           Runnable onStart,
                                           Runnable onFinish) {

        DatabaseConnection.performDatabaseOperation(
            () -> fetchStudentsByTimeslot(timeSlotId),
            onSuccess,
            onError,
            onStart,
            onFinish
        );
    }

    /**
     * Get timeslot details for display purposes
     */
    public String getTimeslotDetails(int timeSlotId) {
        String sql = "{CALL GetTimeslotDetails(?)}";

        try (java.sql.Connection conn = DatabaseConnection.Connect();
             java.sql.PreparedStatement stmt = conn != null ? conn.prepareStatement(sql) : null) {
            if (stmt != null) {
                stmt.setInt(1, timeSlotId);
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs != null && rs.next()) {
                        return String.format("%s - %s | %s | %s - %s (Capacity: %d)",
                            rs.getString("hospital_name"),
                            rs.getString("section_name"),
                            rs.getDate("slot_date"),
                            rs.getTime("start_time"),
                            rs.getTime("end_time"),
                            rs.getInt("capacity"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Timeslot";
    }
}
