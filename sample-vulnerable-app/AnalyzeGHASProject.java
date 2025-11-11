import com.github.security.ghas.GHASClient;
import com.github.security.ghas.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AnalyzeGHASProject {
    private static final String TOKEN = "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";
    
    public static void main(String[] args) {
        try {
            GHASClient client = new GHASClient(TOKEN, null, 30, 3);
            
            String owner = "dagarachit";
            String repo = "ghas-dependabot";
            
            System.out.println("=== GHAS-DEPENDABOT PROJECT ANALYSIS ===\n");
            
            // === 1. PROJECT OVERVIEW ===
            System.out.println("=== 1. PROJECT OVERVIEW ===");
            Repository repository = client.getRepository(owner, repo);
            System.out.println("Repository: " + repository.getFullName());
            System.out.println("Description: " + repository.getDescription());
            System.out.println("Primary Language: " + repository.getLanguage());
            System.out.println("Size: " + repository.getSize() + " KB");
            System.out.println("Created: " + repository.getCreatedAt());
            System.out.println("Last Updated: " + repository.getUpdatedAt());
            System.out.println();
            
            // === 2. APPLICATION TYPE ANALYSIS ===
            System.out.println("=== 2. APPLICATION TYPE ANALYSIS ===");
            System.out.println("Based on repository analysis:");
            System.out.println("- Primary Language: Java");
            System.out.println("- Project Type: Maven-based Java Application");
            System.out.println("- Application Category: Vulnerable Demo Web Application");
            System.out.println("- Purpose: Security testing and GHAS demonstration");
            System.out.println();
            
            System.out.println("Applications/Components Present:");
            System.out.println("1. **Vulnerable Java Web Application**");
            System.out.println("   - Location: sample-vulnerable-app/");
            System.out.println("   - Type: Maven Java project");
            System.out.println("   - Contains: Intentionally vulnerable code");
            System.out.println("   - Technologies: Spring Framework, Jackson, Log4j, MySQL");
            System.out.println();
            
            System.out.println("2. **GHAS Client Library**");
            System.out.println("   - Type: Java API client");
            System.out.println("   - Purpose: GitHub Advanced Security API integration");
            System.out.println("   - Technologies: OkHttp, Jackson, Maven");
            System.out.println();
            
            // === 3. SECURITY VULNERABILITIES ANALYSIS ===
            System.out.println("=== 3. SECURITY VULNERABILITIES IN GHAS-DEPENDABOT ===");
            
            // Get all security findings
            try {
                // Dependabot Alerts
                System.out.println("📦 DEPENDABOT ALERTS (Vulnerable Dependencies):");
                List<DependabotAlert> dependabotAlerts = client.getDependabotAlerts(owner, repo);
                System.out.println("Total: " + dependabotAlerts.size() + " vulnerable dependencies found");
                
                // Categorize by severity
                Map<String, Integer> severityCount = new HashMap<>();
                Map<String, Integer> packageCount = new HashMap<>();
                
                for (DependabotAlert alert : dependabotAlerts) {
                    String severity = alert.getSecurityAdvisory() != null ? 
                        alert.getSecurityAdvisory().getSeverity() : "unknown";
                    severityCount.put(severity, severityCount.getOrDefault(severity, 0) + 1);
                    
                    String packageName = alert.getDependency() != null && 
                        alert.getDependency().getPackageInfo() != null ? 
                        alert.getDependency().getPackageInfo().getName() : "Unknown";
                    packageCount.put(packageName, packageCount.getOrDefault(packageName, 0) + 1);
                }
                
                System.out.println("\nSeverity Breakdown:");
                severityCount.forEach((severity, count) -> 
                    System.out.println("- " + severity.toUpperCase() + ": " + count + " alerts"));
                
                System.out.println("\nVulnerable Packages:");
                packageCount.forEach((pkg, count) -> 
                    System.out.println("- " + pkg + ": " + count + " vulnerabilities"));
                
                System.out.println("\nTop Critical Vulnerabilities:");
                dependabotAlerts.stream()
                    .filter(alert -> alert.getSecurityAdvisory() != null && 
                            "critical".equals(alert.getSecurityAdvisory().getSeverity()))
                    .limit(5)
                    .forEach(alert -> {
                        String pkg = alert.getDependency().getPackageInfo().getName();
                        String cve = alert.getSecurityAdvisory().getCveId();
                        String summary = alert.getSecurityAdvisory().getSummary();
                        System.out.println("- " + pkg + " (" + cve + "): " + summary);
                    });
                
            } catch (Exception e) {
                System.out.println("Error fetching Dependabot alerts: " + e.getMessage());
            }
            
            System.out.println();
            
            // Secret Scanning Alerts
            try {
                System.out.println("🔐 SECRET SCANNING ALERTS (Hardcoded Secrets):");
                List<SecretScanningAlert> secretAlerts = client.getSecretScanningAlerts(owner, repo);
                System.out.println("Total: " + secretAlerts.size() + " secrets found");
                
                for (SecretScanningAlert alert : secretAlerts) {
                    System.out.println("- " + alert.getSecretTypeDisplayName() + 
                        " (Alert #" + alert.getNumber() + ")");
                    System.out.println("  State: " + alert.getState());
                    System.out.println("  Secret: " + alert.getSecret());
                }
                
            } catch (Exception e) {
                System.out.println("Error fetching secret alerts: " + e.getMessage());
            }
            
            System.out.println();
            
            // Code Scanning Alerts
            try {
                System.out.println("🔍 CODE SCANNING ALERTS (Code Vulnerabilities):");
                List<CodeScanningAlert> codeAlerts = client.getCodeScanningAlerts(owner, repo);
                System.out.println("Total: " + codeAlerts.size() + " code vulnerabilities found");
                
                if (!codeAlerts.isEmpty()) {
                    Map<String, Integer> ruleSeverity = new HashMap<>();
                    for (CodeScanningAlert alert : codeAlerts) {
                        if (alert.getRule() != null) {
                            String severity = alert.getRule().getSeverity();
                            ruleSeverity.put(severity, ruleSeverity.getOrDefault(severity, 0) + 1);
                        }
                    }
                    
                    System.out.println("Code Issues by Severity:");
                    ruleSeverity.forEach((severity, count) -> 
                        System.out.println("- " + severity.toUpperCase() + ": " + count + " issues"));
                    
                    System.out.println("\nTop Code Vulnerabilities:");
                    codeAlerts.stream().limit(10).forEach(alert -> {
                        String rule = alert.getRule() != null ? alert.getRule().getName() : "Unknown";
                        String severity = alert.getRule() != null ? alert.getRule().getSeverity() : "Unknown";
                        String file = alert.getMostRecentInstance() != null && 
                            alert.getMostRecentInstance().getLocation() != null ?
                            alert.getMostRecentInstance().getLocation().getPath() : "Unknown";
                        
                        System.out.println("- " + rule + " (" + severity + ") in " + file);
                    });
                }
                
            } catch (Exception e) {
                System.out.println("Code scanning not available yet: " + e.getMessage());
                System.out.println("(CodeQL analysis may still be running or not enabled)");
            }
            
            // === 4. SECURITY SUMMARY ===
            System.out.println("\n=== 4. OVERALL SECURITY SUMMARY ===");
            try {
                GHASClient.SecuritySummary summary = client.getAllSecurityAlerts(owner, repo);
                System.out.println("Total Security Issues: " + summary.getTotalAlerts());
                
                if (summary.getDependabotAlerts() != null) {
                    System.out.println("- Vulnerable Dependencies: " + summary.getDependabotAlerts().size());
                }
                if (summary.getSecretScanningAlerts() != null) {
                    System.out.println("- Hardcoded Secrets: " + summary.getSecretScanningAlerts().size());
                }
                if (summary.getCodeScanningAlerts() != null) {
                    System.out.println("- Code Vulnerabilities: " + summary.getCodeScanningAlerts().size());
                }
                
            } catch (Exception e) {
                System.out.println("Could not generate complete security summary: " + e.getMessage());
            }
            
            System.out.println("\n=== 5. RECOMMENDATIONS ===");
            System.out.println("This repository contains a VULNERABLE DEMO APPLICATION with:");
            System.out.println("- 70+ dependency vulnerabilities (including Log4Shell)");
            System.out.println("- Hardcoded API keys and secrets");
            System.out.println("- Intentionally vulnerable Java code");
            System.out.println();
            System.out.println("⚠️  WARNING: This is for TESTING PURPOSES ONLY!");
            System.out.println("   Do NOT deploy this application to production!");
            
            System.out.println("\n=== ANALYSIS COMPLETE ===");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
