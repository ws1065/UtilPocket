package com.sailing.jpcap;

import cn.hutool.core.io.FileUtil;
import io.pkts.Pcap;
import io.pkts.buffer.Buffer;
import io.pkts.framer.FramingException;
import io.pkts.packet.IPPacket;
import io.pkts.packet.Packet;
import io.pkts.packet.TransportPacket;
import io.pkts.protocol.Protocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class PcapParser {

    public static void main(String[] args) throws IOException {
        String filename = "D:\\developWork\\VSCG\\国标文档\\码流抓包\\转发实时点播流.pcap";
        if (args.length >= 2) {
            filename = args[1];
        }
        Pcap pcap = Pcap.openStream(new File(filename));



        pcap.loop(packet -> {
            getTransportPacket(packet).ifPresent(transportPacket -> {
                final int sourcePort = transportPacket.getSourcePort();
                final int destPort = transportPacket.getDestinationPort();
                // 假设都是 utf-8
                Buffer payload1 = transportPacket.getPayload();


                final IPPacket ip = transportPacket.getParentPacket();
                final String destIp = ip.getDestinationIP();
                final String sourceIp = ip.getSourceIP();

                System.out.println(destIp + ":" + destPort + " -> " + sourceIp + ":" + sourcePort);
                if (payload1 != null) {
                    final String payload = payload1.toString();
                    System.out.println(payload);
                }

                System.out.println();
                System.out.println();
            });
            return true;
        });
    }

    /**
     * 获取传输包
     *
     * @param packet
     * @return
     * @throws IOException
     */
    private static Optional<TransportPacket> getTransportPacket(final Packet packet) throws IOException {
        if (packet.hasProtocol(Protocol.TCP)) {
            // TCP is a transport protocol and therefore, it extends the base packet
            // TransportPacket. You could also convert it into a TCPPacket but for
            // this example, we are not interested in TCP packet specific information.
            // Just Transport Layer in general (source ip, dest ip etc)
            return Optional.of((TransportPacket) packet.getPacket(Protocol.TCP));
        }
        if (packet.hasProtocol(Protocol.UDP)) {
            return Optional.of((TransportPacket) packet.getPacket(Protocol.UDP));
        }
        return Optional.empty();
    }

    /**
     * 读取pcap包中rtp流
     * @throws FileNotFoundException
     * @throws IOException
     * @throws FramingException
     */
    public static void pcapRtp() throws FileNotFoundException, IOException, FramingException {
        Pcap pcap = Pcap.openStream(new File("D:\\developWork\\VSCG\\国标文档\\码流抓包\\转发实时点播流.pcap"));
        // Byte[] rawArray = transportPacket.getPayload().getRawArray();


        // rawArray
        // FileUtil.writeBytes(stream, file);
        // byte[] stream = new byte[rawArray.length];
        final List<Buffer> buffers = new ArrayList<>();
        pcap.loop(packet -> {

            getTransportPacket(packet).ifPresent(transportPacket -> {
                final int sourcePort = transportPacket.getSourcePort();
                final int destPort = transportPacket.getDestinationPort();
                // final String payload = transportPacket.getPayload().toString();

                final IPPacket ip = transportPacket.getParentPacket();
                final String destIp = ip.getDestinationIP();
                final String sourceIp = ip.getSourceIP();
                buffers.add(transportPacket.getPayload());
                System.out.println(destIp + ":" + destPort + " -> " + sourceIp + ":" + sourcePort);
                // System.out.println(payload);
                System.out.println();
                System.out.println();
            });
            return true;
        });
        byte[] stream = new byte[0];
        AtomicLong aLong = new AtomicLong(0);
        for (Buffer buffer : buffers) {
            byte[] rawArray = buffer.getArray();
            aLong.addAndGet(rawArray.length);
            stream = concat(stream, rawArray);
        }
        File file = new File("C:\\Users\\WEN\\Desktop\\杭州熙菱\\相关任务\\18 JPcap 相关\\testsExport.raw");
        // // file.deleteOnExit();
        file.createNewFile();
        // 能播但是糊的一批
        FileUtil.writeBytes(stream, file);
        // System.out.println(aLong.get());
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
