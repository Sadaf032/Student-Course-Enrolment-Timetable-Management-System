package college;

import DB.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

import java.awt.event.*;

public class LoginPage extends JFrame {

    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login - College Course Enrolment System");
        setSize(650, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // ---------- LEFT SIDE PANEL (Dashboard-style) ----------
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(245, 250, 255),
                        getWidth(), getHeight(), new Color(250, 253, 255)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(230, 0));
        leftPanel.setLayout(new GridBagLayout());

        JLabel logoLabel = new JLabel("🎓");
        logoLabel.setFont(new Font("Poppins", Font.PLAIN, 90));
        logoLabel.setForeground(new Color(40, 80, 120));
        leftPanel.add(logoLabel);
        add(leftPanel, BorderLayout.WEST);

        // ---------- RIGHT SIDE LOGIN PANEL ----------
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        // ---------- Title ----------
        JLabel title = new JLabel("🔐 Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("Poppins", Font.BOLD, 22));
        title.setForeground(new Color(40, 60, 90));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(30));

        // ---------- Username ----------
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(200, 35));
        usernameField.setMaximumSize(new Dimension(300, 40));
        usernameField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                "Username", 0, 0,
                new Font("Segoe UI", Font.PLAIN, 11),
                new Color(100, 100, 100)
        ));

        // ---------- Password ----------
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 35));
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                "Password", 0, 0,
                new Font("Segoe UI", Font.PLAIN, 11),
                new Color(100, 100, 100)
        ));

        // ---------- Buttons Panel ----------
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        JButton loginBtn = createStyledButton("Login", new Color(70, 130, 180), new Color(60, 120, 170));
        JButton exitBtn = createStyledButton("Exit", new Color(200, 70, 70), new Color(180, 50, 50));

        loginBtn.addActionListener(_ -> login());
        exitBtn.addActionListener(_ -> System.exit(0));

        buttonPanel.add(loginBtn);
        buttonPanel.add(exitBtn);

        // ---------- Add Components ----------
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(30));
        panel.add(buttonPanel);

        add(panel, BorderLayout.CENTER);
    }

    // ---------- Helper Method to Create Buttons ----------
    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 38));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    // ---------- Login Logic ----------
    private void login() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM USERS WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "✅ Login Successful!");
                new Dashboard().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid Username or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Error: " + ex.getMessage());
        }
    }

    // ---------- Run ----------
    static void main() {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
