package college;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;


public class Dashboard extends JFrame {
    private final JPanel contentPanel;
    private JButton activeButton = null; // Track active button

    public Dashboard() {
        setTitle("College Course Enrollment & Timetable Management System");
        setSize(1280, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Background =====
        JPanel bgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                // Very light blue gradient background
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(245, 250, 255),
                        getWidth(), getHeight(), new Color(250, 253, 255)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(bgPanel);
        // ===== Header =====
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(1280, 75));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        // Title in CENTER
        Color titleBlue = new Color(40,80,120);
        JLabel title = new JLabel(" College Course Enrollment & Timetable Management", JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(titleBlue);

        // ✅ IMPORTANT — Add title IN CENTER
        header.add(title, BorderLayout.CENTER);

        // --- User profile section (MODIFIED) ---
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        // 3. Lowering the Welcome section (20 pixels padding from top)
        userPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel welcomeLabel = new JLabel("Welcome, Admin");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // 4. Welcome text color adjusted for white background
        welcomeLabel.setForeground(Color.DARK_GRAY);

        JLabel userIcon = new JLabel(loadIcon("/icons/user (1).png"));
        userIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        userPanel.add(welcomeLabel);
        userPanel.add(userIcon);

        // Add components to header
        // The title is now in the CENTER, making it centered visually.
        header.add(title, BorderLayout.CENTER);
        header.add(userPanel, BorderLayout.EAST);
        bgPanel.add(header, BorderLayout.NORTH);
        // ===== End of Header =====

        // ===== Sidebar =====
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(250, 252, 255)); // Very light blue sidebar
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 230, 240)));

        // Logo and Name
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(new Color(250, 252, 255));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 30, 25));

        JLabel logoLabel = new JLabel(loadIcon("/icons/graduation-hat (3).png"));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("College Portal", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(new Color(60, 110, 180));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(120, 140, 160));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(logoLabel);
        logoPanel.add(Box.createVerticalStrut(15));
        logoPanel.add(nameLabel);
        logoPanel.add(subtitleLabel);
        sidebar.add(logoPanel);

        // ===== Sidebar Buttons =====
        JButton btnDashboard = createMenuButton("Dashboard", "/icons/home (1).png", "Go to Dashboard");
        JButton btnStudent = createMenuButton("Students", "/icons/user (1).png", "Manage Students");
        JButton btnCourse = createMenuButton("Courses", "/icons/open-book.png", "Manage Courses");
        JButton btnEnroll = createMenuButton("Enrollments", "/icons/add-user (4).png", "Manage Enrollments");
        JButton btnTimetable = createMenuButton("Timetable", "/icons/timetable (1).png", "View Timetable");
        JButton btnAbout = createMenuButton("About Project", "/icons/information-button.png", "About this Project");
        JButton btnExit = createMenuButton("Logout", "/icons/turn-off.png", "Logout and return to Login");

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnDashboard); sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnStudent);   sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnCourse);    sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnEnroll);    sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnTimetable); sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnAbout);     sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnExit);      sidebar.add(Box.createVerticalStrut(15));

        bgPanel.add(sidebar, BorderLayout.WEST);

        // ===== Content Panel =====
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        bgPanel.add(contentPanel, BorderLayout.CENTER);

        // ===== Button Actions =====
        btnDashboard.addActionListener(_ -> { setActiveButton(btnDashboard); showHomePanel(btnStudent, btnCourse, btnEnroll, btnTimetable); });
        btnStudent.addActionListener(_ -> { setActiveButton(btnStudent); showPanel(new StudentPanel()); });
        btnCourse.addActionListener(_ -> { setActiveButton(btnCourse); showPanel(new CoursePanel()); });
        btnEnroll.addActionListener(_ -> { setActiveButton(btnEnroll); showPanel(new EnrolmentPanel()); });
        btnTimetable.addActionListener(_ -> { setActiveButton(btnTimetable); showPanel(new TimetablePanel()); });
        btnAbout.addActionListener(_ -> { setActiveButton(btnAbout); showAboutDialog(); });
        btnExit.addActionListener(_ -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginPage().setVisible(true);
            }
        });

        // Set default active button
        setActiveButton(btnDashboard);
        showHomePanel(btnStudent, btnCourse, btnEnroll, btnTimetable);
    }

    // ===== Menu Buttons =====
    private JButton createMenuButton(String text, String iconPath, String tooltip) {
        JButton btn = new JButton(" " + text, loadIcon(iconPath));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(70, 90, 110));
        btn.setBackground(new Color(250, 252, 255));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(260, 45));
        btn.setToolTipText(tooltip);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != activeButton)
                    btn.setBackground(new Color(240, 245, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != activeButton)
                    btn.setBackground(new Color(250, 252, 255));
            }
        });
        return btn;
    }

    // ===== Active Button =====
    private void setActiveButton(JButton btn) {
        if (activeButton != null) {
            activeButton.setBackground(new Color(250, 252, 255));
            activeButton.setForeground(new Color(70, 90, 110));
        }
        activeButton = btn;
        activeButton.setBackground(new Color(60, 110, 180));
        activeButton.setForeground(Color.WHITE);
    }

    // ===== Load Icon =====
    private ImageIcon loadIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) return new ImageIcon(imgURL);
        else {
            System.err.println("⚠️ Icon not found: " + path);
            return new ImageIcon();
        }
    }

    // ===== Show Home Panel with Clickable Cards =====
    private void showHomePanel(JButton btnStudent, JButton btnCourse, JButton btnEnroll, JButton btnTimetable) {
        contentPanel.removeAll();

        // Main dashboard panel
        JPanel dashboard = new JPanel(new BorderLayout(25, 25));
        dashboard.setOpaque(false);

        // WelcomeSection
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel welcomeLabel = new JLabel("Dashboard Overview");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(new Color(50, 80, 120));

        JLabel dateLabel = new JLabel(" " + LocalDate.now());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(100, 120, 140));

        welcomePanel.add(welcomeLabel, BorderLayout.WEST);
        welcomePanel.add(dateLabel, BorderLayout.EAST);

        dashboard.add(welcomePanel, BorderLayout.NORTH);

        // Statistics Cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        cardsPanel.add(createDashboardCard("Total Students", "1,248", "/icons/user (1).png",
                new Color(74, 144, 226), () -> {
                    setActiveButton(btnStudent);
                    showPanel(new StudentPanel());
                }));
        cardsPanel.add(createDashboardCard(" Available Courses", "42", "/icons/open-book.png",
                new Color(65, 179, 163), () -> {
                    setActiveButton(btnCourse);
                    showPanel(new CoursePanel());
                }));
        cardsPanel.add(createDashboardCard(" Active Enrollments", "2,847", "/icons/add-user (4).png",
                new Color(237, 137, 54), () -> {
                    setActiveButton(btnEnroll);
                    showPanel(new EnrolmentPanel());
                }));
        cardsPanel.add(createDashboardCard(" Classes Today", "18", "/icons/timetable (1).png",
                new Color(155, 103, 211), () -> {
                    setActiveButton(btnTimetable);
                    showPanel(new TimetablePanel());
                }));

        dashboard.add(cardsPanel, BorderLayout.CENTER);

        // ===== Timetable Section =====
        JPanel timetableSection = new JPanel(new BorderLayout());
        timetableSection.setOpaque(false);
        timetableSection.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Timetable Header
        JPanel timetableHeader = new JPanel(new BorderLayout());
        timetableHeader.setOpaque(false);
        timetableHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel timetableTitle = new JLabel(" Today's Timetable!");
        timetableTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        timetableTitle.setForeground(new Color(50, 80, 120));

        JLabel viewAllLabel = new JLabel("View Full Timetable →");
        viewAllLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        viewAllLabel.setForeground(new Color(60, 110, 180));
        viewAllLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        timetableHeader.add(timetableTitle, BorderLayout.WEST);
        timetableHeader.add(viewAllLabel, BorderLayout.EAST);

        // Timetable Table
        String[] columns = {"Course", "Time", "Room"};
        Object[][] data = {
                {"Software Requirement Engineering", "08:00 - 09:30", "A101"},
                {"DSA ", "09:00 - 10:00", "B201"},
                {"Compiler Construction", "10:00 - 11:00", "C101"},
                {"Operating Systems", "11:00 - 12:30", "Lab A"},
                {"Lunch Break", "12:30 - 13:30", "-"},
                {"Database Systems", "13:30 - 14:30", "D301"}
        };

        JTable table = new JTable(data, columns) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    if (row % 2 == 0) {
                        comp.setBackground(new Color(248, 250, 252));
                    } else {
                        comp.setBackground(Color.WHITE);
                    }
                }
                return comp;
            }
        };

        table.setFillsViewportHeight(true);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Style header
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(60, 110, 180));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 45));

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 240), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setPreferredSize(new Dimension(800, 250));

        timetableSection.add(timetableHeader, BorderLayout.NORTH);
        timetableSection.add(tableScroll, BorderLayout.CENTER);

        dashboard.add(timetableSection, BorderLayout.SOUTH);

        contentPanel.add(dashboard, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ===== Dashboard Card =====
    private JPanel createDashboardCard(String title, String count, String iconPath, Color bgColor, Runnable onClick) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(220, 150));
        card.setBackground(bgColor);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Top section with icon
        JLabel iconLabel = new JLabel(loadIcon(iconPath));
        iconLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Center section with count
        JLabel countLabel = new JLabel(count, SwingConstants.LEFT);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        countLabel.setForeground(Color.WHITE);

        // Bottom section with title
        JLabel titleLabel = new JLabel(title, SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) { onClick.run(); }
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(
                        Math.min(bgColor.getRed() + 15, 255),
                        Math.min(bgColor.getGreen() + 15, 255),
                        Math.min(bgColor.getBlue() + 15, 255)
                ));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE, 2),
                        BorderFactory.createEmptyBorder(18, 18, 18, 18)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(bgColor);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
        });

        return card;
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                """
                🎓 College Course Enrolment & Timetable Management System
                Version 2.0
                
                Developed using Java Swing with professional enterprise standards.
                
                Modules Included:
                • Student Management
                • Course Management
                • Enrollment Management
                • Timetable Scheduling
                
                © 2025 College Portal. All rights reserved.
                """,
                "About System", JOptionPane.INFORMATION_MESSAGE);
    }

    static void main() {
        try {
            UIManager.setLookAndFeel((LookAndFeel) null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
        });
    }

    private static String getUI() {
        return null;
    }
}