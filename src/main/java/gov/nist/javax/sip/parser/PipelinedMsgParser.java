//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.javax.sip.parser;

import gov.nist.core.Debug;
import gov.nist.javax.sip.header.ContentLength;
import gov.nist.javax.sip.message.SIPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
//获得数据并对进行处理
@SuppressWarnings("AlibabaAvoidUseTimer")
public final class PipelinedMsgParser implements Runnable {
    protected SIPMessageListener sipMessageListener;
    private Thread mythread;
    private byte[] messageBody;
    private boolean errorFlag;
    private Pipeline rawInputStream;
    private int maxMessageSize;
    private int sizeCounter;
    private int messageSize;
    private static int uid = 0;
    private static Logger log = LoggerFactory.getLogger(PipelinedMsgParser.class);

    protected PipelinedMsgParser() {
    }

    private static synchronized int getNewUid() {
        return uid++;
    }

    public PipelinedMsgParser(SIPMessageListener var1, Pipeline var2, boolean var3, int var4) {
        this();
        this.sipMessageListener = var1;
        this.rawInputStream = var2;
        this.maxMessageSize = var4;
        this.mythread = new Thread(this);
        this.mythread.setName("PipelineThread-" + getNewUid());
    }

    public PipelinedMsgParser(SIPMessageListener var1, Pipeline var2, int var3) {
        this(var1, var2, false, var3);
    }

    public PipelinedMsgParser(Pipeline var1) {
        this((SIPMessageListener)null, var1, false, 0);
    }

    public void processInput() {
        this.mythread.start();
    }

    protected Object clone() {
        PipelinedMsgParser var1 = new PipelinedMsgParser();
        var1.rawInputStream = this.rawInputStream;
        var1.sipMessageListener = this.sipMessageListener;
        Thread var2 = new Thread(var1);
        var2.setName("PipelineThread");
        return var1;
    }

    public void setMessageListener(SIPMessageListener var1) {
        this.sipMessageListener = var1;
    }

    private String readLine(FilterInputStream var1) throws IOException {
        StringBuffer var2 = new StringBuffer("");

        while(true) {
            try {
                int var4 = var1.read();
                if (var4 == -1) {
                    throw new IOException("End of stream");
                }

                char var3 = (char)var4;
                if (this.maxMessageSize > 0) {
                    --this.sizeCounter;
                    if (this.sizeCounter <= 0) {
                        throw new IOException("Max size exceeded!");
                    }
                }

                if (var3 != '\r') {
                    var2.append(var3);
                }

                if (var3 == '\n') {
                    return var2.toString();
                }
            } catch (IOException var5) {
                throw var5;
            }
        }
    }

    public void run() {
        MyFilterInputStream var1 = null;
        var1 = new MyFilterInputStream(this.rawInputStream);

        while(true) {
            try {
                SIPMessage sipMessage;
                label360:
                while(true) {
                    this.sizeCounter = this.maxMessageSize;
                    this.messageSize = 0;
                    StringBuffer var2 = new StringBuffer();
                    String var4 = null;

                    String var3;
                    while(true) {
                        try {
                            var3 = this.readLine(var1);
                            if (!"\n".equals(var3)) {
                                break;
                            }
                        } catch (IOException var36) {
                            Debug.printStackTrace(var36);
                            this.rawInputStream.stopTimer();
                            return;
                        }
                    }

                    var2.append(var3);
                    this.rawInputStream.startTimer();

                    while(true) {
                        try {
                            var4 = this.readLine(var1);
                            var2.append(var4);
                            if ("".equals(var4.trim())) {
                                break;
                            }
                        } catch (IOException var37) {
                            this.rawInputStream.stopTimer();
                            Debug.printStackTrace(var37);
                            return;
                        }
                    }

                    this.rawInputStream.stopTimer();
                    var2.append(var4);
                    gov.nist.javax.sip.parser.StringMsgParser var5 = new StringMsgParser(this.sipMessageListener);
                    var5.readBody = false;
                    sipMessage = null;
                    try {
                        sipMessage = var5.parseSIPMessage(var2.toString());
                        log.info("处理出来的数据：{}",sipMessage);
                        if (sipMessage == null) {
                            this.rawInputStream.stopTimer();
                            continue;
                        }
                    } catch (Exception e) {
                        log.error("ERROR FOUND",e);
                        continue;
                    }
                    ContentLength var7 = (ContentLength)sipMessage.getContentLength();
                    boolean var8 = false;
                    int var42;
                    if (var7 != null) {
                        var42 = var7.getContentLength();
                    } else {
                        var42 = 0;
                    }

                    if (var42 == 0) {
                        sipMessage.removeContent();
                        break;
                    }

                    if (this.maxMessageSize != 0 && var42 >= this.sizeCounter) {
                        break;
                    }

                    byte[] var9 = new byte[var42];
                    int var10 = 0;

                    while(true) {
                        if (var10 < var42) {
                            this.rawInputStream.startTimer();

                            try {
                                int var11 = var1.read(var9, var10, var42 - var10);
                                if (var11 > 0) {
                                    var10 += var11;
                                    continue;
                                }
                            } catch (IOException var38) {
                                var38.printStackTrace();
                            } finally {
                                this.rawInputStream.stopTimer();
                            }
                        }
                        sipMessage.setMessageContent(var9);
                        break label360;
                    }
                }

                if (this.sipMessageListener == null) {
                    continue;
                }

                try {
                    this.sipMessageListener.processMessage(sipMessage);
                    continue;
                } catch (Exception var35) {
                }
            } finally {
                try {
                    var1.close();
                } catch (IOException var34) {
                    log.error("ERROR FOUND:",var34);
                }

            }

            return;
        }
    }

    class MyFilterInputStream extends FilterInputStream {
        public MyFilterInputStream(InputStream var2) {
            super(var2);
        }
    }
}
