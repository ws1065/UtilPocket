//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.javax.sip;

import gov.nist.core.InternalErrorHandler;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.CallID;
import gov.nist.javax.sip.header.Contact;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.stack.*;

import javax.sip.*;
import javax.sip.address.Hop;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.io.IOException;
import java.text.ParseException;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TooManyListenersException;
@SuppressWarnings("AlibabaAvoidUseTimer")
public final class SipProviderImpl implements SipProvider, SIPTransactionEventListener {
    protected SipListener sipListener;
    protected SipStackImpl sipStack;
    private HashMap listeningPoints;
    private EventScanner eventScanner;
    private String address;
    private int port;
    private boolean automaticDialogSupportEnabled;
    private String IN_ADDR_ANY = "0.0.0.0";
    private String IN6_ADDR_ANY = "::0";

    protected void stop() {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getLogWriter().logDebug("Exiting provider");
        }

        Iterator var1 = this.listeningPoints.values().iterator();

        while(var1.hasNext()) {
            ListeningPointImpl var2 = (ListeningPointImpl)var1.next();
            var2.removeSipProvider();
        }

        this.eventScanner.stop();
    }

    public ListeningPoint getListeningPoint(String var1) {
        if (var1 == null) {
            throw new NullPointerException("Null transport param");
        } else {
            return (ListeningPoint)this.listeningPoints.get(var1.toUpperCase());
        }
    }

    public void handleEvent(EventObject var1, SIPTransaction var2) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getLogWriter().logDebug("handleEvent " + var1 + "currentTransaction = " + var2 + "this.sipListener = " + this.sipListener + "sipEvent.source = " + var1.getSource());
            Dialog var3;
            if (var1 instanceof RequestEvent) {
                var3 = ((RequestEvent)var1).getDialog();
                this.sipStack.getLogWriter().logDebug("Dialog = " + var3);
            } else if (var1 instanceof ResponseEvent) {
                var3 = ((ResponseEvent)var1).getDialog();
                this.sipStack.getLogWriter().logDebug("Dialog = " + var3);
            }

            this.sipStack.getLogWriter().logStackTrace();
        }

        EventWrapper var4 = new EventWrapper(var1, var2);
        if (!this.sipStack.reEntrantListener) {
            this.eventScanner.addEvent(var4);
        } else {
            this.eventScanner.deliverEvent(var4);
        }

    }

    protected SipProviderImpl(SipStackImpl var1) {
        this.eventScanner = var1.eventScanner;
        this.sipStack = var1;
        this.eventScanner.incrementRefcount();
        this.listeningPoints = new HashMap();
        this.automaticDialogSupportEnabled = this.sipStack.isAutomaticDialogSupportEnabled();
    }

    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public boolean equals(Object var1) {
        return super.equals(var1);
    }

    public void addSipListener(SipListener var1) throws TooManyListenersException {
        if (this.sipStack.sipListener == null) {
            this.sipStack.sipListener = var1;
        } else if (this.sipStack.sipListener != var1) {
            throw new TooManyListenersException("Stack already has a listener. Only one listener per stack allowed");
        }

        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getLogWriter().logDebug("add SipListener " + var1);
        }

        this.sipListener = var1;
    }

    public ListeningPoint getListeningPoint() {
        return this.listeningPoints.size() > 0 ? (ListeningPoint)this.listeningPoints.values().iterator().next() : null;
    }

    public CallIdHeader getNewCallId() {
        String var1 = Utils.generateCallIdentifier(this.getListeningPoint().getIPAddress());
        CallID var2 = new CallID();

        try {
            var2.setCallId(var1);
        } catch (ParseException var4) {
        }

        return var2;
    }

    public ClientTransaction getNewClientTransaction(Request var1) throws TransactionUnavailableException {
        if (var1 == null) {
            throw new NullPointerException("null request");
        } else if (!this.sipStack.isAlive()) {
            throw new TransactionUnavailableException("Stack is stopped");
        } else {
            SIPRequest var2 = (SIPRequest)var1;
            if (var2.getTransaction() != null) {
                throw new TransactionUnavailableException("Transaction already assigned to request");
            } else {
                if (var2.getTopmostVia() == null) {
                    ListeningPointImpl var3 = (ListeningPointImpl)this.getListeningPoint("udp");
                    Via var4 = var3.getViaHeader();
                    var1.setHeader(var4);
                }

                try {
                    var2.checkHeaders();
                } catch (ParseException var11) {
                    throw new TransactionUnavailableException(var11.getMessage());
                }

                if (var2.getTopmostVia().getBranch() != null && var2.getTopmostVia().getBranch().startsWith("z9hG4bK") && this.sipStack.findTransaction((SIPRequest)var1, false) != null) {
                    throw new TransactionUnavailableException("Transaction already exists!");
                } else {
                    String var15 = var2.getTopmostVia().getTransport();
                    ListeningPointImpl var16 = (ListeningPointImpl)this.getListeningPoint(var15);
                    if (var16 == null || !var16.getMessageProcessor().getSavedIpAddress().equals(this.IN_ADDR_ANY) && !var16.getMessageProcessor().getSavedIpAddress().equals(this.IN6_ADDR_ANY) && !var16.getSentBy().equalsIgnoreCase(var2.getTopmostVia().getSentBy().toString())) {
                        if (this.sipStack.isLoggingEnabled()) {
                            this.sipStack.getLogWriter().logError("listeningPoint " + var16);
                            if (var16 != null) {
                                this.sipStack.getLogWriter().logError("port = " + var16.getPort());
                            }
                        }

                        throw new TransactionUnavailableException("sentBy does not match the sentby setting of the ListeningPoint " + var2.getTopmostVia().getSentBy().toString());
                    } else {
                        SIPClientTransaction var5;
                        if ("CANCEL".equalsIgnoreCase(var1.getMethod())) {
                            var5 = (SIPClientTransaction)this.sipStack.findCancelTransaction((SIPRequest)var1, false);
                            if (var5 != null) {
                                SIPClientTransaction var18 = this.sipStack.createClientTransaction((SIPRequest)var1, var5.getMessageChannel());
                                ((SIPTransaction)var18).addEventListener(this);
                                this.sipStack.addTransaction((SIPClientTransaction)var18);
                                if (var5.getDialog() != null) {
                                    ((SIPClientTransaction)var18).setDialog((SIPDialog)var5.getDialog(), var2.getDialogId(false));
                                }

                                return var18;
                            }
                        }

                        if (this.sipStack.isLoggingEnabled()) {
                            this.sipStack.getLogWriter().logDebug("could not find existing transaction for " + ((SIPRequest)var1).getFirstLine() + " creating a new one ");
                        }

                        var5 = null;

                        Hop var17;
                        try {
                            var17 = this.sipStack.getNextHop((SIPRequest)var1);
                            if (var17 == null) {
                                throw new TransactionUnavailableException("Cannot resolve next hop -- transaction unavailable");
                            }
                        } catch (SipException var14) {
                            throw new TransactionUnavailableException("Cannot resolve next hop -- transaction unavailable", var14);
                        }

                        String var6 = var2.getDialogId(false);
                        SIPDialog var7 = this.sipStack.getDialog(var6);
                        if (var7 != null && var7.getState() == DialogState.TERMINATED) {
                            this.sipStack.removeDialog(var7);
                        }

                        try {
                            String var8 = null;
                            if (var2.getTopmostVia().getBranch() == null || !var2.getTopmostVia().getBranch().startsWith("z9hG4bK")) {
                                var8 = Utils.generateBranchId();
                                var2.getTopmostVia().setBranch(var8);
                            }

                            var8 = var2.getTopmostVia().getBranch();
                            SIPClientTransaction var9 = (SIPClientTransaction)this.sipStack.createMessageChannel(var2, var16.getMessageProcessor(), var17);
                            var9.setNextHop(var17);
                            if (var9 == null) {
                                throw new TransactionUnavailableException("Cound not create tx");
                            } else {
                                var9.setOriginalRequest(var2);
                                var9.setBranch(var8);
                                if (this.sipStack.isDialogCreated(var1.getMethod())) {
                                    if (var7 != null) {
                                        var9.setDialog(var7, var2.getDialogId(false));
                                    } else if (this.isAutomaticDialogSupportEnabled()) {
                                        SIPDialog var10 = this.sipStack.createDialog(var9);
                                        var9.setDialog(var10, var2.getDialogId(false));
                                    }
                                } else if (var7 != null) {
                                    var9.setDialog(var7, var2.getDialogId(false));
                                }

                                var9.addEventListener(this);
                                return var9;
                            }
                        } catch (IOException var12) {
                            throw new TransactionUnavailableException("Could not resolve next hop or listening point unavailable! ", var12);
                        } catch (ParseException var13) {
                            InternalErrorHandler.handleException(var13);
                            throw new TransactionUnavailableException("Unexpected Exception FIXME! ", var13);
                        }
                    }
                }
            }
        }
    }

    public ServerTransaction getNewServerTransaction(Request var1) throws TransactionAlreadyExistsException, TransactionUnavailableException {
        if (!this.sipStack.isAlive()) {
            throw new TransactionUnavailableException("Stack is stopped");
        } else {
            SIPServerTransaction var2 = null;
            SIPRequest var3 = (SIPRequest)var1;

            try {
                var3.checkHeaders();
            } catch (ParseException var9) {
                this.sipStack.getLogWriter().logError("Missing a required Header", var9);
                throw new TransactionUnavailableException(var9.getMessage());
            }

            if ("NOTIFY".equals(var3.getMethod()) && var3.getFromTag() != null && var3.getToTag() == null) {
                SIPClientTransaction var4 = this.sipStack.findSubscribeTransaction(var3, (ListeningPointImpl)this.getListeningPoint());
                if (var4 == null && !this.sipStack.deliverUnsolicitedNotify) {
                    throw new TransactionUnavailableException("Cannot find matching Subscription (and gov.nist.javax.sip.DELIVER_UNSOLICITED_NOTIFY not set)");
                }
            }

            SIPDialog var5;
            String var10;
            if (this.sipStack.isDialogCreated(var3.getMethod())) {
                if (this.sipStack.findTransaction((SIPRequest)var1, true) != null) {
                    throw new TransactionAlreadyExistsException("server transaction already exists!");
                }

                var2 = (SIPServerTransaction)((SIPRequest)var1).getTransaction();
                if (var2 == null) {
                    throw new TransactionUnavailableException("Transaction not available");
                }

                if (var2.getOriginalRequest() == null) {
                    var2.setOriginalRequest(var3);
                }

                try {
                    this.sipStack.addTransaction(var2);
                } catch (IOException var8) {
                    throw new TransactionUnavailableException("Error sending provisional response");
                }

                var2.addEventListener(this);
                if (this.isAutomaticDialogSupportEnabled()) {
                    var10 = var3.getDialogId(true);
                    var5 = this.sipStack.getDialog(var10);
                    if (var5 == null) {
                        var5 = this.sipStack.createDialog(var2);
                    }

                    var2.setDialog(var5, var3.getDialogId(true));
                    if (this.sipStack.isDialogCreated(var3.getMethod())) {
                        this.sipStack.putInMergeTable(var2, var3);
                    }

                    var5.addRoute(var3);
                    if (var5.getRemoteTag() != null && var5.getLocalTag() != null) {
                        this.sipStack.putDialog(var5);
                    }
                }
            } else {
                if (!this.isAutomaticDialogSupportEnabled()) {
                    var2 = (SIPServerTransaction)this.sipStack.findTransaction((SIPRequest)var1, true);
                    if (var2 != null) {
                        throw new TransactionAlreadyExistsException("Transaction exists! ");
                    }

                    var2 = (SIPServerTransaction)((SIPRequest)var1).getTransaction();
                    if (var2 != null) {
                        if (var2.getOriginalRequest() == null) {
                            var2.setOriginalRequest(var3);
                        }

                        this.sipStack.mapTransaction(var2);
                        var10 = var3.getDialogId(true);
                        var5 = this.sipStack.getDialog(var10);
                        if (var5 != null) {
                            var5.addTransaction(var2);
                            var5.addRoute(var3);
                            var2.setDialog(var5, var3.getDialogId(true));
                        }

                        return var2;
                    }

                    MessageChannel var11 = (MessageChannel)var3.getMessageChannel();
                    var2 = this.sipStack.createServerTransaction(var11);
                    if (var2 == null) {
                        throw new TransactionUnavailableException("Transaction unavailable -- too many servrer transactions");
                    }

                    var2.setOriginalRequest(var3);
                    this.sipStack.mapTransaction(var2);
                    String var12 = var3.getDialogId(true);
                    SIPDialog var6 = this.sipStack.getDialog(var12);
                    if (var6 != null) {
                        var6.addTransaction(var2);
                        var6.addRoute(var3);
                        var2.setDialog(var6, var3.getDialogId(true));
                    }

                    return var2;
                }

                var2 = (SIPServerTransaction)this.sipStack.findTransaction((SIPRequest)var1, true);
                if (var2 != null) {
                    throw new TransactionAlreadyExistsException("Transaction exists! ");
                }

                var2 = (SIPServerTransaction)((SIPRequest)var1).getTransaction();
                if (var2 == null) {
                    throw new TransactionUnavailableException("Transaction not available!");
                }

                if (var2.getOriginalRequest() == null) {
                    var2.setOriginalRequest(var3);
                }

                try {
                    this.sipStack.addTransaction(var2);
                } catch (IOException var7) {
                    throw new TransactionUnavailableException("Could not send back provisional response!");
                }

                var10 = var3.getDialogId(true);
                var5 = this.sipStack.getDialog(var10);
                if (var5 != null) {
                    var5.addTransaction(var2);
                    var5.addRoute(var3);
                    var2.setDialog(var5, var3.getDialogId(true));
                }
            }

            return var2;
        }
    }

    public SipStack getSipStack() {
        return this.sipStack;
    }

    public void removeSipListener(SipListener var1) {
        if (var1 == this.sipListener) {
            this.sipListener = null;
        }

        boolean var2 = false;
        Iterator var3 = this.sipStack.getSipProviders();

        while(var3.hasNext()) {
            SipProviderImpl var4 = (SipProviderImpl)var3.next();
            if (var4.sipListener != null) {
                var2 = true;
            }
        }

        if (!var2) {
            this.sipStack.sipListener = null;
        }

    }

    public void sendRequest(Request request) throws SipException {
        if (!this.sipStack.isAlive()) {
            throw new SipException("Stack is stopped.");
        } else {
            if (((SIPRequest)request).getRequestLine() != null && "ACK".equals(request.getMethod())) {
                SIPDialog var2 = this.sipStack.getDialog(((SIPRequest)request).getDialogId(false));
                if (var2 != null && var2.getState() != null) {
                    this.sipStack.getLogWriter().logWarning("Dialog exists -- you may want to use Dialog.sendAck() " + var2.getState());
                }
            }
            Hop hop = this.sipStack.getRouter((SIPRequest)request).getNextHop(request);
            if (hop == null) {
                throw new SipException("could not determine next hop!");
            } else {
                SIPRequest var3 = (SIPRequest)request;
                if (!var3.isNullRequest() && var3.getTopmostVia() == null) {
                    throw new SipException("Invalid SipRequest -- no via header!");
                } else {
                    try {
                        if (!var3.isNullRequest()) {
                            Via var4 = var3.getTopmostVia();
                            String var5 = var4.getBranch();
                            if (var5 == null || var5.length() == 0) {
                                var4.setBranch(var3.getTransactionId());
                            }
                        }

                        MessageChannel messageChannel = null;
                        if (this.sipStack.isLoggingEnabled()) {
                            this.sipStack.getLogWriter().logDebug("发送TCP："+hop.getTransport());
                            for (Object o : listeningPoints.entrySet()) {
                                this.sipStack.getLogWriter().logDebug("发送TCP："+o);
                            }
                        }
                        if (this.listeningPoints.containsKey(hop.getTransport().toUpperCase())) {

                            messageChannel = this.sipStack.createRawMessageChannel(
                                    this.getListeningPoint(hop.getTransport()).getIPAddress(),
                                    this.getListeningPoint(hop.getTransport()).getPort(),
                                    hop);
                        }

                        if (messageChannel == null) {
                            throw new SipException("Could not create a message channel for " + hop.toString());
                        }

                        messageChannel.sendMessage(var3);
                    } catch (IOException var10) {
                        if (this.sipStack.isLoggingEnabled()) {
                            this.sipStack.getLogWriter().logException(var10);
                        }

                        throw new SipException("IO Exception occured while Sending Request", var10);
                    } catch (ParseException var11) {
                        InternalErrorHandler.handleException(var11);
                    } finally {
                        if (this.sipStack.isLoggingEnabled()) {
                            this.sipStack.getLogWriter().logDebug("done sending " + request.getMethod() + " to hop " + hop);
                        }

                    }

                }
            }
        }
    }

    public void sendResponse(Response var1) throws SipException {
        if (!this.sipStack.isAlive()) {
            throw new SipException("Stack is stopped");
        } else {
            SIPResponse var2 = (SIPResponse)var1;
            Via var3 = var2.getTopmostVia();
            if (var3 == null) {
                throw new SipException("No via header in response!");
            } else {
                String var4 = var3.getTransport();
                String var5 = var3.getReceived();
                if (var5 == null) {
                    var5 = var3.getHost();
                }

                int var6 = var3.getRPort();
                if (var6 == -1) {
                    var6 = var3.getPort();
                    if (var6 == -1) {
                        if ("TLS".equalsIgnoreCase(var4)) {
                            var6 = 5061;
                        } else {
                            var6 = 5060;
                        }
                    }
                }

                if (var5.indexOf(":") > 0 && var5.indexOf("[") < 0) {
                    var5 = "[" + var5 + "]";
                }

                Hop var7 = this.sipStack.getAddressResolver().resolveAddress(new HopImpl(var5, var6, var4));

                try {
                    ListeningPointImpl var8 = (ListeningPointImpl)this.getListeningPoint(var4);
                    if (var8 == null) {
                        throw new SipException("whoopsa daisy! no listening point found for transport " + var4);
                    } else {
                        MessageChannel var9 = this.sipStack.createRawMessageChannel(this.getListeningPoint(var7.getTransport()).getIPAddress(), var8.port, var7);
                        var9.sendMessage(var2);
                    }
                } catch (IOException var10) {
                    throw new SipException(var10.getMessage());
                }
            }
        }
    }

    public void setListeningPoint(ListeningPoint var1) {
        if (var1 == null) {
            throw new NullPointerException("Null listening point");
        } else {
            ListeningPointImpl var2 = (ListeningPointImpl)var1;
            var2.sipProvider = this;
            String var3 = var2.getTransport().toUpperCase();
            this.address = var1.getIPAddress();
            this.port = var1.getPort();
            this.listeningPoints.clear();
            this.listeningPoints.put(var3, var1);
        }
    }

    public Dialog getNewDialog(Transaction var1) throws SipException {
        if (var1 == null) {
            throw new NullPointerException("Null transaction!");
        } else if (!this.sipStack.isAlive()) {
            throw new SipException("Stack is stopped.");
        } else if (this.isAutomaticDialogSupportEnabled()) {
            throw new SipException(" Error - AUTOMATIC_DIALOG_SUPPORT is on");
        } else if (!this.sipStack.isDialogCreated(var1.getRequest().getMethod())) {
            throw new SipException("Dialog cannot be created for this method " + var1.getRequest().getMethod());
        } else {
            SIPDialog var2 = null;
            SIPTransaction var3 = (SIPTransaction)var1;
            SIPResponse var5;
            SIPRequest var6;
            String var7;
            if (var1 instanceof ServerTransaction) {
                SIPServerTransaction var4 = (SIPServerTransaction)var1;
                var5 = var4.getLastResponse();
                if (var5 != null && var5.getStatusCode() != 100) {
                    throw new SipException("Cannot set dialog after response has been sent");
                }

                var6 = (SIPRequest)var1.getRequest();
                var7 = var6.getDialogId(true);
                var2 = this.sipStack.getDialog(var7);
                if (var2 == null) {
                    var2 = this.sipStack.createDialog((SIPTransaction)var1);
                    var2.addTransaction(var3);
                    var2.addRoute(var6);
                    var3.setDialog(var2, (String)null);
                } else {
                    var3.setDialog(var2, var6.getDialogId(true));
                }

                if (this.sipStack.isDialogCreated(var6.getMethod())) {
                    this.sipStack.putInMergeTable(var4, var6);
                }
            } else {
                SIPClientTransaction var8 = (SIPClientTransaction)var1;
                var5 = var8.getLastResponse();
                if (var5 != null) {
                    throw new SipException("Cannot call this method after response is received!");
                }

                var6 = (SIPRequest)var8.getRequest();
                var7 = var6.getDialogId(false);
                var2 = this.sipStack.getDialog(var7);
                if (var2 != null) {
                    throw new SipException("Dialog already exists!");
                }

                var2 = this.sipStack.createDialog(var3);
                var8.setDialog(var2, (String)null);
            }

            return var2;
        }
    }

    public void transactionErrorEvent(SIPTransactionErrorEvent var1) {
        SIPTransaction var2 = (SIPTransaction)var1.getSource();
        Object var3;
        Timeout var4;
        TimeoutEvent var5;
        if (var1.getErrorID() == 2) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getLogWriter().logDebug("TransportError occured on " + var2);
            }

            var3 = var1.getSource();
            var4 = Timeout.TRANSACTION;
            var5 = null;
            if (var3 instanceof SIPServerTransaction) {
                var5 = new TimeoutEvent(this, (ServerTransaction)var3, var4);
            } else {
                var5 = new TimeoutEvent(this, (ClientTransaction)var3, var4);
            }

            this.handleEvent(var5, (SIPTransaction)var3);
        } else if (var1.getErrorID() == 1) {
            var3 = var1.getSource();
            var4 = Timeout.TRANSACTION;
            var5 = null;
            if (var3 instanceof SIPServerTransaction) {
                var5 = new TimeoutEvent(this, (ServerTransaction)var3, var4);
            } else {
                var5 = new TimeoutEvent(this, (ClientTransaction)var3, var4);
            }

            this.handleEvent(var5, (SIPTransaction)var3);
        } else if (var1.getErrorID() == 3) {
            var3 = var1.getSource();
            Transaction var7 = (Transaction)var3;
            if (var7.getDialog() != null) {
                InternalErrorHandler.handleException("Unexpected event !", this.sipStack.getLogWriter());
            }

            Timeout var8 = Timeout.RETRANSMIT;
            TimeoutEvent var6 = null;
            if (var3 instanceof SIPServerTransaction) {
                var6 = new TimeoutEvent(this, (ServerTransaction)var3, var8);
            } else {
                var6 = new TimeoutEvent(this, (ClientTransaction)var3, var8);
            }

            this.handleEvent(var6, (SIPTransaction)var3);
        }

    }

    public ListeningPoint[] getListeningPoints() {
        ListeningPointImpl[] var1 = new ListeningPointImpl[this.listeningPoints.size()];
        this.listeningPoints.values().toArray(var1);
        return var1;
    }

    public void addListeningPoint(ListeningPoint var1) throws ObjectInUseException {
        ListeningPointImpl var2 = (ListeningPointImpl)var1;
        if (var2.sipProvider != null && var2.sipProvider != this) {
            throw new ObjectInUseException("Listening point assigned to another provider");
        } else {
            String var3 = var2.getTransport().toUpperCase();
            if (this.listeningPoints.isEmpty()) {
                this.address = var1.getIPAddress();
                this.port = var1.getPort();
            } else if (!this.address.equals(var1.getIPAddress()) || this.port != var1.getPort()) {
                throw new ObjectInUseException("Provider already has different IP Address associated");
            }

            if (this.listeningPoints.containsKey(var3) && this.listeningPoints.get(var3) != var1) {
                throw new ObjectInUseException("Listening point already assigned for transport!");
            } else {
                var2.sipProvider = this;
                this.listeningPoints.put(var3, var2);
            }
        }
    }

    public void removeListeningPoint(ListeningPoint var1) throws ObjectInUseException {
        ListeningPointImpl var2 = (ListeningPointImpl)var1;
        if (var2.messageProcessor.inUse()) {
            throw new ObjectInUseException("Object is in use");
        } else {
            this.listeningPoints.remove(var2.getTransport().toUpperCase());
        }
    }

    public void removeListeningPoints() {
        Iterator var1 = this.listeningPoints.values().iterator();

        while(var1.hasNext()) {
            ListeningPointImpl var2 = (ListeningPointImpl)var1.next();
            var2.messageProcessor.stop();
            var1.remove();
        }

    }

    public void setAutomaticDialogSupportEnabled(boolean var1) {
        this.automaticDialogSupportEnabled = var1;
    }

    public boolean isAutomaticDialogSupportEnabled() {
        return this.automaticDialogSupportEnabled;
    }

    public ContactHeader createContactForProvider(String var1) {
        try {
            String var2 = this.getListeningPoint(var1).getIPAddress();
            int var3 = this.getListeningPoint(var1).getPort();
            SipUri var4 = new SipUri();
            var4.setHost(var2);
            var4.setPort(var3);
            var4.setTransportParam(var1);
            Contact var5 = new Contact();
            AddressImpl var6 = new AddressImpl();
            var6.setURI(var4);
            var5.setAddress(var6);
            return var5;
        } catch (Exception var7) {
            InternalErrorHandler.handleException(var7);
            return null;
        }
    }
}
