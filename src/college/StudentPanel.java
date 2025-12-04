package college;

import DB.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color PANEL_BACKGROUND_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Color TEXT_LABEL_COLOR = new Color(51, 65, 85);

    private static final Color SAVE_COLOR = new Color(0, 153, 102);
    private static final Color VIEW_COLOR = new Color(70, 130, 180);
    private static final Color UPDATE_COLOR = new Color(255, 140, 0);
    private static final Color DELETE_COLOR = new Color(220, 53, 69);
    private static final Color ENROLL_COLOR = new Color(102, 51, 153);

    private JTable studentTable, courseTable;
    private DefaultTableModel studentModel, courseModel;
    private JTextField txtID, txtName, txtEmail, txtDept, txtContact;
    private JComboBox<String> courseCombo;

    public StudentPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        connectDB();

        studentModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Department", "Contact"}, 0);
        courseModel = new DefaultTableModel(new String[]{"Course ID", "Course Name"}, 0);

        initComponents();
    }

    private void connectDB() {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn == null)
                throw new SQLException("Connection is null.");
            System.out.println("✅ Connected to DB!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "DB Connection Failed: " + e.getMessage());
        }
    }

    private void initComponents() {
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northPanel.setBackground(BACKGROUND_COLOR);
        JLabel lblTitle = new JLabel("Student Enrollment Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_LABEL_COLOR);
        northPanel.add(lblTitle);
        add(northPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));
        centerPanel.setBackground(BACKGROUND_COLOR);

        JPanel formPanel = createFormPanel();
        JPanel tablesPanel = createTablesPanel();

        tablesPanel.setVisible(false); // initially hidden

        centerPanel.add(formPanel);
        centerPanel.add(tablesPanel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel(tablesPanel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        Border fieldBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        );

        JLabel[] labels = {
                new JLabel("Student ID:"), new JLabel("Full Name:"),
                new JLabel("Email:"), new JLabel("Department:"),
                new JLabel("Contact No:"), new JLabel("Enroll Course:")
        };
        JTextField[] fields = {txtID = new JTextField(20), txtName = new JTextField(20),
                txtEmail = new JTextField(20), txtDept = new JTextField(20),
                txtContact = new JTextField(20)};

        for (JLabel lbl : labels) lbl.setFont(labelFont);

        for (int i = 0; i < 5; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            formPanel.add(labels[i], gbc);
            gbc.gridx = 1;
            fields[i].setBorder(fieldBorder);
            formPanel.add(fields[i], gbc);
        }

        txtID.setEditable(false);
        txtID.setBackground(new Color(245, 245, 245));

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(labels[5], gbc);

        gbc.gridx = 1;
        courseCombo = new JComboBox<>();
        courseCombo.setBorder(fieldBorder);
        loadCoursesToCombo();
        formPanel.add(courseCombo, gbc);

        return formPanel;
    }

    private JPanel createTablesPanel() {
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        tablesPanel.setBackground(BACKGROUND_COLOR);

        Border border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                "Records", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.PLAIN, 14), new Color(107, 114, 128));

        studentTable = new JTable(studentModel);
        styleTable(studentTable);
        JScrollPane studentScroll = new JScrollPane(studentTable);
        studentScroll.setBorder(border);
        tablesPanel.add(studentScroll);

        courseTable = new JTable(courseModel);
        styleTable(courseTable);
        JScrollPane courseScroll = new JScrollPane(courseTable);
        courseScroll.setBorder(border);
        tablesPanel.add(courseScroll);

        studentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadSelection();
                loadEnrolledCourses();
            }
        });

        return tablesPanel;
    }

    // ✅ Updated: Toggle View/Hide functionality added here
    private JPanel createButtonPanel(JPanel tablesPanel) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton btnSave = styledButton("Save", SAVE_COLOR);
        JButton btnView = styledButton("View", VIEW_COLOR);
        JButton btnUpdate = styledButton("Update", UPDATE_COLOR);
        JButton btnDelete = styledButton("Delete", DELETE_COLOR);
        JButton btnEnroll = styledButton("Enroll", ENROLL_COLOR);

        buttonPanel.add(btnSave);
        buttonPanel.add(btnView);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnEnroll);

        // 🔁 View Button Toggle
        btnView.addActionListener(e -> {
            boolean isVisible = tablesPanel.isVisible();
            if (isVisible) {
                tablesPanel.setVisible(false);
                btnView.setText("View");
            } else {
                loadStudentsTable();
                tablesPanel.setVisible(true);
                btnView.setText("Hide");
            }
            revalidate();
            repaint();
        });

        // Save -> auto show table
        btnSave.addActionListener(e -> {
            saveStudent();
            loadStudentsTable();
            tablesPanel.setVisible(true);
            btnView.setText("Hide");
        });

        btnUpdate.addActionListener(e -> {
            updateStudent();
            loadStudentsTable();
        });

        btnDelete.addActionListener(e -> {
            deleteStudent();
            loadStudentsTable();
        });

        btnEnroll.addActionListener(e -> enrollStudentInCourse());

        return buttonPanel;
    }

    private JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(100, 35));
        return b;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(25);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(220, 230, 255));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(51, 65, 85));
        table.getTableHeader().setForeground(Color.WHITE);
    }

    // ---------- DB Functions ----------
    private void loadStudentsTable() {
        studentModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT student_id, name, email, department, contact_no FROM students ORDER BY student_id")) {
            while (rs.next()) {
                studentModel.addRow(new Object[]{
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getString("contact_no")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load Error: " + e.getMessage());
        }
    }

    private void loadCoursesToCombo() {
        courseCombo.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT course_id, course_name FROM courses")) {
            while (rs.next()) {
                courseCombo.addItem(rs.getString("course_id") + " - " + rs.getString("course_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Courses Load Error: " + e.getMessage());
        }
    }

    private void loadSelection() {
        int row = studentTable.getSelectedRow();
        if (row < 0) return;
        txtID.setText(studentModel.getValueAt(row, 0).toString());
        txtName.setText(studentModel.getValueAt(row, 1).toString());
        txtEmail.setText(studentModel.getValueAt(row, 2).toString());
        txtDept.setText(studentModel.getValueAt(row, 3).toString());
        txtContact.setText(studentModel.getValueAt(row, 4).toString());
    }

    private void loadEnrolledCourses() {
        courseModel.setRowCount(0);
        if (txtID.getText().isEmpty()) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT c.course_id, c.course_name FROM courses c JOIN enrollments e ON c.course_id = e.course_id WHERE e.student_id=?")) {
            ps.setInt(1, Integer.parseInt(txtID.getText()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                courseModel.addRow(new Object[]{rs.getString(1), rs.getString(2)});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Enrolled Courses Load Error: " + e.getMessage());
        }
    }

    private void saveStudent() {
        if (txtName.getText().isEmpty() || txtEmail.getText().isEmpty() ||
                txtDept.getText().isEmpty() || txtContact.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Please fill all fields!");
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO students (name, email, department, contact_no) VALUES (?, ?, ?, ?)",
                     new String[]{"student_id"})) {

            ps.setString(1, txtName.getText());
            ps.setString(2, txtEmail.getText());
            ps.setString(3, txtDept.getText());
            ps.setString(4, txtContact.getText());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int newId = -1;
            if (rs.next()) newId = rs.getInt(1);

            if (newId != -1) {
                studentModel.addRow(new Object[]{
                        newId,
                        txtName.getText(),
                        txtEmail.getText(),
                        txtDept.getText(),
                        txtContact.getText()
                });
            }

            JOptionPane.showMessageDialog(this, "✅ Student saved successfully!");
            clearForm();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Save Error: " + e.getMessage());
        }
    }

    private void updateStudent() {
        if (txtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a student first!");
            return;
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE students SET name=?, email=?, department=?, contact_no=? WHERE student_id=?")) {
            ps.setString(1, txtName.getText());
            ps.setString(2, txtEmail.getText());
            ps.setString(3, txtDept.getText());
            ps.setString(4, txtContact.getText());
            ps.setInt(5, Integer.parseInt(txtID.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Updated successfully!");
            clearForm();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Update Error: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        if (txtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a student first!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE student_id=?")) {
            ps.setInt(1, Integer.parseInt(txtID.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Deleted successfully!");
            clearForm();
            loadStudentsTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + e.getMessage());
        }
    }

    private void enrollStudentInCourse() {
        if (txtID.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a student first!");
            return;
        }
        String selected = (String) courseCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course!");
            return;
        }
        String courseId = selected.split(" - ")[0];
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)")) {
            ps.setInt(1, Integer.parseInt(txtID.getText()));
            ps.setString(2, courseId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student enrolled!");
            loadEnrolledCourses();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Enroll Error: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtID.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtDept.setText("");
        txtContact.setText("");
        studentTable.clearSelection();
        courseModel.setRowCount(0);
    }
}
