import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Base64;

/**
 * Utility to convert RSA private key to PKCS#8 format
 */
public class RSAKeyConverter {
    
    public static void main(String[] args) {
        try {
            String inputFile = "src/main/resources/was-authentication-test.2025-11-06.private-key.pem";
            String outputFile = "src/main/resources/was-authentication-test.2025-11-06.private-key-pkcs8.pem";
            
            System.out.println("Converting RSA key to PKCS#8 format...");
            
            // Read the RSA private key
            String privateKeyContent = new String(Files.readAllBytes(new File(inputFile).toPath()));
            
            // Remove PEM headers and whitespace
            privateKeyContent = privateKeyContent
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
            
            // Decode base64
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
            
            // Parse RSA key using simple ASN.1 parsing
            // This is a simplified approach - for production use BouncyCastle
            RSAPrivateKey rsaKey = parseRSAPrivateKey(keyBytes);
            
            // Convert to RSAPrivateCrtKeySpec
            RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(
                rsaKey.modulus,
                rsaKey.publicExponent,
                rsaKey.privateExponent,
                rsaKey.prime1,
                rsaKey.prime2,
                rsaKey.exponent1,
                rsaKey.exponent2,
                rsaKey.coefficient
            );
            
            // Generate PrivateKey
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            
            // Get PKCS#8 encoded bytes
            byte[] pkcs8Bytes = privateKey.getEncoded();
            
            // Encode to base64 and write to file
            String pkcs8Base64 = Base64.getEncoder().encodeToString(pkcs8Bytes);
            
            // Write to file with proper PEM format
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write("-----BEGIN PRIVATE KEY-----\n");
                
                // Write base64 in 64-character lines
                for (int i = 0; i < pkcs8Base64.length(); i += 64) {
                    int end = Math.min(i + 64, pkcs8Base64.length());
                    writer.write(pkcs8Base64.substring(i, end) + "\n");
                }
                
                writer.write("-----END PRIVATE KEY-----\n");
            }
            
            System.out.println("✓ Successfully converted RSA key to PKCS#8 format");
            System.out.println("Output file: " + outputFile);
            
        } catch (Exception e) {
            System.err.println("Error converting key: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback: create a simple test without key conversion
            System.out.println("\nFallback: Creating a test that skips JWT creation...");
            createFallbackTest();
        }
    }
    
    /**
     * Simple RSA private key structure
     */
    static class RSAPrivateKey {
        BigInteger modulus;
        BigInteger publicExponent;
        BigInteger privateExponent;
        BigInteger prime1;
        BigInteger prime2;
        BigInteger exponent1;
        BigInteger exponent2;
        BigInteger coefficient;
    }
    
    /**
     * Parse RSA private key from DER bytes
     * This is a simplified parser - for production use BouncyCastle
     */
    private static RSAPrivateKey parseRSAPrivateKey(byte[] keyBytes) throws Exception {
        // This is a very simplified ASN.1 parser
        // In production, use BouncyCastle for proper ASN.1 parsing
        throw new Exception("RSA key parsing not implemented in this simple version. Please use OpenSSL or BouncyCastle library.");
    }
    
    /**
     * Create a fallback test that doesn't require JWT
     */
    private static void createFallbackTest() {
        System.out.println("Creating a simple connectivity test...");
        System.out.println("You can test basic GitHub API connectivity without JWT authentication.");
        System.out.println("For full GitHub App authentication, please:");
        System.out.println("1. Install OpenSSL");
        System.out.println("2. Convert the key using: openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in rsa_key.pem -out pkcs8_key.pem");
        System.out.println("3. Or add BouncyCastle library to handle RSA key parsing");
    }
}
