package com.sailing.tcp;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-03-10 16:46
 */
@Slf4j
public class SimpleTcpClient {

    public static void main(String[] args) throws IOException {


        new SimpleTcpClient().run("127.0.0.1",5060+"");
    }
    private  byte[] intToBytes(long n, int offset, boolean big){
        byte[] array = new byte[4];
        if(big){
            array[3+offset] = (byte) (n & 0xff);
            array[2+offset] = (byte) (n >> 8 & 0xff);
            array[1+offset] = (byte) (n >> 16 & 0xff);
            array[offset] = (byte) (n >> 24 & 0xff);
        }else{
            array[offset] = (byte) (n & 0xff);
            array[1+offset] = (byte) (n >> 8 & 0xff);
            array[2+offset] = (byte) (n >> 16 & 0xff);
            array[3+offset] = (byte) (n >> 24 & 0xff);
        }
        return array;
    }
    public static  byte[] concat(byte[] a, byte[] b) {

        byte[] c= new byte[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
    public static byte[] charToByte(char c, boolean big) {
        byte[] b = new byte[2];
        if (big) {
            b[0] = (byte) ((c & 0xFF00) >> 8);
            b[1] = (byte) (c & 0xFF);
        } else {
            b[1] = (byte) ((c & 0xFF00) >> 8);
            b[0] = (byte) (c & 0xFF);
        }
        return b;
    }
    public void run(String ip, String portStr) throws IOException {

//
        try {
            Socket socket = new Socket(InetAddress.getByName(ip),Integer.parseInt(portStr));
            socket.setKeepAlive(true);
            socket.setSoTimeout(Integer.MAX_VALUE);
            OutputStream outputStream = socket.getOutputStream();
            String rawData = "INVITE sip:122227113000000002@172.0.52.2:17100;MSG_TYPE=MSG_START_VIDEO_REQ SIP/2.0\r\n" +
                    "Via: SIP/2.0/TCP 172.0.52.1:7100;branch=z9hG4bK737130459,SIP/2.0/TCP 41.216.64.105:44548;cgid=100;rport=44548;branch=z9hG4bK2044832781,SIP/2.0/TCP 41.216.64.105:6666;rport=4644;branch=z9hG4bK12345,SIP/2.0/TCP 41.213.51.119:52102;received=41.213.51.119;branch=z9hG4bK12345\r\n" +
                    "From: <sip:122225990822085040@172.0.52.1:7100>;tag=-1666293258\r\n" +
                    "To: <sip:122227000100000970@122227:0;RouteID=3100100000>\r\n" +
                    "Call-ID: 561C9DAB-100A-4be3-98A3-0F4718015F3C\r\n" +
                    "CSeq: 4 INVITE\r\n" +
                    "Contact: <sip:122227101000000002@172.0.52.1:7100>\r\n" +
                    "Max-Forwards: 68\r\n" +
                    "User-Agent: CU\r\n" +
                    "Req-acc: 热门名@wz\r\n" +
                    "Src_pt: 122227105000000002\r\n" +
                    "Content-Type: text/xml\r\n" +
                    "Content-Length: 0\r\n" +
                    "\r\n#";


            byte[] rawDataByte = rawData.getBytes("gbk");
            int length = rawDataByte.length;

            char[] oldBytes = new char[]{0,0,0,0,0,0};

            byte[] newBytes = new byte[]{0,0,0,0,0,0};
            byte[] lengthByte = intToBytes((long) length , 0, false);
            //往新包头里加入数据长度参数
            byte[] charToByte = charToByte(oldBytes[0], false);
            byte[] charToByte5 = charToByte(oldBytes[5], false);
            newBytes[0] = charToByte[0];
            newBytes[5] = charToByte5[0];
            for (int i = 0; i < lengthByte.length; i++) {
                newBytes[i+1] = lengthByte[i];
            }
            log.debug("contentLength:{};", length);
            log.debug("rawData:{}|newHead:{}", rawDataByte.length, Arrays.toString(newBytes));

            rawDataByte = concat(newBytes, rawDataByte);

            for (int i = 0; i < 4; i++) {

                outputStream.write(rawDataByte);
                outputStream.flush();
            }


            System.in.read();

            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int containEOF(String bytes,String stopWords){
        if (bytes.contains(stopWords)){
            int index = bytes.indexOf(stopWords) + stopWords.length() - 1;
            return index;
        }
        return -1;
    }

    /**
     * 将字符串写入文件中
     *
     * @param str      字符串
     * @param path     目录
     * @param fileName 文件名
     */
    public static Boolean writeStringToFile(String str, String path, String fileName) {
        BufferedWriter writer = null;
        try {
            File file = new File(path, fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
            writer.write(str);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                    ;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}