//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.javax.sip.stack;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import gov.nist.core.Host;
import gov.nist.core.HostPort;
import gov.nist.core.LogWriter;
import gov.nist.core.ThreadAuditor;
import gov.nist.core.ThreadAuditor.ThreadHandle;
import gov.nist.core.net.AddressResolver;
import gov.nist.core.net.DefaultNetworkLayer;
import gov.nist.core.net.NetworkLayer;
import gov.nist.javax.sip.DefaultAddressResolver;
import gov.nist.javax.sip.ListeningPointImpl;
import gov.nist.javax.sip.LogRecordFactory;
import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.header.Contact;
import gov.nist.javax.sip.header.Event;
import gov.nist.javax.sip.header.Server;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;

import javax.sip.*;
import javax.sip.address.Address;
import javax.sip.address.Hop;
import javax.sip.address.Router;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
@SuppressWarnings("AlibabaAvoidUseTimer")
public abstract class SIPTransactionStack implements SIPTransactionEventListener {
    public static final int BASE_TIMER_INTERVAL = 500;
    public static final int CONNECTION_LINGER_TIME = 8;
    protected ConcurrentHashMap retransmissionAlertTransactions;
    protected ConcurrentHashMap dialogTable;
    protected HashSet dialogCreatingMethods;
    protected Timer timer;
    private ConcurrentHashMap pendingTransactions;
    private ConcurrentHashMap clientTransactionTable;
    private boolean unlimitedServerTransactionTableSize;
    protected boolean unlimitedClientTransactionTableSize;
    protected int serverTransactionTableHighwaterMark;
    protected int serverTransactionTableLowaterMark;
    protected int clientTransactionTableHiwaterMark;
    protected int clientTransactionTableLowaterMark;
    private int activeClientTransactionCount;
    private ConcurrentHashMap serverTransactionTable;
    private ConcurrentHashMap mergeTable;
    protected LogWriter logWriter;
    protected ServerLog serverLog;
    boolean udpFlag;
    protected DefaultRouter defaultRouter;
    protected boolean needsLogging;
    private boolean non2XXAckPassedToListener;
    protected IOHandler ioHandler;
    protected boolean toExit;
    protected String stackName;
    protected String stackAddress;
    protected InetAddress stackInetAddress;
    protected StackMessageFactory sipMessageFactory;
    protected Router router;
    protected int threadPoolSize;
    protected int maxConnections;
    protected boolean cacheServerConnections;
    protected boolean cacheClientConnections;
    protected boolean useRouterForAll;
    protected int maxContentLength;
    protected int maxMessageSize;
    private Collection messageProcessors;
    protected int readTimeout;
    protected NetworkLayer networkLayer;
    protected String outboundProxy;
    protected String routerPath;
    protected boolean isAutomaticDialogSupportEnabled;
    protected HashSet forkedEvents;
    protected boolean generateTimeStampHeader;
    protected AddressResolver addressResolver;
    protected int maxListenerResponseTime;
    protected boolean useTlsAccelerator;
    protected ThreadAuditor threadAuditor;
    protected LogRecordFactory logRecordFactory;

    protected SIPTransactionStack() {
        this.unlimitedServerTransactionTableSize = false;
        this.unlimitedClientTransactionTableSize = true;
        this.serverTransactionTableHighwaterMark = 5000;
        this.serverTransactionTableLowaterMark = 4000;
        this.clientTransactionTableHiwaterMark = 1000;
        this.clientTransactionTableLowaterMark = 800;
        this.threadAuditor = new ThreadAuditor();
        this.toExit = false;
        this.forkedEvents = new HashSet();
        this.threadPoolSize = -1;
        this.cacheServerConnections = true;
        this.cacheClientConnections = true;
        this.maxConnections = -1;
        this.messageProcessors = new ArrayList();
        this.ioHandler = new IOHandler(this);
        this.readTimeout = -1;
        this.maxListenerResponseTime = -1;
        this.dialogCreatingMethods = new HashSet();
        this.dialogCreatingMethods.add("REFER");
        this.dialogCreatingMethods.add("INVITE");
        this.dialogCreatingMethods.add("SUBSCRIBE");
        this.addressResolver = new DefaultAddressResolver();
        this.dialogTable = new ConcurrentHashMap();
        this.clientTransactionTable = new ConcurrentHashMap();
        this.serverTransactionTable = new ConcurrentHashMap();
        this.mergeTable = new ConcurrentHashMap();
        this.retransmissionAlertTransactions = new ConcurrentHashMap();
        this.timer = new Timer();
        this.pendingTransactions = new ConcurrentHashMap();
        if(this.getThreadAuditor().isEnabled()) {
            this.timer.schedule(new PingTimer((ThreadHandle)null), 0L);
        }

    }

    protected void reInit() {
        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("Re-initializing !");
        }

        this.messageProcessors = new ArrayList();
        this.ioHandler = new IOHandler(this);
        this.pendingTransactions = new ConcurrentHashMap();
        this.clientTransactionTable = new ConcurrentHashMap();
        this.serverTransactionTable = new ConcurrentHashMap();
        this.retransmissionAlertTransactions = new ConcurrentHashMap();
        this.mergeTable = new ConcurrentHashMap();
        this.dialogTable = new ConcurrentHashMap();
        this.timer = new Timer();
    }

    public void disableLogging() {
        this.getLogWriter().disableLogging();
    }

    public void enableLogging() {
        this.getLogWriter().enableLogging();
    }

    public void printDialogTable() {
        if(this.getLogWriter().isLoggingEnabled()) {
            this.getLogWriter().logDebug("dialog table  = " + this.dialogTable);
            System.out.println("dialog table = " + this.dialogTable);
        }

    }

    public SIPServerTransaction getRetransmissionAlertTransaction(String var1) {
        return (SIPServerTransaction)this.retransmissionAlertTransactions.get(var1);
    }

    public boolean isDialogCreated(String var1) {
        boolean var2 = this.dialogCreatingMethods.contains(var1);
        if(this.isLoggingEnabled()) {
            this.getLogWriter().logDebug("isDialogCreated : " + var1 + " returning " + var2);
        }

        return var2;
    }

    public void addExtensionMethod(String var1) {
        if("NOTIFY".equals(var1)) {
            if(this.logWriter.isLoggingEnabled()) {
                this.logWriter.logDebug("NOTIFY Supported Natively");
            }
        } else {
            this.dialogCreatingMethods.add(var1.trim().toUpperCase());
        }

    }

    public void putDialog(SIPDialog var1) {
        String var2 = var1.getDialogId();
        if(this.dialogTable.containsKey(var2)) {
            if(this.logWriter.isLoggingEnabled()) {
                this.logWriter.logDebug("putDialog: dialog already exists" + var2 + " in table = " + this.dialogTable.get(var2));
            }

        } else {
            if(this.logWriter.isLoggingEnabled()) {
                this.logWriter.logDebug("putDialog dialogId=" + var2 + " dialog = " + var1);
            }

            var1.setStack(this);
            if(this.logWriter.isLoggingEnabled()) {
                this.logWriter.logStackTrace();
            }

            this.dialogTable.put(var2, var1);
        }
    }

    public SIPDialog createDialog(SIPTransaction var1) {
        SIPDialog var2 = new SIPDialog(var1);
        return var2;
    }

    public Iterator getDialogs() {
        return this.dialogTable.values().iterator();
    }

    public void removeDialog(SIPDialog var1) {
        String var2 = var1.getDialogId();
        if(var2 != null) {
            Object var3 = this.dialogTable.remove(var2);
            if(var3 != null && !var1.testAndSetIsDialogTerminatedEventDelivered()) {
                DialogTerminatedEvent var4 = new DialogTerminatedEvent(var1.getSipProvider(), var1);
                var1.getSipProvider().handleEvent(var4, (SIPTransaction)null);
            }
        }

    }

    public SIPDialog getDialog(String var1) {
        SIPDialog var2 = (SIPDialog)this.dialogTable.get(var1);
        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("getDialog(" + var1 + ") : returning " + var2);
        }

        return var2;
    }

    public SIPClientTransaction findSubscribeTransaction(SIPRequest var1, ListeningPointImpl var2) {
        SIPClientTransaction var3 = null;

        try {
            Iterator var4 = this.clientTransactionTable.values().iterator();
            this.logWriter.logDebug("ct table size = " + this.clientTransactionTable.size());
            String var5 = var1.getTo().getTag();
            if(var5 == null) {
                SIPClientTransaction var21 = var3;
                return var21;
            } else {
                Event var6 = (Event)var1.getHeader("Event");
                SIPClientTransaction var7;
                if(var6 == null) {
                    if(this.logWriter.isLoggingEnabled()) {
                        this.logWriter.logDebug("event Header is null -- returning null");
                    }

                    var7 = var3;
                    return var7;
                } else {
                    while(var4.hasNext()) {
                        var7 = (SIPClientTransaction)var4.next();
                        if("SUBSCRIBE".equals(var7.getMethod())) {
                            SIPRequest var8 = var7.getOriginalRequest();
                            Contact var9 = var8.getContactHeader();
                            Address var10 = var9.getAddress();
                            SipURI var11 = (SipURI)var10.getURI();
                            String var12 = var11.getHost();
                            int var13 = var11.getPort();
                            String var14 = var11.getTransportParam();
                            if(var14 == null) {
                                var14 = "udp";
                            }

                            if(var13 == -1) {
                                if(!"udp".equals(var14) && !"tcp".equals(var14)) {
                                    var13 = 5061;
                                } else {
                                    var13 = 5060;
                                }
                            }

                            String var15 = var7.from.getTag();
                            Event var16 = var7.event;
                            if(var16 != null) {
                                if(this.isLoggingEnabled()) {
                                    this.logWriter.logDebug("ct.fromTag = " + var15);
                                    this.logWriter.logDebug("thisToTag = " + var5);
                                    this.logWriter.logDebug("hisEvent = " + var16);
                                    this.logWriter.logDebug("eventHdr " + var6);
                                }

                                if(var2.getPort() == var13 && var2.getIPAddress().equals(var12) && var15.equalsIgnoreCase(var5) && var16 != null && var6.match(var16) && var1.getCallId().getCallId().equalsIgnoreCase(var7.callId.getCallId())) {
                                    if(var7.acquireSem()) {
                                        var3 = var7;
                                    }

                                    SIPClientTransaction var17 = var3;
                                    return var17;
                                }
                            }
                        }
                    }

                    var7 = var3;
                    return var7;
                }
            }
        } finally {
            if(this.isLoggingEnabled()) {
                this.logWriter.logDebug("findSubscribeTransaction : returning " + var3);
            }

        }
    }

//    public void removeTransaction(SIPMessage var1){
//        SIPTransaction var3 = null;
//        Via var4;
//        String var5;
//        var4 = var1.getTopmostVia();
//        if(var4.getBranch() != null) {
//            var5 = var1.getTransactionId();
//            var3 = (SIPTransaction)this.serverTransactionTable.get(var5);
//            if(this.logWriter.isLoggingEnabled()) {
//                this.getLogWriter().logDebug("serverTx: looking for key " + var5 + " existing=" + this.serverTransactionTable);
//            }
//
//            if(var5.startsWith("z9hg4bk")) {
//                this.serverTransactionTable.remove(var3);
//            }
//        }
//    }
    public SIPTransaction findTransaction(SIPMessage var1, boolean var2) {
        SIPTransaction var3 = null;
        Via var4;
        String var5;
        Iterator var7;
        if(var2) {
            var4 = var1.getTopmostVia();
            if(var4.getBranch() != null) {
                var5 = var1.getTransactionId();
                var3 = (SIPTransaction)this.serverTransactionTable.get(var5);
                if(this.logWriter.isLoggingEnabled()) {
                    this.getLogWriter().logDebug("serverTx: looking for key " + var5 + " existing=" + this.serverTransactionTable);
                }

                if(var5.startsWith("z9hg4bk")) {
                    return var3;
                }
            }

            var7 = this.serverTransactionTable.values().iterator();

            while(var7.hasNext()) {
                SIPServerTransaction var6 = (SIPServerTransaction)var7.next();
                if(var6.isMessagePartOfTransaction(var1)) {
                    return var6;
                }
            }
        } else {
            var4 = var1.getTopmostVia();
            if(var4.getBranch() != null) {
                var5 = var1.getTransactionId();
                if(this.logWriter.isLoggingEnabled()) {
                    this.getLogWriter().logDebug("clientTx: looking for key " + var5);
                }

                var3 = (SIPTransaction)this.clientTransactionTable.get(var5);
                if(var5.startsWith("z9hg4bk")) {
                    return var3;
                }
            }

            var7 = this.clientTransactionTable.values().iterator();

            while(var7.hasNext()) {
                SIPClientTransaction var8 = (SIPClientTransaction)var7.next();
                if(var8.isMessagePartOfTransaction(var1)) {
                    return var8;
                }
            }
        }

        return null;
    }

    public SIPTransaction findCancelTransaction(SIPRequest var1, boolean var2) {
        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("findCancelTransaction request= \n" + var1 + "\nfindCancelRequest isServer=" + var2);
        }

        Iterator var3;
        SIPTransaction var4;
        if(var2) {
            var3 = this.serverTransactionTable.values().iterator();

            while(var3.hasNext()) {
                var4 = (SIPTransaction)var3.next();
                SIPServerTransaction var5 = (SIPServerTransaction)var4;
                if(var5.doesCancelMatchTransaction(var1)) {
                    return var5;
                }
            }
        } else {
            var3 = this.clientTransactionTable.values().iterator();

            while(var3.hasNext()) {
                var4 = (SIPTransaction)var3.next();
                SIPClientTransaction var6 = (SIPClientTransaction)var4;
                if(var6.doesCancelMatchTransaction(var1)) {
                    return var6;
                }
            }
        }

        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("Could not find transaction for cancel request");
        }

        return null;
    }

    protected SIPTransactionStack(StackMessageFactory var1) {
        this();
        this.sipMessageFactory = var1;
    }

    public SIPServerTransaction findPendingTransaction(SIPRequest var1) {
        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("looking for pending tx for :" + var1.getTransactionId());
        }

        return (SIPServerTransaction)this.pendingTransactions.get(var1.getTransactionId());
    }

    public SIPServerTransaction findMergedTransaction(SIPRequest var1) {
        if(!this.isDialogCreated(var1.getMethod())) {
            return null;
        } else {
            String var2 = var1.getMergeId();
            return var2 != null?(SIPServerTransaction)this.mergeTable.get(var2):null;
        }
    }

    public void removePendingTransaction(SIPServerTransaction var1) {
        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("removePendingTx: " + var1.getTransactionId());
        }

        this.pendingTransactions.remove(var1.getTransactionId());
    }

    public void removeFromMergeTable(SIPServerTransaction var1) {
        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("Removing tx from merge table ");
        }

        String var2 = ((SIPRequest)var1.getRequest()).getMergeId();
        if(var2 != null) {
            this.mergeTable.remove(var2);
        }

    }

    public void putInMergeTable(SIPServerTransaction var1, SIPRequest var2) {
        String var3 = var2.getMergeId();
        if(var3 != null) {
            this.mergeTable.put(var3, var1);
        }

    }

    public void mapTransaction(SIPServerTransaction var1) {
        if(!var1.isMapped) {
            this.addTransactionHash(var1);
            var1.startTransactionTimer();
            var1.isMapped = true;
        }
    }

    public ServerRequestInterface newSIPServerRequest(SIPRequest var1, MessageChannel var2) {
        String var6 = var1.getTransactionId();
        var1.setMessageChannel(var2);
        SIPServerTransaction var5 = (SIPServerTransaction)this.serverTransactionTable.get(var6);
        if(var5 == null || !var5.isMessagePartOfTransaction(var1)) {
            Iterator var3 = this.serverTransactionTable.values().iterator();
            var5 = null;
            if(!var6.toLowerCase().startsWith("z9hg4bk")) {
                while(var3.hasNext() && var5 == null) {
                    SIPServerTransaction var4 = (SIPServerTransaction)var3.next();
                    if(var4.isMessagePartOfTransaction(var1)) {
                        var5 = var4;
                    }
                }
            }

            if(var5 == null) {
                var5 = this.findPendingTransaction(var1);
                if(var5 != null) {
                    var1.setTransaction(var5);
                    if(var5 != null && var5.acquireSem()) {
                        return var5;
                    }

                    return null;
                }

                var5 = this.createServerTransaction(var2);
                if(var5 != null) {
                    var5.setOriginalRequest(var1);
                    var1.setTransaction(var5);
                }
            }
        }

        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("newSIPServerRequest( " + var1.getMethod() + ":" + var1.getTopmostVia().getBranch() + "):" + var5);
        }

        if(var5 != null) {
            var5.setRequestInterface(this.sipMessageFactory.newSIPServerRequest(var1, var5));
        }

        return var5 != null && var5.acquireSem()?var5:null;
    }

    protected ServerResponseInterface newSIPServerResponse(SIPResponse var1, MessageChannel var2) {
        String var6 = var1.getTransactionId();
        SIPClientTransaction var5 = (SIPClientTransaction)this.clientTransactionTable.get(var6);
        if(var5 == null || !var5.isMessagePartOfTransaction(var1) && !var6.startsWith("z9hg4bk")) {
            Iterator var3 = this.clientTransactionTable.values().iterator();
            var5 = null;

            while(var3.hasNext() && var5 == null) {
                SIPClientTransaction var4 = (SIPClientTransaction)var3.next();
                if(var4.isMessagePartOfTransaction(var1)) {
                    var5 = var4;
                }
            }

            if(var5 == null) {
                if(this.logWriter.isLoggingEnabled(16)) {
                    var2.logResponse(var1, System.currentTimeMillis(), "before processing");
                }

                return this.sipMessageFactory.newSIPServerResponse(var1, var2);
            }
        }

        boolean var7 = var5.acquireSem();
        if(this.logWriter.isLoggingEnabled(16)) {
            var5.logResponse(var1, System.currentTimeMillis(), "before processing");
        }

        if(var7) {
            ServerResponseInterface var8 = this.sipMessageFactory.newSIPServerResponse(var1, var5);
            if(var8 == null) {
                if(this.logWriter.isLoggingEnabled()) {
                    this.logWriter.logDebug("returning null - serverResponseInterface is null!");
                }

                var5.releaseSem();
                return null;
            }

            var5.setResponseInterface(var8);
        }

        return var7?var5:null;
    }

    public MessageChannel createMessageChannel(SIPRequest var1, MessageProcessor var2, Hop var3) throws IOException {
        Host var5 = new Host();
        var5.setHostname(var3.getHost());
        HostPort var6 = new HostPort();
        var6.setHost(var5);
        var6.setPort(var3.getPort());
        MessageChannel var7 = var2.createMessageChannel(var6);
        if(var7 == null) {
            return null;
        } else {
            SIPClientTransaction var4 = this.createClientTransaction(var1, var7);
            ((SIPClientTransaction)var4).setViaPort(var3.getPort());
            ((SIPClientTransaction)var4).setViaHost(var3.getHost());
            this.addTransactionHash(var4);
            var4.startTransactionTimer();
            return var4;
        }
    }

    public SIPClientTransaction createClientTransaction(SIPRequest var1, MessageChannel var2) {
        SIPClientTransaction var3 = new SIPClientTransaction(this, var2);
        var3.setOriginalRequest(var1);
        return var3;
    }

    public SIPServerTransaction createServerTransaction(MessageChannel var1) {
        if(!this.unlimitedServerTransactionTableSize && this.serverTransactionTable.size() >= this.serverTransactionTableLowaterMark) {
            if(this.serverTransactionTable.size() >= this.serverTransactionTableHighwaterMark) {
                return null;
            } else {
                float var2 = (float)(this.serverTransactionTable.size() - this.serverTransactionTableLowaterMark) / (float)(this.serverTransactionTableHighwaterMark - this.serverTransactionTableLowaterMark);
                boolean var3 = Math.random() > 1.0D - (double)var2;
                return var3?null:new SIPServerTransaction(this, var1);
            }
        } else {
            return new SIPServerTransaction(this, var1);
        }
    }

    public int getClientTransactionTableSize() {
        return this.clientTransactionTable.size();
    }

    public void addTransaction(SIPClientTransaction var1) {
        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("added transaction " + var1);
        }

        this.addTransactionHash(var1);
        var1.startTransactionTimer();
    }

    public void removeTransaction(SIPTransaction var1) {
        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("Removing Transaction = " + var1.getTransactionId() + " transaction = " + var1);
        }

        String var2;
        Object var3;
        if(var1 instanceof SIPServerTransaction) {
            if(this.logWriter.isLoggingEnabled()) {
                this.logWriter.logStackTrace();
            }

            var2 = var1.getTransactionId();
            var3 = this.serverTransactionTable.remove(var2);
            String var4 = var1.getMethod();
            this.removePendingTransaction((SIPServerTransaction)var1);
            if(this.isDialogCreated(var4)) {
                this.removeFromMergeTable((SIPServerTransaction)var1);
            }

            SipProviderImpl var5 = var1.getSipProvider();
            if(var3 != null && var1.testAndSetTransactionTerminatedEvent()) {
                TransactionTerminatedEvent var6 = new TransactionTerminatedEvent(var5, (ServerTransaction)var1);
                var5.handleEvent(var6, var1);
            }
        } else {
            var2 = var1.getTransactionId();
            var3 = this.clientTransactionTable.remove(var2);
            if(this.logWriter.isLoggingEnabled()) {
                this.logWriter.logDebug("REMOVED client tx " + var3 + " KEY = " + var2);
            }

            if(var3 != null && var1.testAndSetTransactionTerminatedEvent()) {
                SipProviderImpl var7 = var1.getSipProvider();
                TransactionTerminatedEvent var8 = new TransactionTerminatedEvent(var7, (ClientTransaction)var1);
                var7.handleEvent(var8, var1);
            }
        }

    }

    public void addTransaction(SIPServerTransaction var1) throws IOException {
        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("added transaction " + var1);
        }

        var1.map();
        this.addTransactionHash(var1);
        var1.startTransactionTimer();
    }

    private void addTransactionHash(SIPTransaction var1) {
        SIPRequest var2 = var1.getOriginalRequest();
        String var7;
        if(var1 instanceof SIPClientTransaction) {
            if(!this.unlimitedClientTransactionTableSize && this.activeClientTransactionCount > this.clientTransactionTableHiwaterMark) {
                try {
                    ConcurrentHashMap var3 = this.clientTransactionTable;
                    synchronized(this.clientTransactionTable) {
                        this.clientTransactionTable.wait();
                    }
                } catch (Exception var6) {
                    if(this.logWriter.isLoggingEnabled()) {
                        this.logWriter.logError("Exception occured while waiting for room", var6);
                    }
                }
            }

            ++this.activeClientTransactionCount;
            var7 = var2.getTransactionId();
            this.clientTransactionTable.put(var7, var1);
            if(this.logWriter.isLoggingEnabled()) {
                this.logWriter.logDebug(" putTransactionHash :  key = " + var7);
            }
        } else {
            var7 = var2.getTransactionId();
            if(this.logWriter.isLoggingEnabled()) {
                this.logWriter.logDebug(" putTransactionHash :  key = " + var7);
            }

            this.serverTransactionTable.put(var7, var1);
        }

    }

    protected void decrementActiveClientTransactionCount() {
        --this.activeClientTransactionCount;
        if(this.activeClientTransactionCount <= this.clientTransactionTableLowaterMark && !this.unlimitedClientTransactionTableSize) {
            ConcurrentHashMap var1 = this.clientTransactionTable;
            synchronized(this.clientTransactionTable) {
                this.clientTransactionTable.notify();
            }
        }

    }

    protected void removeTransactionHash(SIPTransaction var1) {
        SIPRequest var2 = var1.getOriginalRequest();
        if(var2 != null) {
            String var3;
            if(var1 instanceof SIPClientTransaction) {
                var3 = var1.getTransactionId();
                if(this.logWriter.isLoggingEnabled()) {
                    this.logWriter.logStackTrace();
                    this.logWriter.logDebug("removing client Tx : " + var3);
                }

                this.clientTransactionTable.remove(var3);
            } else if(var1 instanceof SIPServerTransaction) {
                var3 = var1.getTransactionId();
                this.serverTransactionTable.remove(var3);
                if(this.logWriter.isLoggingEnabled()) {
                    this.logWriter.logDebug("removing server Tx : " + var3);
                }
            }

        }
    }

    public synchronized void transactionErrorEvent(SIPTransactionErrorEvent var1) {
        SIPTransaction var2 = (SIPTransaction)var1.getSource();
        if(var1.getErrorID() == 2) {
            var2.setState(SIPTransaction.TERMINATED_STATE);
            if(var2 instanceof SIPServerTransaction) {
                ((SIPServerTransaction)var2).collectionTime = 0;
            }

            var2.disableTimeoutTimer();
            var2.disableRetransmissionTimer();
        }

    }

    public void stopStack() {
        if(this.timer != null) {
            this.timer.cancel();
        }

        this.timer = null;
        this.pendingTransactions.clear();
        this.toExit = true;
        synchronized(this) {
            this.notifyAll();
        }

        ConcurrentHashMap var1 = this.clientTransactionTable;
        synchronized(this.clientTransactionTable) {
            this.clientTransactionTable.notifyAll();
        }

        Collection var9 = this.messageProcessors;
        synchronized(this.messageProcessors) {
            MessageProcessor[] var2 = this.getMessageProcessors();
            int var3 = 0;

            while(true) {
                if(var3 >= var2.length) {
                    this.ioHandler.closeAll();
                    break;
                }

                this.removeMessageProcessor(var2[var3]);
                ++var3;
            }
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException var5) {
            ;
        }

        this.clientTransactionTable.clear();
        this.serverTransactionTable.clear();
        this.dialogTable.clear();
        this.serverLog.closeLogFile();
    }

    public void putPendingTransaction(SIPServerTransaction var1) {
        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("putPendingTransaction: " + var1);
        }

        this.pendingTransactions.put(var1.getTransactionId(), var1);
    }

    public NetworkLayer getNetworkLayer() {
        return (NetworkLayer)(this.networkLayer == null?DefaultNetworkLayer.SINGLETON:this.networkLayer);
    }

    public boolean isLoggingEnabled() {
        return this.logWriter == null?false:this.logWriter.isLoggingEnabled();
    }

    public LogWriter getLogWriter() {
        return this.logWriter;
    }

    public ServerLog getServerLog() {
        return this.serverLog;
    }

    public int getMaxMessageSize() {
        return this.maxMessageSize;
    }

    public void setSingleThreaded() {
        this.threadPoolSize = 1;
    }

    public void setThreadPoolSize(int var1) {
        this.threadPoolSize = var1;
    }

    public void setMaxConnections(int var1) {
        this.maxConnections = var1;
    }

    public Hop getNextHop(SIPRequest var1) throws SipException {
        return this.useRouterForAll?(this.router != null?this.router.getNextHop(var1):null):(!var1.getRequestURI().isSipURI() && var1.getRouteHeaders() == null?(this.router != null?this.router.getNextHop(var1):null):this.defaultRouter.getNextHop(var1));
    }

    public void setStackName(String var1) {
        this.stackName = var1;
    }

    public Server createServerHeaderForStack() {
        Server var1 = new Server();
        var1.addProductToken(this.stackName);
        return var1;
    }

    protected void setHostAddress(String var1) throws UnknownHostException {
        if(var1.indexOf(58) != var1.lastIndexOf(58) && var1.trim().charAt(0) != 91) {
            this.stackAddress = '[' + var1 + ']';
        } else {
            this.stackAddress = var1;
        }

        this.stackInetAddress = InetAddress.getByName(var1);
    }

    /** @deprecated */
    public String getHostAddress() {
        return this.stackAddress;
    }

    protected void setRouter(Router var1) {
        this.router = var1;
    }

    public Router getRouter(SIPRequest var1) {
        return (Router)(var1.getRequestLine() == null?this.defaultRouter:(this.useRouterForAll?this.router:(!"sip".equals(var1.getRequestURI().getScheme()) && !"sips".equals(var1.getRequestURI().getScheme())?(this.router != null?this.router:this.defaultRouter):this.defaultRouter)));
    }

    public Router getRouter() {
        return this.router;
    }

    public boolean isAlive() {
        return !this.toExit;
    }

    protected void addMessageProcessor(MessageProcessor var1) throws IOException {
        Collection var2 = this.messageProcessors;
        synchronized(this.messageProcessors) {
            this.messageProcessors.add(var1);
        }
    }

    protected void removeMessageProcessor(MessageProcessor var1) {
        Collection var2 = this.messageProcessors;
        synchronized(this.messageProcessors) {
            if(this.messageProcessors.remove(var1)) {
                var1.stop();
            }

        }
    }

    protected MessageProcessor[] getMessageProcessors() {
        Collection var1 = this.messageProcessors;
        synchronized(this.messageProcessors) {
            return (MessageProcessor[])((MessageProcessor[])this.messageProcessors.toArray(new MessageProcessor[0]));
        }
    }

    protected MessageProcessor createMessageProcessor(InetAddress var1, int var2, String var3) throws IOException {
        if("udp".equalsIgnoreCase(var3)) {
            UDPMessageProcessor var6 = new UDPMessageProcessor(var1, this, var2);
            this.addMessageProcessor(var6);
            this.udpFlag = true;
            return var6;
        } else if("tcp".equalsIgnoreCase(var3)) {
            TCPMessageProcessor var5 = new TCPMessageProcessor(var1, this, var2);
            this.addMessageProcessor(var5);
            return var5;
        } else if("tls".equalsIgnoreCase(var3)) {
            TLSMessageProcessor var4 = new TLSMessageProcessor(var1, this, var2);
            this.addMessageProcessor(var4);
            return var4;
        } else {
            throw new IllegalArgumentException("bad transport");
        }
    }

    protected void setMessageFactory(StackMessageFactory var1) {
        this.sipMessageFactory = var1;
    }

    public MessageChannel createRawMessageChannel(String var1, int var2, Hop var3) throws UnknownHostException {
        Host var4 = new Host();
        var4.setHostname(var3.getHost());
        HostPort var5 = new HostPort();
        var5.setHost(var4);
        var5.setPort(var3.getPort());
        MessageChannel var8 = null;
        Iterator var6 = this.messageProcessors.iterator();

        while(var6.hasNext() && var8 == null) {
            MessageProcessor var7 = (MessageProcessor)var6.next();
            if(var3.getTransport().equalsIgnoreCase(var7.getTransport()) && var1.equals(var7.getIpAddress().getHostAddress()) && var2 == var7.getPort()) {
                try {
                    var8 = var7.createMessageChannel(var5);
                } catch (UnknownHostException var10) {
                    if(this.logWriter.isLoggingEnabled()) {
                        this.logWriter.logException(var10);
                    }

                    throw var10;
                } catch (IOException var11) {
                    if(this.logWriter.isLoggingEnabled()) {
                        this.logWriter.logException(var11);
                    }
                }
            }
        }

        return var8;
    }

    public boolean isEventForked(String var1) {
        if(this.logWriter.isLoggingEnabled()) {
            this.logWriter.logDebug("isEventForked: " + var1 + " returning " + this.forkedEvents.contains(var1));
        }

        return this.forkedEvents.contains(var1);
    }

    public AddressResolver getAddressResolver() {
        return this.addressResolver;
    }

    public void setAddressResolver(AddressResolver var1) {
        this.addressResolver = var1;
    }

    public void setLogRecordFactory(LogRecordFactory var1) {
        this.logRecordFactory = var1;
    }

    public ThreadAuditor getThreadAuditor() {
        return this.threadAuditor;
    }

    public String auditStack(Set var1, long var2, long var4) {
        String var6 = null;
        String var7 = this.auditDialogs(var1, var2);
        String var8 = this.auditTransactions(this.serverTransactionTable, var4);
        String var9 = this.auditTransactions(this.clientTransactionTable, var4);
        if(var7 != null || var8 != null || var9 != null) {
            var6 = "SIP Stack Audit:\n" + (var7 != null?var7:"") + (var8 != null?var8:"") + (var9 != null?var9:"");
        }

        return var6;
    }

    private String auditDialogs(Set var1, long var2) {
        String var4 = "  Leaked dialogs:\n";
        int var5 = 0;
        long var6 = System.currentTimeMillis();
        ConcurrentHashMap var9 = this.dialogTable;
        LinkedList var8;
        synchronized(this.dialogTable) {
            var8 = new LinkedList(this.dialogTable.values());
        }

        Iterator var16 = var8.iterator();

        while(var16.hasNext()) {
            SIPDialog var10 = (SIPDialog)var16.next();
            CallIdHeader var11 = var10 != null?var10.getCallId():null;
            String var12 = var11 != null?var11.getCallId():null;
            if(var12 != null && !var1.contains(var12)) {
                if(var10.auditTag == 0L) {
                    var10.auditTag = var6;
                } else if(var6 - var10.auditTag >= var2) {
                    ++var5;
                    DialogState var13 = var10.getState();
                    String var14 = "dialog id: " + var10.getDialogId() + ", dialog state: " + (var13 != null?var13.toString():"null");
                    var4 = var4 + "    " + var14 + "\n";
                    var10.setState(3);
                    this.logWriter.logDebug("auditDialogs: leaked " + var14);
                }
            }
        }

        if(var5 > 0) {
            var4 = var4 + "    Total: " + Integer.toString(var5) + " leaked dialogs detected and removed.\n";
        } else {
            var4 = null;
        }

        return var4;
    }

    private String auditTransactions(ConcurrentHashMap var1, long var2) {
        String var4 = "  Leaked transactions:\n";
        int var5 = 0;
        long var6 = System.currentTimeMillis();
        LinkedList var8 = new LinkedList(var1.values());
        Iterator var9 = var8.iterator();

        while(var9.hasNext()) {
            SIPTransaction var10 = (SIPTransaction)var9.next();
            if(var10 != null) {
                if(var10.auditTag == 0L) {
                    var10.auditTag = var6;
                } else if(var6 - var10.auditTag >= var2) {
                    ++var5;
                    TransactionState var11 = var10.getState();
                    SIPRequest var12 = var10.getOriginalRequest();
                    String var13 = var12 != null?var12.getMethod():null;
                    String var14 = var10.getClass().getName() + ", state: " + (var11 != null?var11.toString():"null") + ", OR: " + (var13 != null?var13:"null");
                    var4 = var4 + "    " + var14 + "\n";
                    this.removeTransaction(var10);
                    this.logWriter.logDebug("auditTransactions: leaked " + var14);
                }
            }
        }

        if(var5 > 0) {
            var4 = var4 + "    Total: " + Integer.toString(var5) + " leaked transactions detected and removed.\n";
        } else {
            var4 = null;
        }

        return var4;
    }

    public void setNon2XXAckPassedToListener(boolean var1) {
        this.non2XXAckPassedToListener = var1;
    }

    public boolean isNon2XXAckPassedToListener() {
        return this.non2XXAckPassedToListener;
    }

    public int getActiveClientTransactionCount() {
        return this.activeClientTransactionCount;
    }

    class PingTimer extends SIPStackTimerTask {
        ThreadHandle threadHandle;

        public PingTimer(ThreadHandle var2) {
            this.threadHandle = var2;
        }

        protected void runTask() {
            if(SIPTransactionStack.this.timer != null) {
                if(this.threadHandle == null) {
                    this.threadHandle = SIPTransactionStack.this.getThreadAuditor().addCurrentThread();
                }

                this.threadHandle.ping();
                SIPTransactionStack.this.timer.schedule(SIPTransactionStack.this.new PingTimer(this.threadHandle), this.threadHandle.getPingIntervalInMillisecs());
            }

        }
    }
}
