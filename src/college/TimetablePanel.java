package college;

import DB.DBConnection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;



import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TimetablePanel extends JPanel {
    private JComboBox<String> courseBox, dayBox, timeBox, roomBox;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scroll;
    private JButton viewBtn; // made it global for toggle text

    public TimetablePanel() {
        setLayout(null);
        setBackground(new Color(236, 240, 241)); // Light background color

        // ===== Header =====
        JLabel header = new JLabel("Course Timetable Management", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setBounds(340, 15, 400, 40);
        add(header);

        // ===== Form Panel =====
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBounds(40, 70, 430, 390);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(40, 80, 120), 2),
                "Timetable Form",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(40, 80, 120)
        ));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 15);
        int y_pos = 50;
        int y_increment = 50;

        // ===== Course ComboBox =====
        JLabel lblCourse = new JLabel("Select Course:");
        lblCourse.setFont(labelFont);
        lblCourse.setBounds(30, y_pos, 120, 25);
        formPanel.add(lblCourse);
        courseBox = new JComboBox<>();
        courseBox.setBounds(150, y_pos, 220, 28);
        formPanel.add(courseBox);
        y_pos += y_increment;

        // ===== Day ComboBox =====
        JLabel lblDay = new JLabel("Select Day:");
        lblDay.setFont(labelFont);
        lblDay.setBounds(30, y_pos, 120, 25);
        formPanel.add(lblDay);
        dayBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        dayBox.setBounds(150, y_pos, 220, 28);
        formPanel.add(dayBox);
        y_pos += y_increment;

        // ===== Time ComboBox =====
        JLabel lblTime = new JLabel("Select Time:");
        lblTime.setFont(labelFont);
        lblTime.setBounds(30, y_pos, 120, 25);
        formPanel.add(lblTime);
        timeBox = new JComboBox<>(new String[]{
                "08:00 - 09:00 AM", "09:00 - 10:00 AM",
                "10:00 - 11:00 AM", "11:00 - 12:00 PM",
                "01:00 - 02:00 PM", "02:00 - 03:00 PM",
                "03:00 - 04:00 PM"
        });
        timeBox.setBounds(150, y_pos, 220, 28);
        formPanel.add(timeBox);
        y_pos += y_increment;

        // ===== Room ComboBox =====
        JLabel lblRoom = new JLabel("Select Room:");
        lblRoom.setFont(labelFont);
        lblRoom.setBounds(30, y_pos, 120, 25);
        formPanel.add(lblRoom);
        roomBox = new JComboBox<>(new String[]{"A101", "A102", "B201", "B202", "C301"});
        roomBox.setBounds(150, y_pos, 220, 28);
        formPanel.add(roomBox);

        add(formPanel);

        // ===== Buttons =====
        int buttonY = 475;

        JButton addBtn = styledButton("Add Timetable", new Color(46, 204, 113));
        addBtn.setBounds(40, buttonY, 120, 35);
        add(addBtn);

        viewBtn = styledButton("View Timetables", new Color(52, 152, 219));
        viewBtn.setBounds(170, buttonY, 130, 35);
        add(viewBtn);

        JButton deleteBtn = styledButton("Delete Selected", new Color(231, 76, 60));
        deleteBtn.setBounds(310, buttonY, 130, 35);
        add(deleteBtn);

        // ===== Table Section =====
        String[] columns = {"Timetable ID", "Course", "Day", "Time", "Room"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        styleTable(table);

        scroll = new JScrollPane(table);
        scroll.setBounds(500, 70, 430, 390);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 73, 94), 2),
                "Timetable Records",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(52, 73, 94)
        ));
        scroll.setVisible(false);
        add(scroll);

        // ===== Load courses =====
        loadCourses();

        // ===== Button Actions =====
        addBtn.addActionListener(e -> addTimetable());
        viewBtn.addActionListener(e -> toggleTableVisibility());
        deleteBtn.addActionListener(e -> deleteSelectedTimetable());
    }

    // ---------- STYLE HELPERS ----------
    private JButton styledButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return b;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(25);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setSelectionBackground(new Color(41, 128, 185));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
    }

    // ---------- Load courses ----------
    private void loadCourses() {
        courseBox.removeAllItems();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COURSE_NAME FROM COURSES")) {
            while (rs.next()) {
                courseBox.addItem(rs.getString("COURSE_NAME"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error loading courses: " + e.getMessage());
        }
    }

    // ---------- Add timetable ----------
    private void addTimetable() {
        String courseName = (String) courseBox.getSelectedItem();
        String day = (String) dayBox.getSelectedItem();
        String time = (String) timeBox.getSelectedItem();
        String room = (String) roomBox.getSelectedItem();

        if (courseName == null || day == null || time == null || room == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Please fill all fields!");
            return;
        }

        String[] timeParts = time.split(" - ");
        String startTime = timeParts[0].trim();
        String endTime = timeParts[1].trim();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO TIMETABLE (COURSE_ID, CLASS_DAY, START_TIME, END_TIME, ROOM_NO) " +
                             "VALUES ((SELECT COURSE_ID FROM COURSES WHERE COURSE_NAME=?), ?, ?, ?, ?)")) {

            ps.setString(1, courseName);
            ps.setString(2, day);
            ps.setString(3, startTime);
            ps.setString(4, endTime);
            ps.setString(5, room);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "✅ Timetable added successfully!");

            // Refresh table if visible
            if (scroll.isVisible()) viewTimetables();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error adding timetable: " + e.getMessage());
        }
    }

    // ---------- View timetable ----------
    private void viewTimetables() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT t.TIMETABLE_ID, c.COURSE_NAME, t.CLASS_DAY, " +
                             "t.START_TIME || ' - ' || t.END_TIME AS TIME_SLOT, t.ROOM_NO " +
                             "FROM TIMETABLE t " +
                             "JOIN COURSES c ON t.COURSE_ID = c.COURSE_ID " +
                             "WHERE t.CLASS_DAY IN ('Monday','Tuesday','Wednesday','Thursday','Friday') " +
                             "ORDER BY DECODE(t.CLASS_DAY,'Monday',1,'Tuesday',2,'Wednesday',3,'Thursday',4,'Friday',5), t.START_TIME"
             )) {
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                model.addRow(new Object[]{
                        rs.getInt("TIMETABLE_ID"),
                        rs.getString("COURSE_NAME"),
                        rs.getString("CLASS_DAY"),
                        rs.getString("TIME_SLOT"),
                        rs.getString("ROOM_NO")
                });
            }
            if (!hasData) {
                JOptionPane.showMessageDialog(this, "ℹ️ No timetable data found for Monday–Friday.");
            }
            scroll.setVisible(true);
            viewBtn.setText("Hide Timetables");
            revalidate();
            repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error viewing timetables: " + e.getMessage());
        }
    }

    // ---------- Toggle Table Visibility ----------
    private void toggleTableVisibility() {
        if (scroll.isVisible()) {
            scroll.setVisible(false);
            viewBtn.setText("View Timetables");
            revalidate();
            repaint();
        } else {
            viewTimetables();
        }
    }

    // ---------- Delete selected timetable ----------
    private void deleteSelectedTimetable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select a timetable row to delete!");
            return;
        }

        int timetableId = (int) model.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this timetable entry?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM TIMETABLE WHERE TIMETABLE_ID = ?")) {

            ps.setInt(1, timetableId);
            int deleted = ps.executeUpdate();

            if (deleted > 0) {
                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "✅ Timetable deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Could not delete timetable from database!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error deleting timetable: " + e.getMessage());
        }
    }
}
