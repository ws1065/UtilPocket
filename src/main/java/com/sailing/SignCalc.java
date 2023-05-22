package com.sailing;

import org.springframework.util.Base64Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-07-14 10:18
 */
public class SignCalc {
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encode(byte[] bytes) throws Exception {
        return Base64.getUrlEncoder().encodeToString(bytes);
    }

    public static String MD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        //拼接计算签名字符串
        //appKey appPwd 由认证服务提供给用户
        //nonce 用户生成的UUID
        //timestamp 用户签名的当前时间戳
        String sailingUserKey = "quanzhou_author";
        String sailingUserSecret = "61f196d8a0311e8305caeb64a8b10ae2";
        String sailingNonce = UUID.randomUUID().toString();
        long sailingTimestamp = System.currentTimeMillis();
        String stringToSign = getMD5(getMD5(sailingUserKey+":"+ sailingUserSecret)+":"+sailingNonce)+sailingTimestamp;
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        byte[] keyBytes = sailingUserKey.getBytes("UTF-8");
        hmacSha256.init(new SecretKeySpec(keyBytes,0,keyBytes.length, "HmacSHA256"));
        byte[] sailingSign = Base64Utils.encode(hmacSha256.doFinal(stringToSign.getBytes("UTF-8")));
        System.out.println("sailingNonce:"+sailingNonce);
        System.out.println("sailingTimestamp:"+sailingTimestamp);
        System.out.println("sailingSign:"+sailingSign);
    }
}