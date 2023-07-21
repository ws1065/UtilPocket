package com.sailing.udp;

public class UDPSocket {

    static{
        try{
            System.load("/opt/udp/libudpsocket.so");
        }catch(UnsatisfiedLinkError e){
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }
    /**
     * 开启client端进行socket监听，返回值大于0成功为发送数据句柄，小于等于0返回失败
     * @param ipStr server端IP地址字符串，形如“127.0.0.1”
     * @param port server端UDP监听端口
     * @param sleepUs 休眠微秒数
     */
    public static native int runClient(String ipStr,int port,int sleepUs);//int run_client(char *ip_str, int port, int sleep_us);

    /**
     * client端发送数据，返回值大于0成功为实际成功发送数据报文长度，返回值小于等于0为失败
     * @param handle 句炳
     * @param buf 发送数据buff
     * @param dataLen 发送数据buff长度
     */
    public static native int sendData(int handle, byte[] buf,int dataLen);//int send_data(int handle, char*buf, int dataLen);

    /**
     * 关闭client端socket，和run_client()配套使用
     * @param handle run_client返回的句柄
     */
    public static native void closeClient(int handle);//void close_client(int handle);

    /**
     * 开启server端进行socket监听，返回值大于0为成功
     * @param port server端UDP监听端口，建议不要使用知名端口
     * @param maxSize server端socket接收报文的最大缓冲区长度，client端发送报文长度一定要小于此长度
     * @param queneSize 缓冲队列长度
     */
    public static native int runServer(int port, int maxSize,int queneSize);//int run_server(int port, int rcvBufMaxSize, int queneSize);

    /**
     * 关闭server端socket，和run_server()配套使用
     * @param handle run_server返回的句柄
     */
    public static native void closeServer(int handle);//void close_server(int handle)

    public static native int respClient
            (int handle, int ip, int port, byte[] jBuffer, int dataLen);

    /*
     * Class:	  com_sailing_udp_UDPSocket
     * Method:	  getnetip
     * Signature: (I)V

    JNIEXPORT jint JNICALL Java_com_sailing_udp_UDPSocket_getnetip
            (JNIEnv *env, jclass cls, jbyteArray jBuffer);
    */
    public static native int getnetip (String jstr);

    /*
     * Class:	  com_sailing_udp_UDPSocket
     * Method:	  getnetport
     * Signature: (I)V

    JNIEXPORT jint JNICALL Java_com_sailing_udp_UDPSocket_getnetport
            (JNIEnv *env, jclass cls, jint port);
    */
    public static native int getnetport (int port);


    /*
     * Class:	  com_sailing_udp_UDPSocket
     * Method:	  gethostip
     * Signature: (I)V
    JNIEXPORT jstring JNICALL Java_com_sailing_udp_UDPSocket_gethostip
            (JNIEnv *env, jclass cls, jint ip);
    */
    public static native String gethostip (int port);
    /*
     * Class:	  com_sailing_udp_UDPSocket
     * Method:	  gethostport
     * Signature: (I)V

    JNIEXPORT jint JNICALL Java_com_sailing_udp_UDPSocket_gethostport
            (JNIEnv *env, jclass cls, jint port);
     */
    public static native int gethostport (int port);
}
