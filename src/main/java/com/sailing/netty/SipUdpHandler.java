package com.sailing.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.sipstack.netty.codec.sip.SipMessageEvent;

public class SipUdpHandler extends SimpleChannelInboundHandler<SipMessageEvent> {


    @Override
    protected void channelRead0(ChannelHandlerContext ch, SipMessageEvent response) throws Exception {

        System.out.println("server receive request:{}"+response.getMessage().toString());

//        ch.writeAndFlush(
//                new DatagramPacket(Unpooled.copiedBuffer("结果", Charset.forName("GBK")), new InetSocketAddress("172.20.52.130", 6666))
//        ).sync();
//        Channel channel = session.getClientChannel(ch.channel());
//        channel.writeAndFlush(response.getMessage().toString());
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception{
        //双向关闭
        Channel channel = channelHandlerContext.channel();
        if(channel.isOpen()) {
            channel.close().sync();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("SipUdpHandler exceptionCaught"  + cause.getMessage());
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
