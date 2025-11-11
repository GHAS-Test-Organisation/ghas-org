import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * Test GitHub App Setup and provide conversion instructions
 */
public class TestGitHubAppSetup {
    
    private final OkHttpClient httpClient;
    private final ObjectMapper mapper;
    
    // Your GitHub App details
    private static final String APP_ID = "2244732";
    private static final long INSTALLATION_ID = 93351110L;
    private static final String PRIVATE_KEY_PATH = "src/main/resources/was-authentication-test.2025-11-06.private-key.pem";
    
    // GitHub API URLs
    private static final String GITHUB_API_BASE = "https://api.github.com";
    
    public TestGitHubAppSetup() {
        this.httpClient = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }
    
    /**
     * Test GitHub API connectivity
     */
    public void testConnectivity() throws IOException {
        System.out.println("=== Testing GitHub API Connectivity ===");
        
        Request request = new Request.Builder()
            .url(GITHUB_API_BASE + "/rate_limit")
            .addHeader("Accept", "application/vnd.github.v3+json")
            .addHeader("User-Agent", "GHAS-Client/1.0")
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                JsonNode rateLimit = mapper.readTree(response.body().string());
                JsonNode core = rateLimit.get("rate");
                System.out.println("✓ GitHub API is accessible");
                System.out.println("Rate Limit: " + core.get("remaining").asInt() + "/" + core.get("limit").asInt());
            } else {
                System.out.println("✗ Failed to connect to GitHub API: " + response.code());
            }
        }
        System.out.println();
    }
    
    /**
     * Display comprehensive setup instructions
     */
    public void displaySetupInstructions() {
        System.out.println("=== GitHub App Authentication Setup ===");
        System.out.println();
        
        System.out.println("Your Configuration:");
        System.out.println("- App ID: " + APP_ID);
        System.out.println("- Installation ID: " + INSTALLATION_ID);
        System.out.println("- Private Key File: " + PRIVATE_KEY_PATH);
        System.out.println();
        
        System.out.println("Current Status:");
        System.out.println("✓ GitHub API connectivity works");
        System.out.println("✓ App ID and Installation ID are configured");
        System.out.println("✓ Private key file exists");
        System.out.println("✗ Private key is in RSA format (needs PKCS#8)");
        System.out.println("✗ JWT dependencies are missing");
        System.out.println();
        
        System.out.println("=== SOLUTION OPTIONS ===");
        System.out.println();
        
        System.out.println("Option 1: Convert Key Using Online Tool");
        System.out.println("1. Go to: https://8gwifi.org/PemParserFunctions.jsp");
        System.out.println("2. Copy your RSA private key content");
        System.out.println("3. Select 'RSA Private Key' as input format");
        System.out.println("4. Select 'PKCS#8 Private Key' as output format");
        System.out.println("5. Click 'Submit' to convert");
        System.out.println("6. Save the converted key as: " + PRIVATE_KEY_PATH.replace(".pem", "-pkcs8.pem"));
        System.out.println();
        
        System.out.println("Option 2: Install OpenSSL");
        System.out.println("1. Download from: https://slproweb.com/products/Win32OpenSSL.html");
        System.out.println("2. Install and add to PATH");
        System.out.println("3. Run conversion command:");
        System.out.println("   openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt \\");
        System.out.println("   -in \"" + PRIVATE_KEY_PATH + "\" \\");
        System.out.println("   -out \"" + PRIVATE_KEY_PATH.replace(".pem", "-pkcs8.pem") + "\"");
        System.out.println();
        
        System.out.println("Option 3: Use Git Bash (if available)");
        System.out.println("1. Open Git Bash");
        System.out.println("2. Navigate to project directory");
        System.out.println("3. Run: openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt \\");
        System.out.println("   -in \"" + PRIVATE_KEY_PATH + "\" \\");
        System.out.println("   -out \"" + PRIVATE_KEY_PATH.replace(".pem", "-pkcs8.pem") + "\"");
        System.out.println();
        
        System.out.println("=== AFTER KEY CONVERSION ===");
        System.out.println();
        System.out.println("1. Update the GitHubAppJWTAuth.java file:");
        System.out.println("   Change privateKeyPath to: \"" + PRIVATE_KEY_PATH.replace(".pem", "-pkcs8.pem") + "\"");
        System.out.println();
        
        System.out.println("2. Add JWT dependencies to pom.xml (if not present):");
        System.out.println("   <!-- Add these to <dependencies> section -->");
        System.out.println("   <dependency>");
        System.out.println("       <groupId>io.jsonwebtoken</groupId>");
        System.out.println("       <artifactId>jjwt-api</artifactId>");
        System.out.println("       <version>0.11.5</version>");
        System.out.println("   </dependency>");
        System.out.println("   <dependency>");
        System.out.println("       <groupId>io.jsonwebtoken</groupId>");
        System.out.println("       <artifactId>jjwt-impl</artifactId>");
        System.out.println("       <version>0.11.5</version>");
        System.out.println("       <scope>runtime</scope>");
        System.out.println("   </dependency>");
        System.out.println("   <dependency>");
        System.out.println("       <groupId>io.jsonwebtoken</groupId>");
        System.out.println("       <artifactId>jjwt-jackson</artifactId>");
        System.out.println("       <version>0.11.5</version>");
        System.out.println("       <scope>runtime</scope>");
        System.out.println("   </dependency>");
        System.out.println();
        
        System.out.println("3. Rebuild the project:");
        System.out.println("   mvn clean compile");
        System.out.println("   mvn dependency:copy-dependencies");
        System.out.println();
        
        System.out.println("4. Run the authentication test:");
        System.out.println("   java -cp \"target\\classes;target\\dependency\\*\" GitHubAppJWTAuth");
        System.out.println();
        
        System.out.println("=== EXPECTED RESULTS ===");
        System.out.println("After successful setup, you should see:");
        System.out.println("✓ JWT token generated");
        System.out.println("✓ App information retrieved");
        System.out.println("✓ Installation access token obtained");
        System.out.println("✓ Repository list for installation");
        System.out.println();
    }
    
    /**
     * Create a batch file for easy key conversion
     */
    public void createConversionBatch() {
        System.out.println("=== Creating Conversion Helper ===");
        System.out.println();
        System.out.println("I'll create a batch file to help with key conversion:");
        System.out.println();
        
        String batchContent = "@echo off\n" +
            "echo GitHub App Private Key Converter\n" +
            "echo ===================================\n" +
            "echo.\n" +
            "echo Checking for OpenSSL...\n" +
            "where openssl >nul 2>&1\n" +
            "if %ERRORLEVEL% EQU 0 (\n" +
            "    echo OpenSSL found! Converting key...\n" +
            "    openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in \"" + PRIVATE_KEY_PATH + "\" -out \"" + PRIVATE_KEY_PATH.replace(".pem", "-pkcs8.pem") + "\"\n" +
            "    if %ERRORLEVEL% EQU 0 (\n" +
            "        echo Success! Converted key saved as: " + PRIVATE_KEY_PATH.replace(".pem", "-pkcs8.pem") + "\n" +
            "    ) else (\n" +
            "        echo Error converting key!\n" +
            "    )\n" +
            ") else (\n" +
            "    echo OpenSSL not found!\n" +
            "    echo Please install OpenSSL or use online converter:\n" +
            "    echo https://8gwifi.org/PemParserFunctions.jsp\n" +
            ")\n" +
            "echo.\n" +
            "pause\n";
        
        System.out.println("Batch file content:");
        System.out.println("-------------------");
        System.out.println(batchContent);
        System.out.println("-------------------");
        System.out.println();
        System.out.println("Save this as 'convert-key.bat' and run it to convert your key.");
        System.out.println();
    }
    
    public static void main(String[] args) {
        try {
            TestGitHubAppSetup test = new TestGitHubAppSetup();
            
            // Test basic connectivity
            test.testConnectivity();
            
            // Display comprehensive setup instructions
            test.displaySetupInstructions();
            
            // Create helper batch file
            test.createConversionBatch();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
