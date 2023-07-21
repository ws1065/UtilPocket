package com.sailing.nioTest;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

@Slf4j
public class UDPClient {

    /**
     * 获取用户输入的数据，发送到远程服务器
     */
    public void send() throws IOException {
        try (final DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);

            final ByteBuffer buffer = ByteBuffer.allocate(1024);

            final Scanner scanner = new Scanner(System.in);
            log.info("UDP客户端启动成功");
            log.info("》》请输入内容：");
            channel.connect(new InetSocketAddress("127.0.0.1", 8888));
            while (scanner.hasNext()) {
                final String input = scanner.next();
                buffer.put(input.getBytes());
                buffer.flip();
                channel.write(buffer);
                buffer.clear();

            }
        }
    }

    /**
     * client
     */
    public static void main(String[] args) throws IOException {
        new UDPClient().send();
    }
}
