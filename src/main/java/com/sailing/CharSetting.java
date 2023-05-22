package com.sailing;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-09-07 10:50
 */
public class CharSetting {
    public static void main(String[] args) throws IOException {
        String a = "5245474953544552207369703a33333033303030323030323030303030303039394033332e3131322e342e3133303a3137303030205349502f322e300d0a43616c6c2d49443a2030613430643963323363616339613162336662386237313539643064353430314033332e3131322e32352e3130360d0a435365713a20312052454749535445520d0a46726f6d3a203c7369703a333330333030303230303230303030313030313940333330333030303230303e3b7461673d63353832333866316266336134643137626365336132346434623130313830330d0a546f3a203c7369703a333330333030303230303230303030313030313940333330333030303230303e0d0a5669613a205349502f322e302f5443502033332e3131322e32352e3130363a31353036303b72706f72743b6272616e63683d7a39684734624b2d3339333833392d65663630323939663161333162363234373362363637643538616466626566610d0a4d61782d466f7277617264733a2037300d0a457870697265733a203330300d0a436f6e746163743a203c7369703a33333033303030323030323030303031303031394033332e3131322e32352e3130363a31353036303e0d0a436f6e74656e742d4c656e6774683a20300d0a0d0a";
        String s = new String(CharSetting.hexStringToString(a));

        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes()),"gb2312"));
        //reader.read(bytes, 0, bytes.length);
        String line;
        while ((line = reader.readLine()) != null) {
                System.out.println(line.toString());
            }

        byte[] bytes = hexStringToString(a);
    }
    public static byte[] hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return baKeyword;
    }
}