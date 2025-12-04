package college;

import DB.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class EnrolmentPanel extends JPanel {

    private final JComboBox<String> studentBox;
    private final JComboBox<String> courseBox;
    private final JTable enrollmentTable;
    private final DefaultTableModel model;
    private final Map<String, CourseInfo> courseMap = new HashMap<>();
    private final Map<String, Integer> studentMap = new HashMap<>(); // store student name → id
    private final Map<String, Integer> courseIdMap = new HashMap<>(); // store course name → id

    private final JTextField txtCredits;
    private final JTextField txtInstructor;

    // ---------- Inner Class to Store Course Info ----------
    private static class CourseInfo {
        int credits;
        String instructor;
        CourseInfo(int c, String i) {
            credits = c;
            instructor = i;
        }
    }

    // ---------- Constructor ----------
    public EnrolmentPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(240, 245, 255));

        // ---------- Header ----------
        JLabel header = new JLabel("Student Course Enrollment", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setForeground(new Color(25, 25, 112));
        add(header, BorderLayout.NORTH);

        // ---------- Form Panel ----------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
                new EmptyBorder(20, 40, 20, 40),
                new LineBorder(new Color(173, 216, 230), 2, true)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Student ComboBox ---
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Student:"), gbc);
        studentBox = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(studentBox, gbc);

        // --- Course ComboBox ---
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Select Course:"), gbc);
        courseBox = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(courseBox, gbc);

        // --- Credits ---
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Credit Hours:"), gbc);
        txtCredits = new JTextField();
        txtCredits.setEditable(false);
        gbc.gridx = 1;
        formPanel.add(txtCredits, gbc);

        // --- Instructor ---
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Instructor:"), gbc);
        txtInstructor = new JTextField();
        txtInstructor.setEditable(false);
        gbc.gridx = 1;
        formPanel.add(txtInstructor, gbc);

        // --- Buttons Panel ---
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // --- Enroll Button ---
        JButton enrollBtn = new JButton(" Enroll Student");
        enrollBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        enrollBtn.setBackground(new Color(30, 144, 255));
        enrollBtn.setForeground(Color.WHITE);
        enrollBtn.setFocusPainted(false);
        enrollBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        enrollBtn.setBorder(new LineBorder(new Color(25, 25, 112), 1, true));
        buttonPanel.add(enrollBtn);

        // --- DELETE Button (New) ---
        JButton deleteBtn = new JButton("Delete Enrollment");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteBtn.setBackground(new Color(231, 76, 60)); // Red color for delete
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.setBorder(new LineBorder(new Color(192, 57, 43), 1, true));
        buttonPanel.add(deleteBtn); // Add new button to the panel

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc); // Add button panel to the form

        add(formPanel, BorderLayout.WEST);

        // ---------- Table Panel ----------
        String[] columns = {"Enrollment ID", "Student Name", "Course Name", "Enroll Date"};
        model = new DefaultTableModel(columns, 0);
        enrollmentTable = new JTable(model);
        enrollmentTable.setRowHeight(25);
        enrollmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        enrollmentTable.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(173, 216, 230), 2, true),
                "Enrollment Records",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(25, 25, 112)
        ));
        add(scrollPane, BorderLayout.CENTER);

        // ---------- Load Data ----------
        loadStudents();
        loadCourses();

        // ---------- Actions ----------
        courseBox.addActionListener(_ -> showCourseDetails());
        enrollBtn.addActionListener(_ -> enrollStudent());

        // --- New Action for Delete Button ---
        deleteBtn.addActionListener(_ -> deleteEnrollment());

        // Load existing enrollments
        viewEnrollments();
    }

    // ---------- Load Students from DB ----------
    private void loadStudents() {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT STUDENT_ID, NAME FROM STUDENTS")) {

            studentBox.removeAllItems();
            studentBox.addItem("Select Student...");
            studentMap.clear();

            while (rs.next()) {
                int id = rs.getInt("STUDENT_ID");
                String name = rs.getString("NAME");
                studentBox.addItem(name);
                studentMap.put(name, id);
            }
            studentBox.setSelectedIndex(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error loading students: " + e.getMessage());
        }
    }

    // ---------- Load Courses from DB ----------
    private void loadCourses() {
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT COURSE_ID, COURSE_NAME, CREDIT_HOURS, INSTRUCTOR_NAME FROM COURSES")) {

            courseBox.removeAllItems();
            courseBox.addItem("Select Course...");
            courseMap.clear();
            courseIdMap.clear();

            while (rs.next()) {
                int id = rs.getInt("COURSE_ID");
                String name = rs.getString("COURSE_NAME");
                int credits = rs.getInt("CREDIT_HOURS");
                String instructor = rs.getString("INSTRUCTOR_NAME");

                courseBox.addItem(name);
                courseMap.put(name, new CourseInfo(credits, instructor));
                courseIdMap.put(name, id);
            }

            courseBox.setSelectedIndex(0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error loading courses: " + e.getMessage());
        }
    }

    // ---------- Show Course Details ----------
    private void showCourseDetails() {
        String selected = (String) courseBox.getSelectedItem();
        if (selected == null || selected.equals("Select Course...")) {
            txtCredits.setText("");
            txtInstructor.setText("");
            return;
        }

        CourseInfo info = courseMap.get(selected);
        if (info != null) {
            txtCredits.setText(String.valueOf(info.credits));
            txtInstructor.setText(info.instructor);
        }
    }

    // ---------- Enroll Student ----------
    private void enrollStudent() {
        String studentName = (String) studentBox.getSelectedItem();
        String courseName = (String) courseBox.getSelectedItem();

        if (studentName == null || studentName.equals("Select Student...") ||
                courseName == null || courseName.equals("Select Course...")) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select both student and course!");
            return;
        }

        int studentId = studentMap.get(studentName);
        int courseId = courseIdMap.get(courseName);

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO ENROLLMENTS (STUDENT_ID, COURSE_ID, ENROLL_DATE) VALUES (?, ?, SYSDATE)"
            );
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Student enrolled successfully!");
            viewEnrollments();

        } catch (SQLException e) {
            // Check for potential duplicate enrollment constraint violation
            if (e.getErrorCode() == 1) { // Oracle unique constraint violation error code
                JOptionPane.showMessageDialog(this, "❌ Error: This student is already enrolled in this course!");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Error enrolling student: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // ---------- Delete Enrollment (New Function) ----------
    private void deleteEnrollment() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select an enrollment record from the table to delete.");
            return;
        }

        // Enrollment ID is in the first column (index 0)
        int enrollmentId = (int) model.getValueAt(selectedRow, 0);
        String studentName = (String) model.getValueAt(selectedRow, 1);
        String courseName = (String) model.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the enrollment for:\n" +
                        "Student: " + studentName + "\nCourse: " + courseName + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM ENROLLMENTS WHERE ENROLL_ID = ?");
                ps.setInt(1, enrollmentId);
                int rowsDeleted = ps.executeUpdate();

                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "🗑️ Enrollment deleted successfully!");
                    viewEnrollments(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Enrollment not found or already deleted.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "❌ Error deleting enrollment: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // ---------- View All Enrollments ----------
    private void viewEnrollments() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT e.ENROLL_ID, s.NAME AS STUDENT_NAME, c.COURSE_NAME, e.ENROLL_DATE " +
                             "FROM ENROLLMENTS e " +
                             "JOIN STUDENTS s ON e.STUDENT_ID = s.STUDENT_ID " +
                             "JOIN COURSES c ON e.COURSE_ID = c.COURSE_ID " +
                             "ORDER BY e.ENROLL_ID")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ENROLL_ID"),
                        rs.getString("STUDENT_NAME"),
                        rs.getString("COURSE_NAME"),
                        rs.getDate("ENROLL_DATE")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error viewing enrollments: " + e.getMessage());
        }
    }
}