import com.github.security.ghas.GHASClient;
import com.github.security.ghas.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import okhttp3.*;
import java.util.List;

public class ShowRepoContents {
    private static final String TOKEN = "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";
    
    public static void main(String[] args) {
        try {
            GHASClient client = new GHASClient(TOKEN, null, 30, 3);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            
            String owner = "dagarachit";
            String repo = "ghas-dependabot";
            
            System.out.println("=== ASSETS WITHIN ghas-dependabot REPOSITORY ===\n");
            
            // Get repository details first
            Repository repository = client.getRepository(owner, repo);
            System.out.println("Repository: " + repository.getFullName());
            System.out.println("Description: " + repository.getDescription());
            System.out.println("Language: " + repository.getLanguage());
            System.out.println("Size: " + repository.getSize() + " KB");
            System.out.println("Default Branch: " + repository.getDefaultBranch());
            System.out.println();
            
            // Get repository contents using GitHub API directly
            System.out.println("=== REPOSITORY CONTENTS (Files & Directories) ===");
            
            OkHttpClient httpClient = new OkHttpClient();
            String contentsUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/contents";
            
            Request request = new Request.Builder()
                .url(contentsUrl)
                .addHeader("Authorization", "Bearer " + TOKEN)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
            
            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println("Root Directory Contents JSON:");
                
                // Parse and pretty print the JSON
                Object jsonObject = mapper.readValue(responseBody, Object.class);
                String prettyJson = mapper.writeValueAsString(jsonObject);
                System.out.println(prettyJson);
                System.out.println();
                
                // Also show a summary
                System.out.println("=== REPOSITORY STRUCTURE SUMMARY ===");
                com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(responseBody);
                
                if (rootNode.isArray()) {
                    System.out.println("Files and directories in root:");
                    for (com.fasterxml.jackson.databind.JsonNode item : rootNode) {
                        String name = item.get("name").asText();
                        String type = item.get("type").asText();
                        int size = item.has("size") ? item.get("size").asInt() : 0;
                        String downloadUrl = item.has("download_url") && !item.get("download_url").isNull() 
                            ? item.get("download_url").asText() : "N/A (directory)";
                        
                        System.out.println("- " + name + " (" + type + ")");
                        if (type.equals("file")) {
                            System.out.println("  Size: " + size + " bytes");
                            System.out.println("  Download URL: " + downloadUrl);
                        }
                        System.out.println();
                    }
                }
                
                // Get sample-vulnerable-app directory contents
                System.out.println("=== SAMPLE-VULNERABLE-APP DIRECTORY CONTENTS ===");
                String sampleAppUrl = contentsUrl + "/sample-vulnerable-app";
                
                Request sampleAppRequest = new Request.Builder()
                    .url(sampleAppUrl)
                    .addHeader("Authorization", "Bearer " + TOKEN)
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .build();
                
                Response sampleAppResponse = httpClient.newCall(sampleAppRequest).execute();
                if (sampleAppResponse.isSuccessful()) {
                    String sampleAppBody = sampleAppResponse.body().string();
                    com.fasterxml.jackson.databind.JsonNode sampleAppNode = mapper.readTree(sampleAppBody);
                    
                    if (sampleAppNode.isArray()) {
                        System.out.println("Files in sample-vulnerable-app/:");
                        for (com.fasterxml.jackson.databind.JsonNode item : sampleAppNode) {
                            String name = item.get("name").asText();
                            String type = item.get("type").asText();
                            int size = item.has("size") ? item.get("size").asInt() : 0;
                            
                            System.out.println("- " + name + " (" + type + ")");
                            if (type.equals("file")) {
                                System.out.println("  Size: " + size + " bytes");
                            }
                        }
                    }
                } else {
                    System.out.println("Could not fetch sample-vulnerable-app contents: " + sampleAppResponse.code());
                }
                
            } else {
                System.out.println("Failed to fetch repository contents: " + response.code());
                System.out.println("Response: " + response.body().string());
            }
            
            response.close();
            
            // Show repository statistics
            System.out.println("\n=== REPOSITORY STATISTICS ===");
            System.out.println("Total size: " + repository.getSize() + " KB");
            System.out.println("Stars: " + repository.getStargazersCount());
            System.out.println("Forks: " + repository.getForksCount());
            System.out.println("Open issues: " + repository.getOpenIssuesCount());
            System.out.println("Created: " + repository.getCreatedAt());
            System.out.println("Last updated: " + repository.getUpdatedAt());
            System.out.println("Last pushed: " + repository.getPushedAt());
            
            // Show languages used (if available)
            System.out.println("\n=== LANGUAGES USED ===");
            String languagesUrl = "https://api.github.com/repos/" + owner + "/" + repo + "/languages";
            
            Request languagesRequest = new Request.Builder()
                .url(languagesUrl)
                .addHeader("Authorization", "Bearer " + TOKEN)
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
            
            Response languagesResponse = httpClient.newCall(languagesRequest).execute();
            if (languagesResponse.isSuccessful()) {
                String languagesBody = languagesResponse.body().string();
                System.out.println("Languages JSON:");
                Object languagesObject = mapper.readValue(languagesBody, Object.class);
                String prettyLanguagesJson = mapper.writeValueAsString(languagesObject);
                System.out.println(prettyLanguagesJson);
            }
            languagesResponse.close();
            
            System.out.println("\n=== REPOSITORY ASSETS ANALYSIS COMPLETE ===");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
