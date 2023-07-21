//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.javax.sip.parser;

import gov.nist.javax.sip.stack.SIPStackTimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("AlibabaAvoidUseTimer")
public class Pipeline extends InputStream {
    private LinkedList buffList;
    private Buffer currentBuffer;
    private boolean isClosed;
    private Timer timer;
    private InputStream pipe;
    private int readTimeout;
    private TimerTask myTimerTask;
    private static Logger log = LoggerFactory.getLogger(Pipeline.class);

    public void startTimer() {
        if (this.readTimeout != -1) {
            this.myTimerTask = new MyTimer(this);
            this.timer.schedule(this.myTimerTask, (long)this.readTimeout);
        }
    }

    public void stopTimer() {
        if (this.readTimeout != -1) {
            if (this.myTimerTask != null) {
                this.myTimerTask.cancel();
            }

        }
    }

    public Pipeline(InputStream var1, int var2, Timer var3) {
        this.timer = var3;
        this.pipe = var1;
        this.buffList = new LinkedList();
        this.readTimeout = var2;
    }

    public void write(byte[] var1, int var2, int var3) throws IOException {
        if (this.isClosed) {
            throw new IOException("Closed!!");
        } else {
            Buffer var4 = new Buffer(var1, var3);
            var4.ptr = var2;
            synchronized(this.buffList) {
                this.buffList.add(var4);
                this.buffList.notifyAll();
            }
        }
    }

    public void write(byte[] var1) throws IOException {
        if (this.isClosed) {
            throw new IOException("Closed!!");
        } else {
            Buffer var2 = new Buffer(var1, var1.length);
            synchronized(this.buffList) {
                this.buffList.add(var2);
                this.buffList.notifyAll();
            }
        }
    }

    public void close() {
        this.isClosed = true;
        synchronized(this.buffList) {
            this.buffList.notifyAll();
        }
    }

    public int read() throws IOException {
        synchronized(this.buffList) {
            int i;
            if (this.currentBuffer != null &&
                    this.currentBuffer.ptr < this.currentBuffer.length) {
                i = this.currentBuffer.getNextByte();
                if (this.currentBuffer.ptr == this.currentBuffer.length) {
                    this.currentBuffer = null;
                }

                return i;
            } else if (this.isClosed && this.buffList.isEmpty()) {
                return -1;
            } else {
                while(true) {
                    byte var10000;
                    try {
                        if (!this.buffList.isEmpty()) {
                            this.currentBuffer = (Buffer)this.buffList.removeFirst();
                            i = this.currentBuffer.getNextByte();
                            if (this.currentBuffer.ptr == this.currentBuffer.length) {
                                this.currentBuffer = null;
                            }
                            return i;
                        }

                        this.buffList.wait();
                        if (!this.isClosed) {
                            continue;
                        }

                        var10000 = -1;
                    } catch (InterruptedException var4) {
                        throw new IOException(var4.getMessage());
                    } catch (NoSuchElementException var5) {
                        var5.printStackTrace();
                        throw new IOException(var5.getMessage());
                    }

                    return var10000;
                }
            }
        }
    }

    class Buffer {
        byte[] bytes;
        int length;
        int ptr = 0;

        public Buffer(byte[] var2, int var3) {
            this.length = var3;
            this.bytes = var2;
        }

        public int getNextByte() {
            int var1 = this.bytes[this.ptr++] & 255;
            return var1;
        }
    }

    class MyTimer extends SIPStackTimerTask {
        Pipeline pipeline;
        private boolean isCancelled;

        protected MyTimer(Pipeline var2) {
            this.pipeline = var2;
        }

        protected void runTask() {
            if (!this.isCancelled) {
                this.pipeline.close();

                try {
                    this.pipeline.pipe.close();
                } catch (IOException var2) {
                }

            }
        }

        public boolean cancel() {
            boolean var1 = super.cancel();
            this.isCancelled = true;
            return var1;
        }
    }
}
