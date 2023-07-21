package com.sailing.nioTest;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * UDP server
 *
 * @author jimo
 * @version 1.0.0
 * @date 2020/7/12 11:42
 */
@Slf4j
public class UDPServer {

    /**
     * 接收客户端请求
     */
    public void receive() throws IOException {
        final DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress("0.0.0.0", 8080));
        log.info("UDP服务器启动");

        final Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);

        // 通过选择器，查询IO事件
        while (selector.select() > 0) {
            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            final ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (iterator.hasNext()) {
                final SelectionKey selectionKey = iterator.next();
                // 可读事件，有数据到来
                if (selectionKey.isReadable()) {
                    final SocketAddress client = channel.receive(buffer);
                    buffer.flip();
                    log.info("收到客户端消息：{}", new String(buffer.array(), 0, buffer.limit()));
                }
            }
            iterator.remove();
        }
        selector.close();
        channel.close();
    }

    /**
     * main
     */
    public static void main(String[] args) throws IOException {
        new UDPServer().receive();
    }
}
