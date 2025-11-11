import com.github.security.ghas.GHASClient;
import com.github.security.ghas.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;

public class ShowJSONResponse {
    private static final String TOKEN = "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";
    
    public static void main(String[] args) {
        try {
            GHASClient client = new GHASClient(TOKEN, null, 30, 3);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            System.out.println("=== GHAS API JSON Responses ===\n");
            
            // 1. Authentication Response
            System.out.println("=== 1. AUTHENTICATION RESPONSE ===");
            User user = client.testAuthentication();
            String userJson = mapper.writeValueAsString(user);
            System.out.println(userJson);
            System.out.println();
            
            // 2. Repositories Response
            System.out.println("=== 2. REPOSITORIES RESPONSE ===");
            List<Repository> repos = client.getRepositories(null);
            String reposJson = mapper.writeValueAsString(repos);
            System.out.println(reposJson);
            System.out.println();
            
            // 3. Security Findings for ghas-dependabot repository
            String owner = "dagarachit";
            String repo = "ghas-dependabot";
            
            // Secret Scanning Alerts
            System.out.println("=== 3. SECRET SCANNING ALERTS JSON ===");
            try {
                List<SecretScanningAlert> secretAlerts = client.getSecretScanningAlerts(owner, repo);
                String secretJson = mapper.writeValueAsString(secretAlerts);
                System.out.println(secretJson);
            } catch (Exception e) {
                System.out.println("Error fetching secret alerts: " + e.getMessage());
            }
            System.out.println();
            
            // Dependabot Alerts (first 5 only to avoid too much output)
            System.out.println("=== 4. DEPENDABOT ALERTS JSON (First 5) ===");
            try {
                List<DependabotAlert> dependabotAlerts = client.getDependabotAlerts(owner, repo);
                List<DependabotAlert> firstFive = dependabotAlerts.subList(0, Math.min(5, dependabotAlerts.size()));
                String dependabotJson = mapper.writeValueAsString(firstFive);
                System.out.println(dependabotJson);
                System.out.println("Total Dependabot alerts: " + dependabotAlerts.size());
            } catch (Exception e) {
                System.out.println("Error fetching Dependabot alerts: " + e.getMessage());
            }
            System.out.println();
            
            // Code Scanning Alerts (if available)
            System.out.println("=== 5. CODE SCANNING ALERTS JSON ===");
            try {
                List<CodeScanningAlert> codeAlerts = client.getCodeScanningAlerts(owner, repo);
                String codeJson = mapper.writeValueAsString(codeAlerts);
                System.out.println(codeJson);
            } catch (Exception e) {
                System.out.println("Code scanning not available yet: " + e.getMessage());
                System.out.println("(This is normal - CodeQL analysis takes 5-10 minutes after first push)");
            }
            System.out.println();
            
            // Security Summary
            System.out.println("=== 6. SECURITY SUMMARY JSON ===");
            try {
                GHASClient.SecuritySummary summary = client.getAllSecurityAlerts(owner, repo);
                String summaryJson = mapper.writeValueAsString(summary);
                System.out.println(summaryJson);
            } catch (Exception e) {
                System.out.println("Security summary error: " + e.getMessage());
            }
            
            System.out.println("\n=== JSON Response Display Complete ===");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
