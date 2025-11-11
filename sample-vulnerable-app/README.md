# Vulnerable Demo App for GHAS Testing

⚠️ **WARNING: This application contains intentional security vulnerabilities for testing purposes only. DO NOT use in production!**

## Purpose

This repository demonstrates GitHub Advanced Security (GHAS) features by containing:

- **Vulnerable Dependencies** (for Dependabot alerts)
- **Security Code Issues** (for CodeQL scanning)  
- **Hardcoded Secrets** (for Secret scanning)

## Security Issues Included

### 🔍 **Code Scanning (CodeQL) Issues**
- SQL Injection vulnerabilities
- Command Injection vulnerabilities  
- Path Traversal vulnerabilities
- Weak cryptography usage
- Information disclosure in logs
- HTTP Response Splitting
- XML External Entity (XXE) vulnerabilities
- Insecure deserialization

### 🔐 **Secret Scanning Issues**
- Hardcoded API keys
- Database passwords in source code
- AWS access keys and secrets
- Various authentication tokens

### 📦 **Dependabot Issues**
- Jackson Databind (CVE-2019-12086, CVE-2019-14540)
- Apache Commons Collections (CVE-2015-6420)
- Spring Framework (CVE-2018-15756)
- Log4j (CVE-2021-44228 - Log4Shell)
- MySQL Connector (Multiple CVEs)

## Setup Instructions

### 1. Create Repository
```bash
# Create a new repository on GitHub
# Clone it locally
git clone https://github.com/your-username/vulnerable-demo-app.git
cd vulnerable-demo-app

# Copy the files from this sample-vulnerable-app directory
```

### 2. Enable GHAS Features
1. Go to repository **Settings** → **Security & analysis**
2. Enable:
   - ✅ Dependency graph
   - ✅ Dependabot alerts
   - ✅ Dependabot security updates
   - ✅ Code scanning (CodeQL)
   - ✅ Secret scanning

### 3. Push Code and Trigger Scans
```bash
git add .
git commit -m "Add vulnerable demo application for GHAS testing"
git push origin main
```

### 4. Wait for Analysis
- **CodeQL**: Will run via GitHub Actions (5-10 minutes)
- **Secret Scanning**: Runs automatically on push
- **Dependabot**: Analyzes dependencies immediately

## Expected Results

After setup, you should see:

### Code Scanning Alerts (~15-20 alerts)
- High severity: SQL injection, Command injection
- Medium severity: Path traversal, Information disclosure
- Low severity: Weak cryptography, Poor error handling

### Secret Scanning Alerts (~4-6 alerts)
- API keys detected
- Database credentials detected  
- AWS credentials detected

### Dependabot Alerts (~10-15 alerts)
- Critical: Log4j vulnerability (Log4Shell)
- High: Jackson deserialization issues
- Medium/Low: Various dependency vulnerabilities

## Testing with GHAS Client

Once alerts are generated, test with the GHAS client:

```java
GHASClient client = new GHASClient();

// Get security summary
SecuritySummary summary = client.getAllSecurityAlerts("your-username", "vulnerable-demo-app");
System.out.println("Total alerts: " + summary.getTotalAlerts());

// Get specific alert types
List<CodeScanningAlert> codeAlerts = client.getCodeScanningAlerts("your-username", "vulnerable-demo-app");
List<SecretScanningAlert> secretAlerts = client.getSecretScanningAlerts("your-username", "vulnerable-demo-app");
List<DependabotAlert> dependabotAlerts = client.getDependabotAlerts("your-username", "vulnerable-demo-app");
```

## Cleanup

After testing, you can:
1. Delete the repository
2. Or fix the vulnerabilities to see alerts resolve
3. Or keep it as a permanent test repository

## Disclaimer

This code is intentionally vulnerable and should only be used for:
- Testing GHAS features
- Security training
- Demonstration purposes

Never deploy this code to production environments!
