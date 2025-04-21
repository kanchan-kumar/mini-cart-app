package com.example.keytabgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.DESKeySpec;

@SpringBootApplication
public class KeytabGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(KeytabGeneratorApplication.class, args);
    }
    
    @RestController
    public static class KeytabController {
        
        // Map of encryption type names to their numeric identifiers
        private static final Map<String, Integer> ENCRYPTION_TYPES = new HashMap<>();
        static {
            ENCRYPTION_TYPES.put("DES-CBC-CRC", 1);
            ENCRYPTION_TYPES.put("DES-CBC-MD4", 2);
            ENCRYPTION_TYPES.put("DES-CBC-MD5", 3);
            ENCRYPTION_TYPES.put("DES3-CBC-SHA1", 16);
            ENCRYPTION_TYPES.put("AES128-CTS-HMAC-SHA1-96", 17);
            ENCRYPTION_TYPES.put("AES256-CTS-HMAC-SHA1-96", 18);
            ENCRYPTION_TYPES.put("ARCFOUR-HMAC", 23);
            ENCRYPTION_TYPES.put("ARCFOUR-HMAC-EXP", 24);
            ENCRYPTION_TYPES.put("CAMELLIA128-CTS-CMAC", 25);
            ENCRYPTION_TYPES.put("CAMELLIA256-CTS-CMAC", 26);
        }
        
        // Map of encryption types to their key lengths in bytes
        private static final Map<Integer, Integer> DEFAULT_KEY_LENGTHS = new HashMap<>();
        static {
            DEFAULT_KEY_LENGTHS.put(1, 8);  // DES-CBC-CRC
            DEFAULT_KEY_LENGTHS.put(2, 8);  // DES-CBC-MD4
            DEFAULT_KEY_LENGTHS.put(3, 8);  // DES-CBC-MD5
            DEFAULT_KEY_LENGTHS.put(16, 24); // DES3-CBC-SHA1
            DEFAULT_KEY_LENGTHS.put(17, 16); // AES128
            DEFAULT_KEY_LENGTHS.put(18, 32); // AES256
            DEFAULT_KEY_LENGTHS.put(23, 16); // ARCFOUR-HMAC
            DEFAULT_KEY_LENGTHS.put(24, 16); // ARCFOUR-HMAC-EXP
            DEFAULT_KEY_LENGTHS.put(25, 16); // CAMELLIA128
            DEFAULT_KEY_LENGTHS.put(26, 32); // CAMELLIA256
        }
        
        @PostMapping("/generate-keytab")
        public ResponseEntity<?> generateKeytab(@RequestBody KeytabRequest request) {
            try {
                // Get the encryption type numeric identifier
                Integer encType = request.getKeyType() != null ? 
                    ENCRYPTION_TYPES.getOrDefault(request.getKeyType().toUpperCase(), 18) : 18; // Default to AES256
                
                // Get key length (use default for the encryption type if not specified)
                int keyLength = request.getKeyLength() != null && request.getKeyLength() > 0 ? 
                    request.getKeyLength() : DEFAULT_KEY_LENGTHS.getOrDefault(encType, 32);
                
                // Generate keytab file
                String filePath = "generated-" + System.currentTimeMillis() + ".keytab";
                
                // Determine if we should return the keytab data or write to file
                if (request.isReturnKeytabData()) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    generateKeytabFile(request.getPrincipal(), request.getPassword(), 
                                       request.getRealm(), outputStream, encType, keyLength);
                    
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    headers.setContentDispositionFormData("attachment", "generated.keytab");
                    
                    return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
                } else {
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(filePath));
                    generateKeytabFile(request.getPrincipal(), request.getPassword(), 
                                       request.getRealm(), fileOutputStream, encType, keyLength);
                    fileOutputStream.close();
                    
                    return ResponseEntity.ok("Keytab file generated successfully at: " + filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body("Error generating keytab: " + e.getMessage());
            }
        }
        
        private void generateKeytabFile(String principal, String password, String realm, 
                                      OutputStream outputStream, Integer encType, int keyLength) 
                throws IOException {
            // A keytab file is a binary file format that contains the following elements:
            // - Keytab file format version (2 bytes)
            // - Entry count (2 bytes)
            // - Entries: for each entry
            //   - Principal components count (2 bytes)
            //   - Realm (string)
            //   - Principal components (strings)
            //   - Name type (4 bytes)
            //   - Timestamp (4 bytes)
            //   - Key version (1 byte)
            //   - Key type (2 bytes)
            //   - Key length (2 bytes)
            //   - Key data (key length bytes)
            
            // Write keytab file format version (version 0x502)
            outputStream.write(0x05);
            outputStream.write(0x02);
            
            // We're going to create a single entry
            outputStream.write(0x00);
            outputStream.write(0x01);
            
            // Generate key entry
            String fullPrincipal = principal + "@" + realm;
            byte[] keyData = generateKeyFromPassword(password, fullPrincipal, keyLength);
            
            // Split principal into components
            List<String> components = new ArrayList<>();
            for (String component : principal.split("/")) {
                components.add(component);
            }
            
            // Write principal components count
            outputStream.write(0x00);
            outputStream.write(components.size());
            
            // Write realm
            byte[] realmBytes = realm.getBytes("UTF-8");
            outputStream.write(realmBytes.length);
            outputStream.write(realmBytes);
            
            // Write principal components
            for (String component : components) {
                byte[] componentBytes = component.getBytes("UTF-8");
                outputStream.write(componentBytes.length);
                outputStream.write(componentBytes);
            }
            
            // Write name type (1 = KRB5_NT_PRINCIPAL)
            outputStream.write(0x00);
            outputStream.write(0x00);
            outputStream.write(0x00);
            outputStream.write(0x01);
            
            // Write timestamp (current time in seconds since epoch)
            long timestamp = System.currentTimeMillis() / 1000;
            outputStream.write((int) ((timestamp >> 24) & 0xFF));
            outputStream.write((int) ((timestamp >> 16) & 0xFF));
            outputStream.write((int) ((timestamp >> 8) & 0xFF));
            outputStream.write((int) (timestamp & 0xFF));
            
            // Write key version (1)
            outputStream.write(0x01);
            
            // Write key type (encType value)
            outputStream.write((encType >> 8) & 0xFF);
            outputStream.write(encType & 0xFF);
            
            // Write key length
            outputStream.write((keyLength >> 8) & 0xFF);
            outputStream.write(keyLength & 0xFF);
            
            // Write key data
            outputStream.write(keyData);
        }
        
        private byte[] generateKeyFromPassword(String password, String principal, int keyLength) {
            // This is a simplified implementation for demonstration purposes
            // In a real-world scenario, you would use proper key derivation functions
            // that adhere to Kerberos standards
            
            try {
                // Create a seed from the principal name
                byte[] principalBytes = principal.getBytes("UTF-8");
                byte[] passwordBytes = password.getBytes("UTF-8");
                
                // Combine password and principal
                byte[] combined = new byte[principalBytes.length + passwordBytes.length];
                System.arraycopy(passwordBytes, 0, combined, 0, passwordBytes.length);
                System.arraycopy(principalBytes, 0, combined, passwordBytes.length, principalBytes.length);
                
                // Generate a key using SecureRandom with the combined seed
                SecureRandom random = new SecureRandom(combined);
                byte[] key = new byte[keyLength];
                random.nextBytes(key);
                
                // Ensure the key is valid if using DES-based encryption (key length of 8)
                if (keyLength % 8 == 0) {
                    for (int i = 0; i < key.length; i += 8) {
                        DESKeySpec.setOddParity(key, i);
                    }
                }
                
                return key;
            } catch (Exception e) {
                throw new RuntimeException("Error generating key from password", e);
            }
        }
    }
    
    public static class KeytabRequest {
        private String principal;
        private String password;
        private String realm;
        private String keyType;
        private Integer keyLength;
        private boolean returnKeytabData;
        
        public String getPrincipal() {
            return principal;
        }
        
        public void setPrincipal(String principal) {
            this.principal = principal;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
        
        public String getRealm() {
            return realm;
        }
        
        public void setRealm(String realm) {
            this.realm = realm;
        }
        
        public String getKeyType() {
            return keyType;
        }
        
        public void setKeyType(String keyType) {
            this.keyType = keyType;
        }
        
        public Integer getKeyLength() {
            return keyLength;
        }
        
        public void setKeyLength(Integer keyLength) {
            this.keyLength = keyLength;
        }
        
        public boolean isReturnKeytabData() {
            return returnKeytabData;
        }
        
        public void setReturnKeytabData(boolean returnKeytabData) {
            this.returnKeytabData = returnKeytabData;
        }
    }
}
