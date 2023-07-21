//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sailing.license.secret;

import java.security.MessageDigest;

public class MD5Util {
    public MD5Util() {
    }

    public static String getMD5(String str) {
        String md5Key = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] b = md.digest();
            StringBuffer buf = new StringBuffer("");

            for(int offset = 0; offset < b.length; ++offset) {
                int i = b[offset];
                if (i < 0) {
                    i += 256;
                }

                if (i < 16) {
                    buf.append("0");
                }

                buf.append(Integer.toHexString(i));
            }

            md5Key = buf.toString();
        } catch (Exception var7) {
            var7.printStackTrace();
            md5Key = str;
        }

        return md5Key;
    }
}
