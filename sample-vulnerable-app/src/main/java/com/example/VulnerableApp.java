package com.example;

import java.sql.*;
import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Vulnerable Demo Application for GHAS Testing
 * 
 * This class intentionally contains security vulnerabilities to demonstrate
 * GitHub Advanced Security features like CodeQL scanning.
 * 
 * WARNING: This code is for testing purposes only and should never be used in production!
 */
public class VulnerableApp {
    
    private static final Logger logger = LogManager.getLogger(VulnerableApp.class);
    
    // Hardcoded secrets (will be detected by secret scanning)
    private static final String API_KEY = "sk-1234567890abcdef1234567890abcdef12345678";
    private static final String DATABASE_PASSWORD = "admin123!@#";
    private static final String AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
    private static final String AWS_SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    
    // Database connection details (hardcoded credentials)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password123";
    
    public static void main(String[] args) {
        VulnerableApp app = new VulnerableApp();
        
        // Simulate various vulnerable operations
        app.demonstrateVulnerabilities();
    }
    
    public void demonstrateVulnerabilities() {
        System.out.println("=== Vulnerable Demo App Started ===");
        
        // SQL Injection vulnerability
        String userId = "1 OR 1=1"; // Simulated malicious input
        getUserData(userId);
        
        // Command injection vulnerability
        String filename = "test.txt; rm -rf /"; // Simulated malicious input
        readFile(filename);
        
        // Path traversal vulnerability
        String path = "../../../etc/passwd"; // Simulated malicious input
        accessFile(path);
        
        // Weak cryptography
        generateWeakPassword();
        
        // Information disclosure
        logSensitiveData();
        
        System.out.println("=== Demo completed ===");
    }
    
    /**
     * SQL Injection vulnerability - CodeQL will detect this
     */
    public void getUserData(String userId) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            
            // VULNERABLE: Direct string concatenation in SQL query
            String query = "SELECT * FROM users WHERE id = " + userId;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                System.out.println("User: " + rs.getString("username"));
            }
            
            conn.close();
        } catch (SQLException e) {
            // Poor error handling - exposes stack trace
            e.printStackTrace();
        }
    }
    
    /**
     * Command Injection vulnerability - CodeQL will detect this
     */
    public void readFile(String filename) {
        try {
            // VULNERABLE: Direct execution of user input
            Process process = Runtime.getRuntime().exec("cat " + filename);
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Path Traversal vulnerability - CodeQL will detect this
     */
    public void accessFile(String userPath) {
        try {
            // VULNERABLE: No validation of file path
            File file = new File("/var/www/html/" + userPath);
            
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[1024];
                fis.read(data);
                fis.close();
                
                System.out.println("File content: " + new String(data));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Weak cryptography - CodeQL will detect this
     */
    public void generateWeakPassword() {
        // VULNERABLE: Using weak random number generator
        Random random = new Random(System.currentTimeMillis());
        
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append((char) ('a' + random.nextInt(26)));
        }
        
        System.out.println("Generated password: " + password.toString());
    }
    
    /**
     * Information disclosure - CodeQL will detect this
     */
    public void logSensitiveData() {
        // VULNERABLE: Logging sensitive information
        logger.info("User logged in with API key: " + API_KEY);
        logger.info("Database password: " + DATABASE_PASSWORD);
        logger.info("AWS credentials: " + AWS_ACCESS_KEY + ":" + AWS_SECRET_KEY);
    }
    
    /**
     * HTTP Response Splitting vulnerability - CodeQL will detect this
     */
    public void handleUserInput(HttpServletRequest request, HttpServletResponse response) {
        try {
            String userInput = request.getParameter("redirect");
            
            // VULNERABLE: Direct use of user input in HTTP header
            response.sendRedirect(userInput);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * XML External Entity (XXE) vulnerability - CodeQL will detect this
     */
    public void parseXML(String xmlContent) {
        try {
            // VULNERABLE: XML parser without security features disabled
            javax.xml.parsers.DocumentBuilderFactory factory = 
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
            
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            
            java.io.StringReader reader = new java.io.StringReader(xmlContent);
            org.xml.sax.InputSource source = new org.xml.sax.InputSource(reader);
            
            builder.parse(source);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Insecure deserialization - CodeQL will detect this
     */
    public void deserializeData(byte[] data) {
        try {
            // VULNERABLE: Deserializing untrusted data
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            
            Object obj = ois.readObject();
            System.out.println("Deserialized: " + obj.toString());
            
            ois.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
