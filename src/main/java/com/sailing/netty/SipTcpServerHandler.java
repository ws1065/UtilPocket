package com.sailing.netty;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.sipstack.netty.codec.sip.SipMessageEvent;


public class SipTcpServerHandler extends SimpleChannelInboundHandler<SipMessageEvent> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SipMessageEvent sipMessageEvent) throws Exception {
        //System.out.println("server receive request:{}"+sipMessageEvent.getMessage().toString());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception{
        /**
         * 双向关闭
         */
        Channel channel = channelHandlerContext.channel();
        if(channel.isOpen()) {
            channel.close().sync();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
