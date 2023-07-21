package com.sailing;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

public class SignDataVSCGMain {
    public static void main(String[] args) throws Exception {
        //拼接计算签名字符串
        //appKey appPwd 由认证服务提供给用户
        //nonce 用户生成的UUID
        //timestamp 用户签名的当前时间戳
        String sailingUserKey = "author";
        String sailingUserSecret = "5fa0a58400fdd3f794899e8a3b174a28";
        String sailingNonce = UUID.randomUUID().toString();
        long sailingTimestamp = System.currentTimeMillis();
        String stringToSign = getMD5(getMD5(sailingUserKey+":"+ sailingUserSecret)+":"+sailingNonce)+sailingTimestamp;
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        byte[] keyBytes = sailingUserKey.getBytes("UTF-8");
        hmacSha256.init(new SecretKeySpec(keyBytes,0,keyBytes.length, "HmacSHA256"));
        String sailingSign = base64UtilsEncode(hmacSha256.doFinal(stringToSign.getBytes("UTF-8")));
        System.out.println("sailingNonce:"+sailingNonce);
        System.out.println("sailingTimestamp:"+sailingTimestamp);
        System.out.println("sailingSign:"+sailingSign);
    }
    public static String base64UtilsEncode(byte[] bytes) throws Exception {
        return new String(new org.apache.commons.codec.binary.Base64().encode(bytes));
    }

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
}