import com.github.security.ghas.GHASClient;
import com.github.security.ghas.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Enhanced GHAS Client with GitHub App Authentication
 * 
 * This class extends the GHAS functionality to use GitHub App authentication
 * instead of Personal Access Tokens for better security and scalability.
 */
public class GHASClientWithAppAuth {
    
    private final GitHubAppJWTAuth appAuth;
    private final ObjectMapper mapper;
    private String currentAccessToken;
    private long currentInstallationId;
    
    public GHASClientWithAppAuth(String appId, String privateKeyPath) throws Exception {
        this.appAuth = new GitHubAppJWTAuth(appId, privateKeyPath);
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Initialize authentication for a specific repository
     */
    public void authenticateForRepository(String owner, String repo) throws Exception {
        System.out.println("Authenticating for repository: " + owner + "/" + repo);
        
        // Get installations and find the one that covers this repository
        JsonNode installations = appAuth.getInstallations();
        
        for (JsonNode installation : installations) {
            long installationId = installation.get("id").asLong();
            String account = installation.get("account").get("login").asText();
            
            if (account.equals(owner)) {
                this.currentInstallationId = installationId;
                this.currentAccessToken = appAuth.getInstallationAccessToken(installationId);
                System.out.println("✓ Authenticated for installation ID: " + installationId);
                return;
            }
        }
        
        throw new Exception("No GitHub App installation found for: " + owner + "/" + repo);
    }
    
    /**
     * Get GHAS client instance with current access token
     */
    public GHASClient getGHASClient() {
        if (currentAccessToken == null) {
            throw new IllegalStateException("Must authenticate for a repository first");
        }
        
        return new GHASClient(currentAccessToken, null, 30, 3);
    }
    
    /**
     * Perform comprehensive GHAS analysis using GitHub App authentication
     */
    public void performComprehensiveAnalysis(String owner, String repo) {
        try {
            System.out.println("=== GHAS Analysis with GitHub App Authentication ===\n");
            
            // 1. Authenticate for the repository
            authenticateForRepository(owner, repo);
            
            // 2. Get GHAS client with installation token
            GHASClient client = getGHASClient();
            
            // 3. Test authentication
            System.out.println("1. Testing Authentication...");
            User user = client.testAuthentication();
            System.out.println("✓ Authenticated as: " + user.getLogin());
            System.out.println("  User Type: " + user.getType());
            System.out.println();
            
            // 4. Get repository information
            System.out.println("2. Repository Analysis...");
            Repository repository = client.getRepository(owner, repo);
            System.out.println("✓ Repository: " + repository.getFullName());
            System.out.println("  Language: " + repository.getLanguage());
            System.out.println("  Private: " + repository.getPrivate());
            System.out.println("  Size: " + repository.getSize() + " KB");
            System.out.println();
            
            // 5. Security Analysis
            System.out.println("3. Security Analysis...");
            
            // Dependabot Alerts
            try {
                List<DependabotAlert> dependabotAlerts = client.getDependabotAlerts(owner, repo);
                System.out.println("✓ Dependabot Alerts: " + dependabotAlerts.size());
                
                // Show severity breakdown
                long critical = dependabotAlerts.stream()
                    .filter(alert -> alert.getSecurityAdvisory() != null && 
                            "critical".equals(alert.getSecurityAdvisory().getSeverity()))
                    .count();
                long high = dependabotAlerts.stream()
                    .filter(alert -> alert.getSecurityAdvisory() != null && 
                            "high".equals(alert.getSecurityAdvisory().getSeverity()))
                    .count();
                
                System.out.println("  - Critical: " + critical);
                System.out.println("  - High: " + high);
                
            } catch (Exception e) {
                System.out.println("⚠ Dependabot: " + e.getMessage());
            }
            
            // Secret Scanning Alerts
            try {
                List<SecretScanningAlert> secretAlerts = client.getSecretScanningAlerts(owner, repo);
                System.out.println("✓ Secret Scanning Alerts: " + secretAlerts.size());
                
                for (SecretScanningAlert alert : secretAlerts) {
                    System.out.println("  - " + alert.getSecretTypeDisplayName() + 
                        " (State: " + alert.getState() + ")");
                }
                
            } catch (Exception e) {
                System.out.println("⚠ Secret Scanning: " + e.getMessage());
            }
            
            // Code Scanning Alerts
            try {
                List<CodeScanningAlert> codeAlerts = client.getCodeScanningAlerts(owner, repo);
                System.out.println("✓ Code Scanning Alerts: " + codeAlerts.size());
                
            } catch (Exception e) {
                System.out.println("⚠ Code Scanning: " + e.getMessage());
            }
            
            System.out.println();
            
            // 6. Generate comprehensive security summary
            System.out.println("4. Security Summary...");
            try {
                GHASClient.SecuritySummary summary = client.getAllSecurityAlerts(owner, repo);
                System.out.println("✓ Total Security Issues: " + summary.getTotalAlerts());
                
                String summaryJson = mapper.writeValueAsString(summary);
                System.out.println("\nSecurity Summary JSON:");
                System.out.println(summaryJson);
                
            } catch (Exception e) {
                System.out.println("⚠ Security Summary: " + e.getMessage());
            }
            
            System.out.println("\n=== Analysis Complete ===");
            
        } catch (Exception e) {
            System.err.println("Error during analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get all repositories accessible by the GitHub App
     */
    public void listAccessibleRepositories() {
        try {
            System.out.println("=== Repositories Accessible by GitHub App ===\n");
            
            JsonNode installations = appAuth.getInstallations();
            
            for (JsonNode installation : installations) {
                long installationId = installation.get("id").asLong();
                String account = installation.get("account").get("login").asText();
                String accountType = installation.get("account").get("type").asText();
                
                System.out.println("Installation: " + account + " (" + accountType + ")");
                System.out.println("ID: " + installationId);
                
                JsonNode repos = appAuth.getInstallationRepositories(installationId);
                int totalCount = repos.get("total_count").asInt();
                
                System.out.println("Repositories: " + totalCount);
                
                if (repos.has("repositories")) {
                    for (JsonNode repo : repos.get("repositories")) {
                        String fullName = repo.get("full_name").asText();
                        String language = repo.has("language") && !repo.get("language").isNull() ? 
                            repo.get("language").asText() : "Unknown";
                        boolean isPrivate = repo.get("private").asBoolean();
                        
                        System.out.println("  - " + fullName + " (" + language + ")" + 
                            (isPrivate ? " [Private]" : " [Public]"));
                    }
                }
                System.out.println();
            }
            
        } catch (Exception e) {
            System.err.println("Error listing repositories: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Example usage
     */
    public static void main(String[] args) {
        try {
            // GitHub App Configuration
            // You need to create a GitHub App and get these values
            String appId = "YOUR_GITHUB_APP_ID";  // Replace with your app ID
            String privateKeyPath = "path/to/your/github-app-private-key.pem";  // Replace with your private key path
            
            // Target repository
            String owner = "dagarachit";
            String repo = "ghas-dependabot";
            
            // Create GHAS client with App authentication
            GHASClientWithAppAuth ghasClient = new GHASClientWithAppAuth(appId, privateKeyPath);
            
            // List all accessible repositories
            ghasClient.listAccessibleRepositories();
            
            // Perform comprehensive analysis on target repository
            ghasClient.performComprehensiveAnalysis(owner, repo);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            System.out.println("\n=== Setup Instructions ===");
            System.out.println("1. Create a GitHub App at: https://github.com/settings/apps");
            System.out.println("2. Generate a private key for your app");
            System.out.println("3. Install the app on your repositories");
            System.out.println("4. Grant the following permissions:");
            System.out.println("   - Repository permissions:");
            System.out.println("     * Security events: Read");
            System.out.println("     * Vulnerability alerts: Read");
            System.out.println("     * Contents: Read");
            System.out.println("     * Metadata: Read");
            System.out.println("5. Update the appId and privateKeyPath in this code");
        }
    }
}
