package com.sailing;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-11-18 11:43
 */
public class JarFileTest {

    public static void main(String[] args) throws IOException {

        JarFile jarFile = new JarFile("D:\\developWork\\VCS\\sourceCode\\VCS-WebConsoleBackend\\target\\vsc_ws.war");
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
        }
        System.out.println();


    }
}