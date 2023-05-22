package com.sailing.unix_socket;

import fi.solita.clamav.ClamAVClient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-03-15 14:08
 */
public class FileScan {
    public static void main(String[] args) {
        ClamAVClient a = new ClamAVClient("172.20.52.130", 3310);
        try {
            Scanner scan = new Scanner(System.in);
            StringBuffer sb=  new StringBuffer();
            while (scan.hasNext()) {
                String next = scan.next();
                if (next.contains("EOF")){
                    break;
                }
                sb.append(next).append(System.lineSeparator());
            }
            System.out.println(sb);
            String data = "jnr-unixsocket\n" +
                    "Native I/O access for java.\n" +
                    "\n" +
                    "Check out the examples for more information.";
            List<byte[]> list = new ArrayList<>();
            long l = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                byte[] r = a.scan(new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
                list.add(r);
            }
            long l1 = System.currentTimeMillis();
            double v = (l1 - l) / 100.0;
            System.out.println("100次平均每次用时:"+v);
            for (byte[] w : list) {
                System.out.println("Everything ok : " + ClamAVClient.isCleanReply(w) + "\n");
            }
            System.out.println("100次平均每次用时:"+v);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}