//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.javax.sip.stack;

import gov.nist.core.HostPort;
import gov.nist.core.ThreadAuditor.ThreadHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
@SuppressWarnings("AlibabaAvoidUseTimer")
public class UDPMessageProcessor extends MessageProcessor {

    private static Logger log = LoggerFactory.getLogger(UDPMessageProcessor.class);


    private static final int HIGHWAT = 100;
    private static final int LOWAT = 50;
    private int port;
    protected LinkedList messageQueue;
    protected LinkedList messageChannels;
    protected int threadPoolSize;
    protected static final int MAX_DATAGRAM_SIZE = 8192;
    protected gov.nist.javax.sip.stack.SIPTransactionStack sipStack;
    protected DatagramSocket sock;
    protected boolean isRunning;

    protected UDPMessageProcessor(InetAddress var1, gov.nist.javax.sip.stack.SIPTransactionStack var2, int var3) throws IOException {
        super(var1, var3, "udp");
        this.sipStack = var2;
        this.messageQueue = new LinkedList();
        this.port = var3;

        try {
            this.sock = var2.getNetworkLayer().createDatagramSocket(var3, var1);
            this.sock.setReceiveBufferSize(8192);
            if (var2.getThreadAuditor().isEnabled()) {
                //this.sock.setSoTimeout((int)var2.getThreadAuditor().getPingIntervalInMillisecs());
            }

            if ("0.0.0.0".equals(var1.getHostAddress()) || "::0".equals(var1.getHostAddress())) {
                super.setIpAddress(this.sock.getLocalAddress());
            }

        } catch (SocketException var5) {
            throw new IOException(var5.getMessage());
        }
    }

    public int getPort() {
        return this.port;
    }

    public void start() throws IOException {
        this.isRunning = true;
        Thread var1 = new Thread(this);
        var1.setDaemon(true);
        var1.setName("UDPMessageProcessorThread");
        var1.start();
    }

    public void run() {
        this.messageChannels = new LinkedList();
        if (this.sipStack.threadPoolSize != -1) {
            for(int var1 = 0; var1 < this.sipStack.threadPoolSize; ++var1) {
                UDPMessageChannel var2 = new UDPMessageChannel(this.sipStack, this);
                this.messageChannels.add(var2);
            }
        }

        ThreadHandle threadHandle = this.sipStack.getThreadAuditor().addCurrentThread();

//        while(this.isRunning) {
        while(true) {
            try {
                threadHandle.ping();
                int bufferSize = this.sock.getReceiveBufferSize();
                byte[] bytes = new byte[bufferSize];
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bufferSize);
                this.sock.receive(datagramPacket);
                if (this.messageQueue.size() >= 100) {
//                    if (this.sipStack.logWriter.isLoggingEnabled()) {
//                        this.sipStack.logWriter.logDebug("Dropping message -- queue length exceeded");
//                    }
                    log.error("Dropping message -- queue length exceeded");
                } else {
                    if (this.messageQueue.size() > 50 && this.messageQueue.size() < 100) {
                        float var5 = (float)(this.messageQueue.size() - 50) / 50.0F;
                        boolean var6 = Math.random() > 1.0D - (double)var5;
                        if (var6) {
                            log.error("Dropping message with probability \t" + (1.0D - (double)var5));
                            continue;
                        }
                    }

                    if (this.sipStack.threadPoolSize != -1) {
                        synchronized(this.messageQueue) {
                            this.messageQueue.addLast(datagramPacket);
                            this.messageQueue.notify();
                        }
                    } else {
                        new UDPMessageChannel(this.sipStack, this, datagramPacket);
                    }
                }
            } catch (SocketTimeoutException e) {
                log.error("ERROR", "Error found: ", e);
            } catch (SocketException e) {
                log.error("UDPMessageProcessor: Stopping");
                log.error("ERROR", "Error found: ", e);
            } catch (IOException e) {
                log.error("UDPMessageProcessor: Got an IO Exception");
                log.error("ERROR", "Error found: ", e);
            } catch (Exception e) {
                log.error("UDPMessageProcessor: Unexpected Exception - quitting");
                log.error("ERROR", "Error found: ", e);
            }
        }

    }

    public void stop() {
        synchronized(this.messageQueue) {
            this.isRunning = false;
            this.messageQueue.notifyAll();
            this.sock.close();
        }
    }

    public String getTransport() {
        return "udp";
    }

    public SIPTransactionStack getSIPStack() {
        return this.sipStack;
    }

    public MessageChannel createMessageChannel(HostPort var1) throws UnknownHostException {
        return new UDPMessageChannel(var1.getInetAddress(), var1.getPort(), this.sipStack, this);
    }

    public MessageChannel createMessageChannel(InetAddress var1, int var2) throws IOException {
        return new UDPMessageChannel(var1, var2, this.sipStack, this);
    }

    public int getDefaultTargetPort() {
        return 5060;
    }

    public boolean isSecure() {
        return false;
    }

    public int getMaximumMessageSize() {
        return 8192;
    }

    public boolean inUse() {
        synchronized(this.messageQueue) {
            return this.messageQueue.size() != 0;
        }
    }
}
