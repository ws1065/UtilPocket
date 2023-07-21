package com.sailing;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-01-22 14:35
 */
public class UnicodeTranslate {
    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\edz\\Downloads\\VSIP_TASK-20200909183137-UP_20210122.2\\VSIP_TASK-20200909183137-UP_20210122.2 - 副本.log");
        if (file.exists()) {

            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int read = inputStream.read(bytes);
            byte[] bytes1 = new byte[read];
            System.arraycopy(bytes,0,bytes1,0,read);
            System.out.println();

//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            List<String> list = new ArrayList<>();
//            String s = "";
//            StringBuffer sb = new StringBuffer();
//            while ((s = reader.readLine()) != null) {
//                System.out.println(s);
//            }
        }
    }
}