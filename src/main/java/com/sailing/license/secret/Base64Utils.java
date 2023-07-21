//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sailing.license.secret;

import org.apache.commons.codec.binary.Base64;

import java.io.*;

public class Base64Utils {
    private static final int CACHE_SIZE = 1024;

    public Base64Utils() {
    }

    public static byte[] decode(String base64) throws Exception {
        Base64 base64Decoder = new Base64();
        return base64Decoder.decode(base64);
    }

    public static String encode(byte[] bytes) throws Exception {
        Base64 base64Decoder = new Base64();
        return new String(base64Decoder.encode(bytes));
    }

    public static String encodeFile(String filePath) throws Exception {
        byte[] bytes = fileToByte(filePath);
        return encode(bytes);
    }

    public static void decodeToFile(String filePath, String base64) throws Exception {
        byte[] bytes = decode(base64);
        byteArrayToFile(bytes, filePath);
    }

    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] cache = new byte[1024];
            boolean var6 = false;

            int nRead;
            while((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }

            out.close();
            in.close();
            data = out.toByteArray();
        }

        return data;
    }

    public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        destFile.createNewFile();
        OutputStream out = new FileOutputStream(destFile);
        byte[] cache = new byte[1024];
        boolean var6 = false;

        int nRead;
        while((nRead = in.read(cache)) != -1) {
            out.write(cache, 0, nRead);
            out.flush();
        }

        out.close();
        in.close();
    }
}
