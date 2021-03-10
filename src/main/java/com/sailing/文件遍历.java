package com.sailing;

import java.io.*;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-03-03 15:28
 */
public class 文件遍历 {


    public static int num = 0;

    public static void main(String[] args) {

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