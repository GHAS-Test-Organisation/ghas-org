package com.example;

import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;

/**
 * Database helper class with additional security vulnerabilities
 */
public class DatabaseHelper {
    
    // More hardcoded secrets
    private static final String ADMIN_PASSWORD = "admin123!@#$";
    private static final String API_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    
    /**
     * Another SQL injection vulnerability
     */
    public User findUserByEmail(String email) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            
            // VULNERABLE: SQL injection via string concatenation
            String sql = "SELECT * FROM users WHERE email = '" + email + "'";
            rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password")); // Storing plain text passwords!
                return user;
            }
            
        } catch (SQLException e) {
            // Poor error handling - exposes sensitive information
            System.err.println("Database error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        } finally {
            // Resource leak - not properly closing connections
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                // Intentionally not closing connection to demonstrate resource leak
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     * Command injection vulnerability in backup function
     */
    public void backupDatabase(String backupPath) {
        try {
            // VULNERABLE: Command injection
            String command = "mysqldump -u root -p" + ADMIN_PASSWORD + " testdb > " + backupPath;
            Process process = Runtime.getRuntime().exec(command);
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Backup completed successfully");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Insecure connection method
     */
    private Connection getConnection() throws SQLException {
        // VULNERABLE: Hardcoded credentials and insecure connection
        String url = "jdbc:mysql://localhost:3306/testdb?useSSL=false&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "password123";
        
        return DriverManager.getConnection(url, username, password);
    }
    
    /**
     * Weak authentication check
     */
    public boolean authenticateUser(String username, String password) {
        // VULNERABLE: Timing attack vulnerability
        User user = findUserByEmail(username);
        
        if (user != null) {
            // VULNERABLE: Plain text password comparison
            return user.getPassword().equals(password);
        }
        
        return false;
    }
    
    /**
     * Insecure configuration loading
     */
    public void loadConfiguration() {
        try {
            Properties props = new Properties();
            
            // VULNERABLE: Loading configuration from user-controlled path
            String configPath = System.getProperty("config.path", "config.properties");
            FileInputStream fis = new FileInputStream(configPath);
            
            props.load(fis);
            
            // VULNERABLE: Logging sensitive configuration
            System.out.println("Loaded configuration: " + props.toString());
            
            fis.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Simple User class for demonstration
    public static class User {
        private int id;
        private String email;
        private String password;
        
        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
