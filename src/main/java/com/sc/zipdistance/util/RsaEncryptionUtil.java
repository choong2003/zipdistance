package com.sc.zipdistance.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RsaEncryptionUtil {

    @Value("${rsa.key.private.path}")
    private String privateKeyPath;

    @Value("${rsa.key.public.path}")
    private String publicKeyPath;
    @Autowired
    private EnvironmentUtil environmentUtil;

    // Method to load the private key from a PEM file
    public PrivateKey getPrivateKey() throws Exception {
        // Read the PEM file containing the private key
        InputStream resourceAsStream = loadPrivateKeyBasedOnEnv();
        byte[] keyBytes = resourceAsStream.readAllBytes();
        // Convert byte array to string for easier processing
        String keyString = new String(keyBytes);

        // Remove the PEM headers and footers
        keyString = keyString.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");  // Remove any whitespace characters

        // Check if the Base64 string is valid and decode it
        try {
            byte[] decodedKey = Base64.getDecoder().decode(keyString);

            // Create a PKCS8EncodedKeySpec and generate the private key
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            // Return the private key
            return keyFactory.generatePrivate(spec);
        } catch (IllegalArgumentException e) {
            System.out.println("Error decoding the Base64 key string.");
            throw e;  // Rethrow the exception for further handling
        }
    }

    public InputStream loadPrivateKeyBasedOnEnv() throws IOException {
        if (environmentUtil.isProdEnv()) {
            return new FileInputStream(privateKeyPath);  // Load from external file in prod
        } else {
            // Load from resources in dev
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(privateKeyPath);
            if (resourceAsStream == null) {
                throw new IOException("Private key not found in resources: " + privateKeyPath);
            }
            return resourceAsStream;
        }
    }

    // Method to load the public key from a PEM file
    public PublicKey getPublicKey() throws Exception {
        // Read the PEM file containing the public key
        InputStream resourceAsStream = loadPublicKeyBasedOnEnv();
        byte[] keyBytes = resourceAsStream.readAllBytes();
        // Convert byte array to string for easier processing
        String keyString = new String(keyBytes);

        // Remove the PEM headers and footers
        keyString = keyString.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // Remove any whitespace characters

        // Check if the Base64 string is valid and decode it
        try {
            byte[] decodedKey = Base64.getDecoder().decode(keyString);

            // Create an X509EncodedKeySpec and generate the public key
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            // Return the public key
            return keyFactory.generatePublic(spec);
        } catch (IllegalArgumentException e) {
            System.out.println("Error decoding the Base64 key string.");
            throw e;  // Rethrow the exception for further handling
        }
    }

    public InputStream loadPublicKeyBasedOnEnv() throws IOException {

        if (environmentUtil.isProdEnv()) {
            return new FileInputStream(publicKeyPath);  // Load from external file in prod
        } else {
            // Load from resources in dev
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(publicKeyPath);
            if (resourceAsStream == null) {
                throw new IOException("Public key not found in resources: " + publicKeyPath);
            }
            return resourceAsStream;
        }
    }


}
