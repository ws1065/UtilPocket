package com.sailing.nioTest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class StatisticsServer {
    //每次发送接收的数据包大小
    private final int MAX_BUFF_SIZE = 1024 * 10;
    //服务端监听端口，客户端也通过该端口发送数据
    private int port;
    private DatagramChannel channel;
    private Selector selector;

    private ScheduledExecutorService es = Executors.newScheduledThreadPool(1);

    public void init() throws IOException {
        //创建通道和选择器
        selector = Selector.open();
        channel = DatagramChannel.open();
        //设置为非阻塞模式
        channel.configureBlocking(false);
        channel.socket().bind(new InetSocketAddress(port));
        //将通道注册至selector，监听只读消息（此时服务端只能读数据，无法写数据）
        channel.register(selector, SelectionKey.OP_READ);

        //使用线程的方式，保证服务端持续等待接收客户端数据
        es.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    while(selector.select() > 0) {
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while(iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            try {
                                iterator.remove();
                                if(key.isReadable()) {
                                    //接收数据
                                    doReceive(key);
                                }
                            } catch (Exception e) {
                                log.error("SelectionKey receive exception", e);
                                try {
                                    if (key != null) {
                                        key.cancel();
                                        key.channel().close();
                                    }
                                } catch (ClosedChannelException cex) {
                                    log.error("Close channel exception", cex);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("selector.select exception", e);
                }
            }
        }, 0L, 2L, TimeUnit.MINUTES);

    }
    private void doReceive(SelectionKey key) throws IOException {
        String content = "";
        DatagramChannel sc = (DatagramChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(MAX_BUFF_SIZE);
        buffer.clear();
        sc.receive(buffer);
        buffer.flip();
        while(buffer.hasRemaining()) {
            byte[] buf = new byte[buffer.limit()];
            buffer.get(buf);
            content += new String(buf);
        }
        buffer.clear();
        log.debug("receive content="+content);
        if(StringUtils.isNotBlank(content)) {
            //doSave(content);
        }
    }
    //处理接收到的数据
    private void doSend(SelectionKey key) throws IOException {
        DatagramChannel channel = DatagramChannel.open();

        StringBuilder sb = new StringBuilder();
        sb.append("2017-03-09 12:30:00;")
                .append("aaa")
                .append("testapp;")
                .append("test.do;")
                .append("param=hello;")
                .append("test;")
                .append("100;")
                .append("1");
        ByteBuffer buffer = ByteBuffer.allocate(10240);
        buffer.clear();
        buffer.put(sb.toString().getBytes());
        buffer.flip();
        //此处IP为服务端IP地址，端口和服务端的端口一致
        int n = channel.send(buffer, new InetSocketAddress("127.0.0.1", 8080));
        System.out.println(n);
        //每次数据发送完毕之后，一定要调用close方法，来关闭占用的udp端口，否则程序不结束，端口不会释放
        channel.close();
    }

    
 }
