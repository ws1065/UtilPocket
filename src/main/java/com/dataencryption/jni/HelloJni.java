package com.dataencryption.jni;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class HelloJni{
    public native void helloWorld();
    // 注意，这个native方法就是调用c语言接口用的
    //algType:1为sm4 cbc软算法，2为欣博sm4 cbc;keyLen和ivLen必须是16:返回值为打开的句柄，>0:成功，<6 失败
    public native int openHandle(int algType, byte[] keyBuff,int keyLen, byte[] ivBuff,int ivLen);// 关闭句柄，handle必须为使用openHandle打开的合法句柄
    public native int closeHandle(int handle);
    //加密接口inLen必须为16的整数倍，inLen==outLen，handle必须为使用openHandle打开的合法句柄
    public native int encryptData(int handle, byte[] inBuff,int inLen, byte[] outBuff,int outLen);//解密接口inLen必须为16的整数倍，inLen==outLen，handle必须为使用openHandle打开的合法句柄
    public native int decryptData(int handle, byte[] inBuff,int inLen, byte[] outBuff,int outLen);
    static {
        System.load("/opt/vcs/engine/vcsproxy/libdataencryption.so"); // 这行是调用动态链接库
    }
    public static void main(String[] args){
        new HelloJni().helloWorld();
        String data ="012345678901234567890123456789012345678901234567890123456789012!";
        String key ="0123456789abcdef";
        String iv="fedcba9876543210";
        int handle = -1;
        handle =new HelloJni().openHandle(1,key.getBytes(),16,iv.getBytes(),16);

        System.out.println("handle id:"+handle);
        ByteBuffer inbuf=ByteBuffer.allocate(64);
        ByteBuffer outbuf=ByteBuffer.allocate(64);

        byte[] dataByte = data.getBytes();
        inbuf.put(dataByte,0,64);

        byte[] sendByte = inbuf.array();
        byte[] recvByte = outbuf.array();

        System.out.println("in data:"+data);
        int result = new HelloJni().encryptData(handle, sendByte,64,recvByte,64);

        System.out.println("out data:"+result);
        String inStr= Arrays.toString(recvByte);
        System.out.println(inStr);

        result =new HelloJni().decryptData(handle, recvByte,64,sendByte,64);
        System.out.println("out data:"+result);

        String outstr=Arrays.toString(sendByte);
        System.out.println(outstr);

        new HelloJni().closeHandle(handle);
    }
}