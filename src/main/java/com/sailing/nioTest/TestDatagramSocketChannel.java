package com.sailing.nioTest;
 
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
 
public class TestDatagramSocketChannel {
	
	private int port[]={8888};
	
	private Selector selector;
	
	public TestDatagramSocketChannel() throws IOException{
		selector = Selector.open();
		for(int i=0;i<port.length;i++){
			DatagramChannel datagramChannel = DatagramChannel.open();
			datagramChannel.socket().bind(new InetSocketAddress(port[i]));
			datagramChannel.configureBlocking(false);
			datagramChannel.register(selector,SelectionKey.OP_READ);  //设置成读取操作
		}
	}
	
	public void testChannel() throws IOException{
		byte bytes[] = new byte[1024];
		int length=0;
		while(true){
			int num = selector.select();
			if(num>0){
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				if(iterator.hasNext()){
					SelectionKey selectionKey = iterator.next();
					DatagramChannel datagramChannel=null;
					if(selectionKey.isReadable()){
						datagramChannel = (DatagramChannel)selectionKey.channel();
						ByteBuffer buffer = ByteBuffer.allocate(10*1024);
						datagramChannel.receive(buffer);
						buffer.flip();  //读取准备

                        String string = "123456789";
                        byte []t= string.getBytes("UTF-8");
                        ByteBuffer write = ByteBuffer.wrap(t);
                        while(write.hasRemaining()){
                            datagramChannel.write(write);
                        }
//						int readLength = buffer.limit();
//						byte byteread[]=new byte[readLength];
//						buffer.get(byteread,0,readLength);
//						System.arraycopy(byteread, 0, bytes,length,readLength);
//						length+=readLength;
//						if(length==10){ //读取的数据达到10哥字节，设置成写出
//							datagramChannel.register(selector,SelectionKey.OP_WRITE);
//						}
					}else if(selectionKey.isWritable()){
						datagramChannel = (DatagramChannel)selectionKey.channel();
						SocketAddress socketAddress = new InetSocketAddress("127.0.0.1",9998);
						datagramChannel.connect(socketAddress);
						String string = "123456789";
						byte []t= string.getBytes("UTF-8");
						ByteBuffer write = ByteBuffer.wrap(t);
						while(write.hasRemaining()){
							datagramChannel.send(write,socketAddress);
						}
						datagramChannel.register(selector,SelectionKey.OP_READ);
					}
					iterator.remove();
				}
			}
		}
	}
	
	public static void main(String []args) throws IOException{
		new TestDatagramSocketChannel().testChannel();
	}
}