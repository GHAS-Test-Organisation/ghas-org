# GitHub Advanced Security API - Complete Curl Commands

This document contains all the curl commands that correspond to the API calls made by the GHAS client.

## Prerequisites
```bash
# Set your GitHub token
export GITHUB_TOKEN="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"
export OWNER="dagarachit"
export REPO="ghas-dependabot"
```

## 1. Authentication & User Information

### 1.1 Test Authentication (Get Current User)
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/user
```

## 2. Asset Discovery

### 2.1 Get Organizations
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/user/orgs
```

### 2.2 Get User Repositories
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/user/repos?per_page=100&sort=updated&direction=desc"
```

### 2.3 Get Organization Repositories (if any orgs)
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/orgs/{ORG_NAME}/repos?per_page=100"
```

### 2.4 Get Specific Repository Details
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO
```

### 2.5 Get Repository Collaborators
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/collaborators
```

### 2.6 Get Repository Languages
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/languages
```

### 2.7 Get Repository Contents (Root Directory)
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/contents
```

### 2.8 Get Repository Contents (Specific Directory)
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/contents/sample-vulnerable-app
```

## 3. Security Findings

### 3.1 Dependabot Alerts

#### 3.1.1 Get All Dependabot Alerts
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/dependabot/alerts?per_page=100"
```

#### 3.1.2 Get Open Dependabot Alerts
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/dependabot/alerts?state=open&per_page=100"
```

#### 3.1.3 Get Fixed Dependabot Alerts
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/dependabot/alerts?state=fixed&per_page=100"
```

#### 3.1.4 Get Dismissed Dependabot Alerts
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/dependabot/alerts?state=dismissed&per_page=100"
```

#### 3.1.5 Get Specific Dependabot Alert
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/dependabot/alerts/1
```

### 3.2 Secret Scanning Alerts

#### 3.2.1 Get All Secret Scanning Alerts
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/secret-scanning/alerts?per_page=100"
```

#### 3.2.2 Get Open Secret Scanning Alerts
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/secret-scanning/alerts?state=open&per_page=100"
```

#### 3.2.3 Get Resolved Secret Scanning Alerts
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/secret-scanning/alerts?state=resolved&per_page=100"
```

#### 3.2.4 Get Specific Secret Scanning Alert
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/secret-scanning/alerts/1
```

#### 3.2.5 Get Secret Scanning Alert Locations
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/secret-scanning/alerts/1/locations
```

### 3.3 Code Scanning Alerts

#### 3.3.1 Get All Code Scanning Alerts
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/code-scanning/alerts?per_page=100"
```

#### 3.3.2 Get Open Code Scanning Alerts
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/code-scanning/alerts?state=open&per_page=100"
```

#### 3.3.3 Get Fixed Code Scanning Alerts
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/code-scanning/alerts?state=fixed&per_page=100"
```

#### 3.3.4 Get Dismissed Code Scanning Alerts
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/code-scanning/alerts?state=dismissed&per_page=100"
```

#### 3.3.5 Get Specific Code Scanning Alert
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/code-scanning/alerts/1
```

#### 3.3.6 Get Code Scanning Alert Instances
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/code-scanning/alerts/1/instances
```

### 3.4 Code Scanning Analyses
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/code-scanning/analyses?per_page=100"
```

## 4. Additional Repository Information

### 4.1 Get Repository Topics
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.mercy-preview+json" \
     https://api.github.com/repos/$OWNER/$REPO/topics
```

### 4.2 Get Repository Vulnerability Alerts (Dependabot Status)
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/vulnerability-alerts
```

### 4.3 Get Repository Security and Analysis Settings
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO
```

## 5. Pagination Examples

### 5.1 Get Next Page of Results
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/dependabot/alerts?per_page=100&page=2"
```

### 5.2 Get All Pages (using Link header)
```bash
# First request
curl -I -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/dependabot/alerts?per_page=100"

# Check Link header for next page URL
# Example: Link: <https://api.github.com/repos/owner/repo/dependabot/alerts?page=2>; rel="next"
```

## 6. Filtering and Sorting Examples

### 6.1 Filter Dependabot Alerts by Severity
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/dependabot/alerts?severity=critical"
```

### 6.2 Filter Code Scanning Alerts by Tool
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/code-scanning/alerts?tool_name=CodeQL"
```

### 6.3 Sort Alerts by Creation Date
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/dependabot/alerts?sort=created&direction=desc"
```

## 7. Rate Limit Information

### 7.1 Check Rate Limit Status
```bash
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/rate_limit
```

## 8. Complete Test Script

### 8.1 Test All Endpoints
```bash
#!/bin/bash

# Set variables
export GITHUB_TOKEN="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"
export OWNER="dagarachit"
export REPO="ghas-dependabot"

echo "=== Testing GHAS API Endpoints ==="

echo "1. Authentication..."
curl -s -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/user | jq '.login'

echo "2. Repository Info..."
curl -s -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO | jq '.full_name'

echo "3. Dependabot Alerts..."
curl -s -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/dependabot/alerts" | jq 'length'

echo "4. Secret Scanning Alerts..."
curl -s -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/secret-scanning/alerts" | jq 'length'

echo "5. Code Scanning Alerts..."
curl -s -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     "https://api.github.com/repos/$OWNER/$REPO/code-scanning/alerts" | jq 'length' 2>/dev/null || echo "Not available"

echo "=== Test Complete ==="
```

## 9. Response Headers to Monitor

### 9.1 Important Headers
- `X-RateLimit-Limit`: Total rate limit
- `X-RateLimit-Remaining`: Remaining requests
- `X-RateLimit-Reset`: Reset time (Unix timestamp)
- `Link`: Pagination links
- `X-GitHub-Media-Type`: API version
- `Status`: HTTP status code

### 9.2 Example with Headers
```bash
curl -I -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/dependabot/alerts
```

## 10. Error Handling Examples

### 10.1 Handle 404 (Not Found)
```bash
curl -s -w "%{http_code}" -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$OWNER/$REPO/code-scanning/alerts
```

### 10.2 Handle 403 (Forbidden - Rate Limited)
```bash
curl -s -H "Authorization: Bearer $GITHUB_TOKEN" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/rate_limit
```

---

**Note**: Replace the token and repository details with your actual values. These curl commands correspond exactly to the API calls made by your GHAS Java client.
