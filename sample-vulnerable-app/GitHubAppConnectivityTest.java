import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * GitHub App Connectivity Test
 * 
 * This test verifies basic connectivity to GitHub API and provides
 * instructions for setting up GitHub App authentication.
 */
public class GitHubAppConnectivityTest {
    
    private final OkHttpClient httpClient;
    private final ObjectMapper mapper;
    
    // GitHub API URLs
    private static final String GITHUB_API_BASE = "https://api.github.com";
    
    public GitHubAppConnectivityTest() {
        this.httpClient = new OkHttpClient();
        this.mapper = new ObjectMapper();
    }
    
    /**
     * Test basic GitHub API connectivity
     */
    public JsonNode testGitHubAPIConnectivity() throws IOException {
        Request request = new Request.Builder()
            .url(GITHUB_API_BASE)
            .addHeader("Accept", "application/vnd.github.v3+json")
            .addHeader("User-Agent", "GHAS-Client/1.0")
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to connect to GitHub API: " + response.code());
            }
            
            return mapper.readTree(response.body().string());
        }
    }
    
    /**
     * Test rate limit endpoint
     */
    public JsonNode testRateLimit() throws IOException {
        Request request = new Request.Builder()
            .url(GITHUB_API_BASE + "/rate_limit")
            .addHeader("Accept", "application/vnd.github.v3+json")
            .addHeader("User-Agent", "GHAS-Client/1.0")
            .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get rate limit: " + response.code());
            }
            
            return mapper.readTree(response.body().string());
        }
    }
    
    /**
     * Display GitHub App setup instructions
     */
    public static void displaySetupInstructions() {
        System.out.println("=== GitHub App Authentication Setup Instructions ===\n");
        
        System.out.println("Your GitHub App Details:");
        System.out.println("- App ID: 2244732");
        System.out.println("- Installation ID: 93351110");
        System.out.println("- Private Key: src/main/resources/was-authentication-test.2025-11-06.private-key.pem");
        System.out.println();
        
        System.out.println("Issue Detected:");
        System.out.println("The private key is in RSA format, but Java requires PKCS#8 format for JWT signing.");
        System.out.println();
        
        System.out.println("Solutions:");
        System.out.println("1. Convert using OpenSSL (if available):");
        System.out.println("   openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt \\");
        System.out.println("   -in src/main/resources/was-authentication-test.2025-11-06.private-key.pem \\");
        System.out.println("   -out src/main/resources/was-authentication-test.2025-11-06.private-key-pkcs8.pem");
        System.out.println();
        
        System.out.println("2. Add BouncyCastle library to pom.xml:");
        System.out.println("   <dependency>");
        System.out.println("       <groupId>org.bouncycastle</groupId>");
        System.out.println("       <artifactId>bcprov-jdk15on</artifactId>");
        System.out.println("       <version>1.70</version>");
        System.out.println("   </dependency>");
        System.out.println();
        
        System.out.println("3. Add JWT dependencies to pom.xml:");
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
    }
    
    /**
     * Main test method
     */
    public static void main(String[] args) {
        try {
            GitHubAppConnectivityTest test = new GitHubAppConnectivityTest();
            
            System.out.println("=== GitHub App Connectivity Test ===\n");
            
            // 1. Test basic connectivity
            System.out.println("1. Testing GitHub API Connectivity...");
            JsonNode apiInfo = test.testGitHubAPIConnectivity();
            System.out.println("✓ Successfully connected to GitHub API!");
            if (apiInfo.has("documentation_url")) {
                System.out.println("API Documentation: " + apiInfo.get("documentation_url").asText());
            } else {
                System.out.println("GitHub API is accessible");
            }
            System.out.println();
            
            // 2. Test rate limit
            System.out.println("2. Testing Rate Limit Endpoint...");
            JsonNode rateLimit = test.testRateLimit();
            System.out.println("✓ Rate limit endpoint accessible!");
            JsonNode core = rateLimit.get("rate");
            System.out.println("Rate Limit - Remaining: " + core.get("remaining").asInt() + 
                             "/" + core.get("limit").asInt());
            System.out.println();
            
            // 3. Display setup instructions
            displaySetupInstructions();
            
            System.out.println("Next Steps:");
            System.out.println("1. Convert your private key to PKCS#8 format");
            System.out.println("2. Add JWT dependencies to your project");
            System.out.println("3. Run the GitHubAppJWTAuth class for full authentication test");
            System.out.println();
            
            System.out.println("=== Connectivity Test Complete ===");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
