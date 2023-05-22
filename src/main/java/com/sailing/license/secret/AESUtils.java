//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sailing.license.secret;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
    public AESUtils() {
    }

    public static String genKeyAES(String keystr) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();
        return Base64Utils.encode(key.getEncoded());
    }

    public static SecretKey loadKeyAES(String base64Key) throws Exception {
        byte[] bytes = Base64Utils.decode(base64Key);
        SecretKeySpec key = new SecretKeySpec(bytes, "AES");
        return key;
    }

    public static byte[] encryptAES(byte[] source, String key) throws Exception {
        SecretKey secretKey = loadKeyAES(key);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(1, secretKey);
        return cipher.doFinal(source);
    }

    public static byte[] decryptAES(byte[] source, String key) throws Exception {
        SecretKey secretKey = loadKeyAES(key);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(2, secretKey);
        return cipher.doFinal(source);
    }
}
