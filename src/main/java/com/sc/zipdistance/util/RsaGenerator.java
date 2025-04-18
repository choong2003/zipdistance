package com.sc.zipdistance.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class RsaGenerator {
    public static void main(String[] args) throws Exception {
        // Generate RSA Key Pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        // Get the private key in PKCS#8 format
        byte[] privateKeyBytes = kp.getPrivate().getEncoded();

        // Convert private key to PKCS#8 format using Base64 encoding
        System.out.println("-----BEGIN PRIVATE KEY-----");
        System.out.println(Base64.getMimeEncoder().encodeToString(privateKeyBytes));
        System.out.println("-----END PRIVATE KEY-----");

        // Get the public key
        byte[] publicKeyBytes = kp.getPublic().getEncoded();

        // Convert public key to Base64 encoding
        System.out.println("-----BEGIN PUBLIC KEY-----");
        System.out.println(Base64.getMimeEncoder().encodeToString(publicKeyBytes));
        System.out.println("-----END PUBLIC KEY-----");
    }
}
