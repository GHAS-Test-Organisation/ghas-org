import com.github.security.ghas.GHASClient;
import com.github.security.ghas.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;

public class ShowDependabotJSON {
    private static final String TOKEN = "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";
    
    public static void main(String[] args) {
        try {
            GHASClient client = new GHASClient(TOKEN, null, 30, 3);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            String owner = "dagarachit";
            String repo = "ghas-dependabot";
            
            System.out.println("=== ASSETS vs FINDINGS BREAKDOWN ===\n");
            
            // === ASSETS ===
            System.out.println("ASSETS (Infrastructure/Inventory):");
            System.out.println("=====================================");
            
            // Organizations (Assets)
            List<User> orgs = client.getOrganizations();
            System.out.println("Organizations: " + orgs.size());
            if (!orgs.isEmpty()) {
                String orgsJson = mapper.writeValueAsString(orgs.get(0));
                System.out.println("Sample Organization JSON:");
                System.out.println(orgsJson);
            }
            System.out.println();
            
            // Repositories (Assets)
            List<Repository> repos = client.getRepositories(null);
            System.out.println("Repositories: " + repos.size());
            Repository targetRepo = repos.stream()
                .filter(r -> r.getName().equals("ghas-dependabot"))
                .findFirst()
                .orElse(repos.get(0));
            
            System.out.println("Sample Repository JSON (Asset):");
            String repoJson = mapper.writeValueAsString(targetRepo);
            System.out.println(repoJson);
            System.out.println();
            
            // === FINDINGS/DETECTIONS ===
            System.out.println("FINDINGS/DETECTIONS (Security Issues):");
            System.out.println("==========================================");
            
            // Dependabot Alerts (Findings) - Show first 3 with full detail
            System.out.println("DEPENDABOT ALERTS (Vulnerable Dependencies):");
            System.out.println("Total alerts: Getting count...");
            
            List<DependabotAlert> dependabotAlerts = client.getDependabotAlerts(owner, repo);
            System.out.println("Total Dependabot alerts: " + dependabotAlerts.size());
            System.out.println();
            
            // Show first 3 alerts in full JSON
            System.out.println("First 3 Dependabot Alerts (Full JSON):");
            List<DependabotAlert> firstThree = dependabotAlerts.subList(0, Math.min(3, dependabotAlerts.size()));
            String dependabotJson = mapper.writeValueAsString(firstThree);
            System.out.println(dependabotJson);
            System.out.println();
            
            // Show summary of all alerts
            System.out.println("SUMMARY OF ALL 72 DEPENDABOT ALERTS:");
            System.out.println("========================================");
            
            int critical = 0, high = 0, medium = 0, low = 0;
            
            for (DependabotAlert alert : dependabotAlerts) {
                String severity = alert.getSecurityAdvisory() != null ? 
                    alert.getSecurityAdvisory().getSeverity() : "unknown";
                
                switch (severity.toLowerCase()) {
                    case "critical": critical++; break;
                    case "high": high++; break;
                    case "medium": medium++; break;
                    case "low": low++; break;
                }
                
                String packageName = alert.getDependency() != null && 
                    alert.getDependency().getPackageInfo() != null ? 
                    alert.getDependency().getPackageInfo().getName() : "Unknown";
                String cveId = alert.getSecurityAdvisory() != null ? 
                    alert.getSecurityAdvisory().getCveId() : "No CVE";
                
                System.out.println("Alert #" + alert.getNumber() + ": " + 
                    packageName + " - " + severity + " - " + cveId);
            }
            
            System.out.println();
            System.out.println("Severity Breakdown:");
            System.out.println("- Critical: " + critical);
            System.out.println("- High: " + high);
            System.out.println("- Medium: " + medium);
            System.out.println("- Low: " + low);
            System.out.println("- Total: " + dependabotAlerts.size());
            
            // Secret Scanning Alerts (Findings)
            System.out.println();
            System.out.println("SECRET SCANNING ALERTS (Hardcoded Secrets):");
            try {
                List<SecretScanningAlert> secretAlerts = client.getSecretScanningAlerts(owner, repo);
                System.out.println("Total secret alerts: " + secretAlerts.size());
                String secretJson = mapper.writeValueAsString(secretAlerts);
                System.out.println("Secret Alerts JSON:");
                System.out.println(secretJson);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
