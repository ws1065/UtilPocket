//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.javax.sip.stack;

import gov.nist.core.HostPort;
import gov.nist.core.InternalErrorHandler;
import gov.nist.javax.sip.stack.MessageChannel;
import gov.nist.javax.sip.stack.MessageProcessor;
import gov.nist.javax.sip.stack.SIPTransactionStack;
import gov.nist.javax.sip.stack.TCPMessageChannel;

import java.io.IOException;
import java.net.*;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

//接受数据的位置
@SuppressWarnings("AlibabaAvoidUseTimer")
public class TCPMessageProcessor extends MessageProcessor {
    protected int nConnections;
    private boolean isRunning;
    private Hashtable tcpMessageChannels;
    private ServerSocket sock;
    protected int useCount;
    protected gov.nist.javax.sip.stack.SIPTransactionStack sipStack;

    protected TCPMessageProcessor(InetAddress var1, gov.nist.javax.sip.stack.SIPTransactionStack var2, int var3) {
        super(var1, var3, "tcp");
        this.sipStack = var2;
        this.tcpMessageChannels = new Hashtable();
    }

    public void start() throws IOException {
        Thread myThread = new Thread(this);
        myThread.setName("TCPMessageProcessorThread");
        myThread.setDaemon(true);
        this.sock = this.sipStack.getNetworkLayer().createServerSocket(this.getPort(), 0, this.getIpAddress());
        this.sipStack.setMaxConnections(100000);
        if ("0.0.0.0".equals(this.getIpAddress().getHostAddress()) || "::0".equals(this.getIpAddress().getHostAddress())) {
            super.setIpAddress(this.sock.getInetAddress());
        }

        this.isRunning = true;
        myThread.start();
    }

    public void run() {
        while(this.isRunning) {
            try {
                synchronized(this) {
                    while(true) {
                        if (this.sipStack.maxConnections != -1 && this.nConnections >= this.sipStack.maxConnections) {
                            label45: {
                                try {
                                    this.wait();
                                    if (this.isRunning) {
                                        continue;
                                    }
                                } catch (InterruptedException var4) {
                                    break label45;
                                }
                                return;
                            }
                        }
                        ++this.nConnections;
                        break;
                    }
                }

                Socket socket = this.sock.accept();
                if (this.sipStack.isLoggingEnabled()) {
                    this.getSIPStack().logWriter.logDebug("Accepting new connection!");
                }

                new TCPMessageChannel(socket, this.sipStack, this);
            } catch (SocketException var6) {
                this.isRunning = false;
            } catch (IOException var7) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.getSIPStack().logWriter.logException(var7);
                }
            } catch (Exception var8) {
                InternalErrorHandler.handleException(var8);
            }
        }

    }

    public String getTransport() {
        return "tcp";
    }

    public SIPTransactionStack getSIPStack() {
        return this.sipStack;
    }

    public synchronized void stop() {
        this.isRunning = false;

        try {
            this.sock.close();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        Collection var1 = this.tcpMessageChannels.values();
        Iterator var2 = var1.iterator();

        while(var2.hasNext()) {
            TCPMessageChannel var3 = (TCPMessageChannel)var2.next();
            var3.close();
        }

        this.notify();
    }

    protected synchronized void remove(TCPMessageChannel var1) {
        String var2 = var1.getKey();
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.logWriter.logDebug(Thread.currentThread() + " removing " + var2);
        }

        if (this.tcpMessageChannels.get(var2) == var1) {
            this.tcpMessageChannels.remove(var2);
        }

    }

    public synchronized MessageChannel createMessageChannel(HostPort var1) throws IOException {
        String var2 = MessageChannel.getKey(var1, "TCP");
        if (this.tcpMessageChannels.get(var2) != null) {
            return (TCPMessageChannel)this.tcpMessageChannels.get(var2);
        } else {
            TCPMessageChannel var3 = new TCPMessageChannel(var1.getInetAddress(), var1.getPort(), this.sipStack, this);
            this.tcpMessageChannels.put(var2, var3);
            var3.isCached = true;
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.logWriter.logDebug("key " + var2);
                this.sipStack.logWriter.logDebug("Creating " + var3);
            }

            return var3;
        }
    }

    protected synchronized void cacheMessageChannel(TCPMessageChannel var1) {
        String var2 = var1.getKey();
        TCPMessageChannel var3 = (TCPMessageChannel)this.tcpMessageChannels.get(var2);
        if (var3 != null) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.logWriter.logDebug("Closing " + var2);
            }

            var3.close();
        }

        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.logWriter.logDebug("Caching " + var2);
        }

        this.tcpMessageChannels.put(var2, var1);
    }

    public synchronized MessageChannel createMessageChannel(InetAddress var1, int var2) throws IOException {
        try {
            String var3 = MessageChannel.getKey(var1, var2, "TCP");
            if (this.tcpMessageChannels.get(var3) != null) {
                return (TCPMessageChannel)this.tcpMessageChannels.get(var3);
            } else {
                TCPMessageChannel var4 = new TCPMessageChannel(var1, var2, this.sipStack, this);
                this.tcpMessageChannels.put(var3, var4);
                var4.isCached = true;
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getLogWriter().logDebug("key " + var3);
                    this.sipStack.getLogWriter().logDebug("Creating " + var4);
                }

                return var4;
            }
        } catch (UnknownHostException var5) {
            throw new IOException(var5.getMessage());
        }
    }

    public int getMaximumMessageSize() {
        return 2147483647;
    }

    public boolean inUse() {
        return this.useCount != 0;
    }

    public int getDefaultTargetPort() {
        return 5060;
    }

    public boolean isSecure() {
        return false;
    }
}
