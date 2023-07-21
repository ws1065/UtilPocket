/**
 *
 */
package com.sailing.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.pkts.buffer.Buffer;
import io.pkts.buffer.Buffers;
import io.pkts.packet.sip.SipMessage;
import io.pkts.packet.sip.SipParseException;
import io.pkts.packet.sip.impl.*;
import io.sipstack.netty.codec.sip.Connection;
import io.sipstack.netty.codec.sip.DefaultSipMessageEvent;
import io.sipstack.netty.codec.sip.TcpConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class SipMessageChannelDecoder extends ByteToMessageDecoder {

    public static final byte CR = '\r';

    public static final byte LF = '\n';

    public static final String CONTENT_LENGTH_HEADER = "Content-Length:";

    @Override
    public boolean isSingleDecode() {
        return true;
    }

    boolean rFlag = false;
    boolean nFlag = false;
    byte[] lineBuffer = new byte[9012];
    byte[] headerBuffer = new byte[]{};
    byte[] firstLineBuffer = new byte[]{};
    byte[] bodyBuffer = new byte[]{};
    int len = 0;
    int contentLength = -1;
    //代表开始读取首行
    boolean startFlag = true;
    //代表上一次读取的是body
    boolean lastGet = false;
    int offset = 0;
    int bodyLess = 0;


    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) {
        try {
            log.debug(Thread.currentThread().getName());
            while (buffer.isReadable()) {

                if (lastGet) {
                    int readableLength = buffer.readableBytes();
                    if (readableLength > bodyLess) {
                        buffer.readBytes(bodyBuffer, offset, bodyLess);

                        process(ctx, out, Buffers.wrap(headerBuffer), Buffers.wrap(bodyBuffer));
                        headerBuffer = new byte[]{};
                        bodyBuffer = new byte[]{};
                        len = 0;
                        startFlag = true;
                        lastGet = false;
                    } else {
                        buffer.readBytes(bodyBuffer, offset, readableLength);
                        bodyLess = bodyLess - readableLength;
                        offset = offset + readableLength;
                        lastGet = true;
                    }

                }

                if (buffer.readableBytes() < 1) {
                    continue;
                }

                final byte bytes = buffer.readByte();

                if (bytes == CR) {
                    rFlag = true;
                }
                if (bytes == LF) {
                    nFlag = true;
                }
                lineBuffer[len] = bytes;
                len++;

                if (rFlag && nFlag) {
                    rFlag = false;
                    nFlag = false;
                    if (len == 2 && lineBuffer[0] == CR && lineBuffer[1] == LF
                            && contentLength > 0 && headerBuffer.length > 2
                            && headerBuffer[headerBuffer.length - 1] == LF && headerBuffer[headerBuffer.length - 2] == CR) {
                        headerBuffer = concat(headerBuffer, Arrays.copyOf(lineBuffer, len));
                        bodyBuffer = new byte[contentLength];
                        offset = 0;
                        bodyLess = contentLength;

                        int readableLength = buffer.readableBytes();
                        log.debug("contentLength:{},readableLength:{}", contentLength, readableLength);
                        if (readableLength > contentLength) {
                            buffer.readBytes(bodyBuffer, offset, contentLength);

                            process(ctx, out, Buffers.wrap(headerBuffer), Buffers.wrap(bodyBuffer));
                            headerBuffer = new byte[]{};
                            bodyBuffer = new byte[]{};
                            len = 0;
                            startFlag = true;
                        } else {
                            buffer.readBytes(bodyBuffer, offset, readableLength);
                            offset = readableLength;
                            bodyLess = contentLength - offset;
                            lastGet = true;
                        }
                        //如果是空行，并且content-length为0，将当前header完成解析
                    } else if (len == 2 && lineBuffer[0] == CR && lineBuffer[1] == LF
                            && contentLength == 0 && headerBuffer.length > 2
                            && headerBuffer[headerBuffer.length - 1] == LF && headerBuffer[headerBuffer.length - 2] == CR) {
                        headerBuffer = concat(headerBuffer, Arrays.copyOf(lineBuffer, len));
                        process(ctx, out, Buffers.wrap(headerBuffer), Buffers.EMPTY_BUFFER);

                        headerBuffer = new byte[]{};
                        len = 0;
                        startFlag = true;
                    } else {
                        String line = new String(lineBuffer, 0, len, "GBK");
                        //如果是content-length，将当前行解析为content-length
                        if (line.startsWith(CONTENT_LENGTH_HEADER)) {
                            String[] split = line.split(":");
                            contentLength = Integer.parseInt(split[1].trim());
                            headerBuffer = concat(headerBuffer, Arrays.copyOf(lineBuffer, len));
                            len = 0;
                        } else if (startFlag) {
                            //如果是首行，将当前行解析为首行
                            byte[] lineByte = lineBuffer;
                            lineBuffer = new byte[9012];
                            firstLineBuffer = Arrays.copyOf(lineByte, len);
                            len = 0;
                            startFlag = false;
                        } else {
                            //将当前行诺列进headerBuffer
                            headerBuffer = concat(headerBuffer, Arrays.copyOf(lineBuffer, len));
                            len = 0;
                        }
                    }
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }
    private void process(ChannelHandlerContext ctx, List<Object> out,Buffer headers,Buffer payload) {
        long arrivalTime = System.currentTimeMillis();
        SipMessage msg = null;

        try {


            SipInitialLine initialLine = SipInitialLine.parse(Buffers.wrap(firstLineBuffer));
            if (initialLine.isRequestLine()) {
                msg =  new SipRequestImpl((SipRequestLine) initialLine, headers, payload);
            } else {
                msg =  new SipResponseImpl((SipResponseLine) initialLine, headers, payload);
            }
        }catch (SipParseException e){
            log.error("firstLineBuffer:{},headers:{},bodys:{}",new String(firstLineBuffer),new String(headers.getArray()),new String(payload.getArray()));
        }

        final Channel channel = ctx.channel();
        final Connection connection = new TcpConnection(channel, (InetSocketAddress) channel.remoteAddress());
        out.add(new DefaultSipMessageEvent(connection, msg, arrivalTime));
    }

    byte[] concat(byte[] a, byte[] b) {

        byte[] c= new byte[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

}
