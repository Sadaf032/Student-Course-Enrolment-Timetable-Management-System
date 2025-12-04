package college;

import DB.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CoursePanel extends JPanel {

    private JTextField txtID, txtCredits, txtInstructor;
    private JComboBox<String> cmbCourses;
    private JTable courseTable;
    private DefaultTableModel courseModel;
    private JScrollPane scroll;
    private Connection conn;

    private final Map<String, CourseInfo> courseMap = new HashMap<>();

    // Helper class for course data
    private static class CourseInfo {
        int id;
        int credits;
        String instructor;

        CourseInfo(int id, int credits, String instructor) {
            this.id = id;
            this.credits = credits;
            this.instructor = instructor;
        }
    }

    public CoursePanel() {
        setLayout(null);
        setBackground(new Color(240, 240, 240));

        connectDB(); // Connect to Oracle DB
        initComponents();
        loadCoursesTable();
        loadCoursesIntoComboBox();
    }

    // ----------------------------------------------
    // DB Connection
    // ----------------------------------------------
    private void connectDB() {
        try {
            conn = DBConnection.getConnection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "⚠️ Failed to connect to Oracle DB.\n" + e.getMessage());
        }
    }

    // ----------------------------------------------
    // UI Setup
    // ----------------------------------------------
    private void initComponents() {

        Font titleFont = new Font("Segoe UI", Font.BOLD, 22);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 15);
        Font textFieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel lblTitle = new JLabel("Course Management");
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(new Color(40, 80, 120));
        lblTitle.setBounds(400, 15, 350, 40);
        add(lblTitle);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBounds(40, 60, 380, 260);
        formPanel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                        "Course Details",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 16),
                        new Color(52, 73, 94))
        );

        int yStart = 30;
        int yGap = 40;
        int labelX = 30;
        int fieldX = 140;
        int fieldWidth = 200;
        int fieldHeight = 28;

        JLabel lblID = new JLabel("Course ID:");
        lblID.setFont(labelFont);
        lblID.setBounds(labelX, yStart, 120, 25);
        formPanel.add(lblID);

        txtID = new JTextField();
        txtID.setFont(textFieldFont);
        txtID.setBounds(fieldX, yStart, fieldWidth, fieldHeight);
        txtID.setEditable(false);
        formPanel.add(txtID);

        JLabel lblName = new JLabel("Course Name:");
        lblName.setFont(labelFont);
        lblName.setBounds(labelX, yStart + yGap, 120, 25);
        formPanel.add(lblName);

        cmbCourses = new JComboBox<>();
        cmbCourses.setFont(textFieldFont);
        cmbCourses.setBounds(fieldX, yStart + yGap, fieldWidth, fieldHeight);
        formPanel.add(cmbCourses);

        cmbCourses.addItem("-- Select Course --");
        cmbCourses.addActionListener(e -> fillInstructorFromSelection());

        JLabel lblCredits = new JLabel("Credit Hours:");
        lblCredits.setFont(labelFont);
        lblCredits.setBounds(labelX, yStart + 2 * yGap, 120, 25);
        formPanel.add(lblCredits);

        txtCredits = new JTextField();
        txtCredits.setFont(textFieldFont);
        txtCredits.setBounds(fieldX, yStart + 2 * yGap, fieldWidth, fieldHeight);
        formPanel.add(txtCredits);

        JLabel lblInstructor = new JLabel("Instructor:");
        lblInstructor.setFont(labelFont);
        lblInstructor.setBounds(labelX, yStart + 3 * yGap, 120, 25);
        formPanel.add(lblInstructor);

        txtInstructor = new JTextField();
        txtInstructor.setFont(textFieldFont);
        txtInstructor.setBounds(fieldX, yStart + 3 * yGap, fieldWidth, fieldHeight);
        formPanel.add(txtInstructor);

        add(formPanel);

        int buttonY = 340;
        int buttonWidth = 90;
        int buttonXStart = 40;
        int buttonSpacing = 100;

        JButton btnSave = styledButton("Save", new Color(46, 204, 113));
        btnSave.setBounds(buttonXStart, buttonY, buttonWidth, 35);
        add(btnSave);

        JButton btnView = styledButton("View", new Color(52, 152, 219));
        btnView.setBounds(buttonXStart + buttonSpacing, buttonY, buttonWidth, 35);
        add(btnView);

        JButton btnUpdate = styledButton("Update", new Color(241, 196, 15));
        btnUpdate.setBounds(buttonXStart + 2 * buttonSpacing, buttonY, buttonWidth, 35);
        add(btnUpdate);

        JButton btnDelete = styledButton("Delete", new Color(231, 76, 60));
        btnDelete.setBounds(buttonXStart + 3 * buttonSpacing, buttonY, buttonWidth, 35);
        add(btnDelete);

        courseModel = new DefaultTableModel();
        courseModel.setColumnIdentifiers(new String[]{"Course ID", "Course Name", "Credit Hours", "Instructor"});
        courseTable = new JTable(courseModel);
        styleTable(courseTable);

        scroll = new JScrollPane(courseTable);
        scroll.setBounds(450, 60, 550, 470);
        scroll.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                        "Course Records",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(52, 73, 94))
        );
        scroll.setVisible(false);
        add(scroll);

        // -------------------------------
        // Button Actions
        // -------------------------------
        btnSave.addActionListener(e -> addCourse());

        // ✅ TOGGLE View/Hide TABLE
        btnView.addActionListener(e -> {
            if (scroll.isVisible()) {
                scroll.setVisible(false);
                btnView.setText("View");
            } else {
                loadCoursesTable();
                scroll.setVisible(true);
                btnView.setText("Hide");
            }
            revalidate();
            repaint();
        });

        btnUpdate.addActionListener(e -> updateCourse());
        btnDelete.addActionListener(e -> deleteCourse());

        courseTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadSelection();
            }
        });
    }

    private void loadSelection() {
        int row = courseTable.getSelectedRow();
        if (row >= 0) {
            txtID.setText(courseModel.getValueAt(row, 0).toString());
            cmbCourses.setSelectedItem(courseModel.getValueAt(row, 1).toString());
            txtCredits.setText(courseModel.getValueAt(row, 2).toString());
            txtInstructor.setText(courseModel.getValueAt(row, 3).toString());
        }
    }

    private JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return b;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(25);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionBackground(new Color(52, 152, 219));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
    }

    private void clearFields() {
        txtID.setText("");
        txtCredits.setText("");
        txtInstructor.setText("");
        cmbCourses.setSelectedIndex(0);
    }

    // ----------------------------------------------
    // ComboBox Data Load + Auto-fill
    // ----------------------------------------------
    private void loadCoursesIntoComboBox() {
        try {
            cmbCourses.removeAllItems();
            cmbCourses.addItem("-- Select Course --");
            courseMap.clear();

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT course_id, course_name, credit_hours, instructor_name FROM Courses ORDER BY course_name");

            while (rs.next()) {
                int id = rs.getInt("course_id");
                String name = rs.getString("course_name");
                int credits = rs.getInt("credit_hours");
                String instructor = rs.getString("instructor_name");

                cmbCourses.addItem(name);
                courseMap.put(name, new CourseInfo(id, credits, instructor));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading course names: " + ex.getMessage());
        }
    }

    private void fillInstructorFromSelection() {
        String selectedCourse = (String) cmbCourses.getSelectedItem();
        if (selectedCourse != null && !selectedCourse.equals("-- Select Course --") && courseMap.containsKey(selectedCourse)) {
            CourseInfo info = courseMap.get(selectedCourse);
            txtID.setText(String.valueOf(info.id));
            txtCredits.setText(String.valueOf(info.credits));
            txtInstructor.setText(info.instructor);
        } else {
            txtID.setText("");
            txtCredits.setText("");
            txtInstructor.setText("");
        }
    }

    // ----------------------------------------------
    // CRUD
    // ----------------------------------------------
    private void addCourse() {
        try {
            String selectedCourse = (String) cmbCourses.getSelectedItem();

            if (selectedCourse == null || selectedCourse.equals("-- Select Course --")
                    || txtCredits.getText().trim().isEmpty() || txtInstructor.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a course and fill all fields.");
                return;
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO Courses(course_name, credit_hours, instructor_name) VALUES (?,?,?)");
            ps.setString(1, selectedCourse);
            ps.setInt(2, Integer.parseInt(txtCredits.getText()));
            ps.setString(3, txtInstructor.getText());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "✅ Course Saved Successfully!");
            loadCoursesTable();
            loadCoursesIntoComboBox();
            clearFields();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error saving course: " + ex.getMessage());
        }
    }

    private void loadCoursesTable() {
        try {
            courseModel.setRowCount(0);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Courses ORDER BY course_id");

            while (rs.next()) {
                courseModel.addRow(new Object[]{
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getInt("credit_hours"),
                        rs.getString("instructor_name")
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading courses: " + ex.getMessage());
        }
    }

    private void updateCourse() {
        try {
            if (txtID.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a course to update.");
                return;
            }

            String selectedCourse = (String) cmbCourses.getSelectedItem();

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE Courses SET course_name=?, credit_hours=?, instructor_name=? WHERE course_id=?");
            ps.setString(1, selectedCourse);
            ps.setInt(2, Integer.parseInt(txtCredits.getText()));
            ps.setString(3, txtInstructor.getText());
            ps.setInt(4, Integer.parseInt(txtID.getText()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "✅ Course Updated Successfully!");
            loadCoursesTable();
            loadCoursesIntoComboBox();
            clearFields();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating course: " + ex.getMessage());
        }
    }

    private void deleteCourse() {
        try {
            if (txtID.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please select a course to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete this course?", "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.NO_OPTION) return;

            int courseId = Integer.parseInt(txtID.getText());
            PreparedStatement psDelete = conn.prepareStatement("DELETE FROM Courses WHERE course_id=?");
            psDelete.setInt(1, courseId);
            psDelete.executeUpdate();

            JOptionPane.showMessageDialog(null, "🗑️ Course Deleted!");
            loadCoursesTable();
            loadCoursesIntoComboBox();
            clearFields();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error deleting course: " + ex.getMessage());
        }
    }
}

