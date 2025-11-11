import com.github.security.ghas.GHASClient;
import com.github.security.ghas.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;

public class ShowAssetsJSON {
    private static final String TOKEN = "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";
    
    public static void main(String[] args) {
        try {
            GHASClient client = new GHASClient(TOKEN, null, 30, 3);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            System.out.println("=== ASSETS RECEIVED FROM GITHUB API ===\n");
            
            // === 1. USER AUTHENTICATION (Asset) ===
            System.out.println("=== 1. AUTHENTICATED USER (Asset) ===");
            User authenticatedUser = client.testAuthentication();
            String userJson = mapper.writeValueAsString(authenticatedUser);
            System.out.println("Authenticated User JSON:");
            System.out.println(userJson);
            System.out.println();
            
            // === 2. ORGANIZATIONS (Assets) ===
            System.out.println("=== 2. ORGANIZATIONS (Assets) ===");
            List<User> organizations = client.getOrganizations();
            System.out.println("Total Organizations: " + organizations.size());
            
            if (!organizations.isEmpty()) {
                String orgsJson = mapper.writeValueAsString(organizations);
                System.out.println("Organizations JSON:");
                System.out.println(orgsJson);
            } else {
                System.out.println("No organizations found - user is not part of any GitHub organizations");
            }
            System.out.println();
            
            // === 3. REPOSITORIES (Assets) ===
            System.out.println("=== 3. REPOSITORIES (Assets) ===");
            List<Repository> repositories = client.getRepositories(null);
            System.out.println("Total Repositories: " + repositories.size());
            System.out.println();
            
            System.out.println("All Repositories JSON:");
            String reposJson = mapper.writeValueAsString(repositories);
            System.out.println(reposJson);
            System.out.println();
            
            // === 4. SPECIFIC REPOSITORY DETAILS (Asset) ===
            System.out.println("=== 4. SPECIFIC REPOSITORY DETAILS (Asset) ===");
            if (!repositories.isEmpty()) {
                Repository targetRepo = repositories.stream()
                    .filter(r -> r.getName().equals("ghas-dependabot"))
                    .findFirst()
                    .orElse(repositories.get(0));
                
                String owner = targetRepo.getOwner().getLogin();
                String repoName = targetRepo.getName();
                
                System.out.println("Getting detailed info for: " + owner + "/" + repoName);
                Repository detailedRepo = client.getRepository(owner, repoName);
                String detailedRepoJson = mapper.writeValueAsString(detailedRepo);
                System.out.println("Detailed Repository JSON:");
                System.out.println(detailedRepoJson);
                System.out.println();
                
                // === 5. REPOSITORY COLLABORATORS (Assets) ===
                System.out.println("=== 5. REPOSITORY COLLABORATORS (Assets) ===");
                try {
                    List<User> collaborators = client.getRepositoryCollaborators(owner, repoName);
                    System.out.println("Total Collaborators: " + collaborators.size());
                    
                    if (!collaborators.isEmpty()) {
                        String collaboratorsJson = mapper.writeValueAsString(collaborators);
                        System.out.println("Collaborators JSON:");
                        System.out.println(collaboratorsJson);
                    } else {
                        System.out.println("No additional collaborators found (only owner has access)");
                    }
                } catch (Exception e) {
                    System.out.println("Could not fetch collaborators: " + e.getMessage());
                }
                System.out.println();
            }
            
            // === 6. ASSET SUMMARY ===
            System.out.println("=== 6. ASSETS SUMMARY ===");
            System.out.println("=============================");
            System.out.println("User: " + authenticatedUser.getLogin() + " (ID: " + authenticatedUser.getId() + ")");
            System.out.println("Organizations: " + organizations.size());
            System.out.println("Repositories: " + repositories.size());
            
            System.out.println("\nRepository Details:");
            for (Repository repo : repositories) {
                System.out.println("- " + repo.getFullName());
                System.out.println("  Language: " + (repo.getLanguage() != null ? repo.getLanguage() : "Unknown"));
                System.out.println("  Private: " + repo.getPrivate());
                System.out.println("  Default Branch: " + repo.getDefaultBranch());
                System.out.println("  Stars: " + repo.getStargazersCount());
                System.out.println("  Forks: " + repo.getForksCount());
                System.out.println("  Size: " + repo.getSize() + " KB");
                System.out.println("  Created: " + repo.getCreatedAt());
                System.out.println("  Updated: " + repo.getUpdatedAt());
                System.out.println("  Clone URL: " + repo.getCloneUrl());
                System.out.println("  SSH URL: " + repo.getSshUrl());
                System.out.println();
            }
            
            System.out.println("=== ASSETS DISPLAY COMPLETE ===");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
