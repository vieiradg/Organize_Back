package com.organize.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;       

@Component 
public class CryptoUtils {

    
    @Value("${app.crypto.secret}")
    private String secret;

    private static final String ALGORITHM = "AES";

    public String encrypt(String plainText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar relatório", e);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), ALGORITHM); 
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            return new String(cipher.doFinal(decoded));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar relatório", e); 
        }
    }
}