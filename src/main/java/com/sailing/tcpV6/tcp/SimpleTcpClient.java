package com.sailing.tcpV6.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-03-10 16:46
 */
public class SimpleTcpClient {

    public static void main(String[] args) {
        new SimpleTcpClient().run("::0",9966+"");
    }

    public void run(String ip, String portStr){
        try {
            Socket  socket = new Socket(InetAddress.getByName(ip),Integer.parseInt(portStr));
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("skkkkks".getBytes(StandardCharsets.UTF_8));
            InputStream inputStream = socket.getInputStream();
            int read = inputStream.read();
            System.out.println(read);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}