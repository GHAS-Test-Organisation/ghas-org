# GitHub App Authentication Setup Guide

This guide will help you set up GitHub App authentication for your GHAS client instead of using Personal Access Tokens.

## Why GitHub App Authentication?

### Benefits over Personal Access Tokens:
- **Better Security**: Scoped permissions, no long-lived tokens
- **Scalability**: Can be installed on multiple repositories/organizations
- **Audit Trail**: Better tracking of API usage
- **Rate Limits**: Higher rate limits (5000 requests/hour vs 1000 for PATs)
- **Fine-grained Permissions**: Only grant necessary permissions

## Step 1: Create a GitHub App

### 1.1 Navigate to GitHub App Settings
1. Go to GitHub.com
2. Click your profile picture → Settings
3. In the left sidebar, click "Developer settings"
4. Click "GitHub Apps"
5. Click "New GitHub App"

### 1.2 Configure Your GitHub App

**Basic Information:**
- **GitHub App name**: `GHAS Security Scanner` (or your preferred name)
- **Description**: `GitHub Advanced Security API client for vulnerability scanning`
- **Homepage URL**: `https://github.com/yourusername/ghas-client` (optional)

**Permissions (Repository permissions):**
```
✓ Contents: Read
✓ Metadata: Read  
✓ Security events: Read
✓ Vulnerability alerts: Read
✓ Pull requests: Read (optional, for PR scanning)
```

**Permissions (Organization permissions):**
```
✓ Members: Read (if scanning org repositories)
```

**Subscribe to events:** (Optional)
```
✓ Security advisory
✓ Vulnerability alert
```

**Where can this GitHub App be installed:**
- Select "Any account" or "Only on this account" based on your needs

### 1.3 Create the App
1. Click "Create GitHub App"
2. Note down your **App ID** (you'll need this)

## Step 2: Generate Private Key

### 2.1 Generate Private Key
1. On your newly created GitHub App page
2. Scroll down to "Private keys"
3. Click "Generate a private key"
4. Download the `.pem` file
5. Store it securely (this is your authentication credential)

### 2.2 Private Key Security
```bash
# Set proper permissions on your private key file
chmod 600 /path/to/your/private-key.pem

# Store in a secure location
mkdir -p ~/.github-apps/
mv ~/Downloads/your-app-name.*.private-key.pem ~/.github-apps/ghas-app-key.pem
```

## Step 3: Install the App

### 3.1 Install on Your Repositories
1. Go to your GitHub App settings page
2. Click "Install App" in the left sidebar
3. Choose the account/organization
4. Select repositories:
   - **All repositories** (for full access)
   - **Selected repositories** (choose specific repos like `ghas-dependabot`)
5. Click "Install"

### 3.2 Note Installation Details
After installation, note:
- **Installation ID** (visible in the URL: `/settings/installations/{installation_id}`)
- **Account name** where it's installed

## Step 4: Update Your Code

### 4.1 Add Maven Dependencies
Add these to your `pom.xml`:

```xml
<!-- JWT Library -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

### 4.2 Update Your Configuration
```java
// Replace these with your actual values
String appId = "123456";  // Your GitHub App ID
String privateKeyPath = "/path/to/your/private-key.pem";
String owner = "dagarachit";
String repo = "ghas-dependabot";

// Create authenticated GHAS client
GHASClientWithAppAuth ghasClient = new GHASClientWithAppAuth(appId, privateKeyPath);
ghasClient.performComprehensiveAnalysis(owner, repo);
```

## Step 5: Test Your Setup

### 5.1 Compile and Run
```bash
# Compile with JWT dependencies
javac -cp "target/classes:target/dependency/*" GitHubAppJWTAuth.java GHASClientWithAppAuth.java

# Run the test
java -cp "target/classes:target/dependency/*:." GHASClientWithAppAuth
```

### 5.2 Expected Output
```
=== Repositories Accessible by GitHub App ===

Installation: dagarachit (User)
ID: 12345678
Repositories: 3
  - dagarachit/ghas-dependabot (Java) [Public]
  - dagarachit/FoodDeliveryApp (Java) [Public]
  - dagarachit/rachit_rep (Unknown) [Public]

=== GHAS Analysis with GitHub App Authentication ===

✓ Authenticated for installation ID: 12345678
✓ Authenticated as: dagarachit[bot]
✓ Repository: dagarachit/ghas-dependabot
✓ Dependabot Alerts: 72
✓ Secret Scanning Alerts: 2
```

## Step 6: Curl Commands with JWT

### 6.1 Generate JWT Token
```bash
# You can use the Java class to generate a JWT
java -cp "target/classes:target/dependency/*:." GitHubAppJWTAuth
```

### 6.2 Use JWT in Curl Commands
```bash
# Set your JWT token
JWT_TOKEN="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."

# Get app installations
curl -H "Authorization: Bearer $JWT_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/app/installations

# Get installation access token
INSTALLATION_ID="12345678"
curl -X POST \
     -H "Authorization: Bearer $JWT_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/app/installations/$INSTALLATION_ID/access_tokens

# Use installation access token for API calls
INSTALLATION_TOKEN="ghs_abc123..."
curl -H "Authorization: token $INSTALLATION_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/dagarachit/ghas-dependabot/dependabot/alerts
```

## Step 7: Security Best Practices

### 7.1 Private Key Security
- **Never commit** private keys to version control
- Store keys in secure locations with proper permissions
- Use environment variables or secure key management systems
- Rotate keys periodically

### 7.2 Token Management
- JWT tokens expire after 10 minutes (GitHub requirement)
- Installation access tokens expire after 1 hour
- Implement token refresh logic in production applications
- Cache tokens appropriately to avoid unnecessary API calls

### 7.3 Error Handling
```java
try {
    String accessToken = appAuth.getInstallationAccessToken(installationId);
    // Use token...
} catch (IOException e) {
    if (e.getMessage().contains("401")) {
        // JWT expired, generate new one
        // Retry operation
    } else if (e.getMessage().contains("404")) {
        // Installation not found
        // Handle appropriately
    }
}
```

## Step 8: Production Considerations

### 8.1 Configuration Management
```java
// Use environment variables
String appId = System.getenv("GITHUB_APP_ID");
String privateKeyPath = System.getenv("GITHUB_APP_PRIVATE_KEY_PATH");

// Or use configuration files
Properties config = new Properties();
config.load(new FileInputStream("app.properties"));
```

### 8.2 Logging and Monitoring
```java
// Add proper logging
private static final Logger logger = LoggerFactory.getLogger(GHASClientWithAppAuth.class);

logger.info("Authenticating for repository: {}/{}", owner, repo);
logger.debug("Generated JWT token for app ID: {}", appId);
```

### 8.3 Rate Limit Handling
```java
// Check rate limits
Response response = httpClient.newCall(request).execute();
String remaining = response.header("X-RateLimit-Remaining");
String resetTime = response.header("X-RateLimit-Reset");

if (Integer.parseInt(remaining) < 10) {
    // Implement backoff strategy
}
```

## Troubleshooting

### Common Issues:

1. **"Bad credentials" error**
   - Check your App ID is correct
   - Verify private key format and path
   - Ensure JWT is properly signed

2. **"Not Found" for installation**
   - Verify the app is installed on the target repository
   - Check installation ID is correct
   - Ensure proper permissions are granted

3. **"Forbidden" errors**
   - Check app permissions include required scopes
   - Verify installation covers the target repository
   - Ensure access token hasn't expired

4. **JWT parsing errors**
   - Verify private key format (PKCS#8)
   - Check JWT library compatibility
   - Ensure proper algorithm (RS256)

### Debug Commands:
```bash
# Verify private key format
openssl rsa -in private-key.pem -text -noout

# Check JWT token structure
echo "JWT_TOKEN" | cut -d. -f2 | base64 -d | jq .

# Test app authentication
curl -H "Authorization: Bearer $JWT_TOKEN" https://api.github.com/app
```

---

**Next Steps:**
1. Create your GitHub App following this guide
2. Update the Java code with your App ID and private key path
3. Test the authentication flow
4. Integrate with your existing GHAS client

Your GHAS client will now use secure GitHub App authentication instead of Personal Access Tokens! 🚀
