package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASSWORD = "admin";
    private static Connection conn;

    // --- Method to establish connection ---
    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                // Load Oracle JDBC Driver
                Class.forName("oracle.jdbc.driver.OracleDriver");
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connected to Oracle Database successfully!");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Database connection failed: " + e.getMessage());
        }
        return conn;
    }

    // --- Method to close connection safely ---
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("🔒 Connection closed successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("⚠️ Error closing connection: " + e.getMessage());
        }
    }

    // --- MAIN METHOD for Testing ---
    public static void main(String[] args) {
        System.out.println("🔍 Testing Oracle Database Connection...");

        try (Connection con = DBConnection.getConnection()) {
            if (con != null && !con.isClosed()) {
                System.out.println("✅ Database connection test passed!");
            }
        } catch (Exception e) {
            System.out.println("❌ Database connection test failed!");
        } finally {
            DBConnection.closeConnection();
        }
    }
}
