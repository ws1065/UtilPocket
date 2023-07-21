package com.sailing;

import java.io.*;
import java.util.Arrays;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-03-03 15:28
 */
public class 文件遍历 {


    public static int num = 0;

    public static void main(String[] args) {


        byte[] bytes = new byte[]{0, 127, 1, 0, 0, 0, 79, 80, 84, 73, 79, 78, 83, 32, 115, 105, 112, 58, 49, 50, 50, 50, 55, 50, 49, 49, 51, 48, 48, 48, 48, 48, 48, 48, 48, 53, 64, 49, 50, 50, 50, 55, 50, 59, 77, 83, 71, 95, 84, 89, 80, 69, 61, 77, 83, 71, 95, 68, 66, 83, 95, 83, 77, 83, 95, 72, 69, 65, 82, 84, 32, 83, 73, 80, 47, 50, 46, 48, 13, 10, 86, 105, 97, 58, 32, 83, 73, 80, 47, 50, 46, 48, 47, 84, 67, 80, 32};
        int i = 0;
        for (; i < bytes.length; i++) {
            if (('A'< bytes[i] && bytes[i] <'Z') || (('a'< bytes[i] && bytes[i] <'z'))){
                break;
            }
        }
        if (i != 0) {
            bytes =  Arrays.copyOfRange(bytes,i,bytes.length);
        }
        File file = new File("D:\\Documents\\WeChat Files\\wxid_lsur2emrh56942\\FileStorage\\File\\2021-03\\messages");

        //循环遍历(file);
        读取遍历文件(file);

    }

    private static void 读取遍历文件(File file) {
        if (file.exists() && file.isFile()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String s = reader.readLine();
                while (s!=null) {

                    if (!s.contains("i40e")) {
                        System.out.println(s);
                    }
                    s = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void 循环遍历(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                循环遍历(f);
            }

        }else if (file.isFile()){
            String name = file.getName();
            if (!name.contains("英语")){
                boolean delete = file.delete();
                System.out.println(delete +":"+name);
            }else {
//                num++;
//                System.out.println(num);
//                System.out.println(name);
            }
        }
    }
}