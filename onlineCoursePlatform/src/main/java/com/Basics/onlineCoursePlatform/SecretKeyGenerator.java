package com.Basics.onlineCoursePlatform;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        keyGen.init(256); // Initialize the key generator with a key size of 256 bits
        SecretKey sk = keyGen.generateKey();
        String secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        System.out.println(secretKey);
    }
}

