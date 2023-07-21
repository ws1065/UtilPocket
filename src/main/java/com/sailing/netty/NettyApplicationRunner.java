package com.sailing.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.sipstack.netty.codec.sip.SipMessageDatagramDecoder;
import io.sipstack.netty.codec.sip.SipMessageEncoder;

import java.net.InetAddress;

public class NettyApplicationRunner {
    private static String localAddr = "127.0.0.1";
    private static int localPort = 80;
    private static String remoteaddr = "127.0.0.1";
    private static int remotePort = 80;
    private Bootstrap clientBootstrap;
    private ServerBootstrap serverBootstrap;

    public static void main(String[] args) throws Exception {

        localAddr = args[0];
        localPort = Integer.parseInt(args[1]);

        new NettyApplicationRunner().start();
    }


    public void start() throws Exception {
        new Thread(()-> {
            final Bootstrap b = new Bootstrap();
            b.group(new NioEventLoopGroup())
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(final DatagramChannel client) throws Exception {
                            System.out.println("InitChannel client:" + client);

                            final ChannelPipeline pipeline = client.pipeline();
                            pipeline.addLast("decoder", new SipMessageDatagramDecoder());
                            pipeline.addLast("encoder", new SipMessageEncoder());
                            pipeline.addLast("handler", new SipUdpHandler());

                        }
                    });
            boolean bindStatus = true;
            while (bindStatus) {
                try {
                    b.bind(InetAddress.getByName(localAddr), localPort).sync().channel().closeFuture().await();
                    bindStatus = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    bindStatus = true;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }).start();
        new Thread(()-> {
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(new NioEventLoopGroup(),new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)

                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel client) throws Exception {
                            //accept client
                            System.out.println("InitChannel child:" + client);
                            ChannelPipeline pipeline = client.pipeline();
                            pipeline.addLast("decoder", new SipMessageChannelDecoder());
                            pipeline.addLast("encoder", new SipMessageEncoder());
                            pipeline.addLast("handler", new SipTcpServerHandler());
                        }

                    })
                    .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                    .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
            boolean bindStatus = true;
            while (bindStatus) {
                try {
                    serverBootstrap.bind(InetAddress.getByName(localAddr), localPort).sync().channel().closeFuture().await();
                    bindStatus = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    bindStatus = true;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }).start();
        System.out.println("Netty Proxy started!");
    }
}
