
package gov.nist.javax.sip.stack;

import gov.nist.core.InternalErrorHandler;
import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.header.Expires;
import gov.nist.javax.sip.header.RSeq;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;

import javax.sip.*;
import javax.sip.address.Hop;
import javax.sip.message.Response;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 *  无此构造方法的参数，故删除参数LingerTimer var1 = new LingerTimer(SIPServerTransaction.this);
 */
@SuppressWarnings("AlibabaAvoidUseTimer")
public class SIPServerTransaction extends SIPTransaction implements ServerRequestInterface, ServerTransaction {
    private int rseqNumber;
    private ServerRequestInterface requestOf;
    private SIPDialog dialog;
    private SIPResponse pendingReliableResponse;
    private ProvisionalResponseTask provisionalResponseTask;
    private boolean retransmissionAlertEnabled;
    private RetransmissionAlertTimerTask retransmissionAlertTimerTask;
    protected boolean isAckSeen;
    private SIPClientTransaction pendingSubscribeTransaction;
    private SIPServerTransaction inviteTransaction;

    private void sendResponse(SIPResponse var1) throws IOException {
        if (this.isReliable()) {
            this.getMessageChannel().sendMessage(var1);
        } else {
            Via var2 = var1.getTopmostVia();
            String var3 = var2.getTransport();
            if (var3 == null) {
                throw new IOException("missing transport!");
            }

            int var4 = var2.getRPort();
            if (var4 == -1) {
                var4 = var2.getPort();
            }

            if (var4 == -1) {
                if ("TLS".equalsIgnoreCase(var3)) {
                    var4 = 5061;
                } else {
                    var4 = 5060;
                }
            }

            String var5 = null;
            if (var2.getMAddr() != null) {
                var5 = var2.getMAddr();
            } else {
                var5 = var2.getParameter("received");
                if (var5 == null) {
                    var5 = var2.getHost();
                }
            }

            Hop var6 = this.sipStack.addressResolver.resolveAddress(new HopImpl(var5, var4, var3));
            MessageChannel var7 = this.getSIPStack().createRawMessageChannel(this.getSipProvider().getListeningPoint(var6.getTransport()).getIPAddress(), this.getPort(), var6);
            if (var7 == null) {
                throw new IOException("Could not create a message channel for " + var6);
            }

            var7.sendMessage(var1);
        }

    }

    protected SIPServerTransaction(gov.nist.javax.sip.stack.SIPTransactionStack var1, MessageChannel var2) {
        super(var1, var2);
        if (var1.maxListenerResponseTime != -1) {
            var1.timer.schedule(new ListenerExecutionMaxTimer(), (long)(var1.maxListenerResponseTime * 1000));
        }

        this.rseqNumber = (int)(Math.random() * 1000.0D);
        if (var1.isLoggingEnabled()) {
            var1.logWriter.logDebug("Creating Server Transaction" + this.getBranchId());
            var1.logWriter.logStackTrace();
        }

    }

    public void setRequestInterface(ServerRequestInterface var1) {
        this.requestOf = var1;
    }

    public MessageChannel getResponseChannel() {
        return this;
    }

    public boolean isMessagePartOfTransaction(SIPMessage sipMessage) {
        //需要返回true，否则报异常
        boolean bool = false;
        //获得发送数据的method
        String method = sipMessage.getCSeq().getMethod();
        //INVITE请求跳过
        if ("INVITE".equals(method) || !this.isTerminated()) {
            ViaList viaList = sipMessage.getViaHeaders();
            if (viaList != null) {
                Via via = (Via)viaList.getFirst();
                String branch = via.getBranch();
                if (branch != null && !branch.toLowerCase().startsWith("z9hg4bk")) {
                    branch = null;
                }

                if (branch != null && this.getBranch() != null) {
                    if ("CANCEL".equals(method)) {
                        //当时CANCEL方法的时候:对比branch 对比via.getSentBy
                        bool = "CANCEL".equals(this.getMethod()) && this.getBranch().equalsIgnoreCase(branch) && via.getSentBy().equals(((Via)this.getOriginalRequest().getViaHeaders().getFirst()).getSentBy());
                    } else {
                        //对比branch 对比via.getSentBy
                        bool = this.getBranch().equalsIgnoreCase(branch) && via.getSentBy().equals(((Via)this.getOriginalRequest().getViaHeaders().getFirst()).getSentBy());
                    }
                } else {
                    String fromTag = super.fromTag;
                    String fromTag1 = sipMessage.getFrom().getTag();
                    boolean isFromTagNotNull = fromTag == null || fromTag1 == null;
                    String var10 = super.toTag;
                    String var11 = sipMessage.getTo().getTag();
                    boolean isToTagNotNull = var10 == null || var11 == null;
                    boolean isResponse = sipMessage instanceof SIPResponse;
                    if ("CANCEL".equalsIgnoreCase(sipMessage.getCSeq().getMethod())
                            && !"CANCEL".equalsIgnoreCase(this.getOriginalRequest().getCSeq().getMethod())) {
                        bool = false;
                        //是response子类
                        //getRequestURI相等
                        //FromTag相等
                        //getCSeq().getSeqNumber() 相等
                        //...
                    } else if ((isResponse || this.getOriginalRequest().getRequestURI().equals(((SIPRequest)sipMessage).getRequestURI()))
                            && (isFromTagNotNull || fromTag.equalsIgnoreCase(fromTag1)) && (isToTagNotNull || var10.equalsIgnoreCase(var11))
                            && this.getOriginalRequest().getCallId().getCallId().equalsIgnoreCase(sipMessage.getCallId().getCallId())
                            && this.getOriginalRequest().getCSeq().getSeqNumber() == sipMessage.getCSeq().getSeqNumber()
                            && (!"CANCEL".equals(sipMessage.getCSeq().getMethod())
                            || this.getOriginalRequest().getMethod().equals(sipMessage.getCSeq().getMethod()))
                            && via.equals(this.getOriginalRequest().getViaHeaders().getFirst())) {
                        bool = true;
                    }
                }
            }
        }

        return bool;
    }

    protected void map() {
        if (this.getRealState() == null || this.getRealState() == TransactionState.TRYING) {
            if (this.isInviteTransaction() && !this.isMapped && this.sipStack.timer != null) {
                this.isMapped = true;
                this.sipStack.timer.schedule(new SendTrying(), 200L);
            } else {
                this.isMapped = true;
            }
        }

        this.sipStack.removePendingTransaction(this);
    }

    public boolean isTransactionMapped() {
        return this.isMapped;
    }

    public void processRequest(SIPRequest var1, MessageChannel var2) {
        boolean var3 = false;
        if (this.sipStack.logWriter.isLoggingEnabled()) {
            this.sipStack.logWriter.logDebug("processRequest: " + var1.getFirstLine());
            this.sipStack.logWriter.logDebug("tx state = " + this.getRealState());
        }

        try {
            if (this.getRealState() == null) {
                this.setOriginalRequest(var1);
                this.setState(TransactionState.TRYING);
                var3 = true;
                this.setPassToListener();
                if (this.isInviteTransaction() && this.isMapped) {
                    this.sendMessage(var1.createResponse(100, "Trying"));
                }
            } else {
                if (this.isInviteTransaction() && TransactionState.COMPLETED == this.getRealState() && "ACK".equals(var1.getMethod())) {
                    this.setState(TransactionState.CONFIRMED);
                    this.disableRetransmissionTimer();
                    if (!this.isReliable()) {
                        this.enableTimeoutTimer(this.TIMER_I);
                    } else {
                        this.setState(TransactionState.TERMINATED);
                    }

                    if (this.sipStack.isNon2XXAckPassedToListener()) {
                        this.requestOf.processRequest(var1, this);
                    } else {
                        if (this.sipStack.logWriter.isLoggingEnabled()) {
                            this.sipStack.logWriter.logDebug("ACK received for server Tx " + this.getTransactionId() + " not delivering to application!");
                        }

                        this.semRelease();
                    }

                    return;
                }

                if (var1.getMethod().equals(this.getOriginalRequest().getMethod())) {
                    if (TransactionState.PROCEEDING != this.getRealState() && TransactionState.COMPLETED != this.getRealState()) {
                        if ("ACK".equals(var1.getMethod())) {
                            if (this.requestOf != null) {
                                this.requestOf.processRequest(var1, this);
                            } else {
                                this.semRelease();
                            }
                        }
                    } else {
                        this.semRelease();
                        if (this.lastResponse != null) {
                            super.sendMessage(this.lastResponse);
                        }
                    }

                    this.sipStack.logWriter.logDebug("completed processing retransmitted request : " + var1.getFirstLine() + this + " txState = " + this.getState() + " lastResponse = " + this.getLastResponse());
                    return;
                }
            }

            if (TransactionState.COMPLETED != this.getRealState() && TransactionState.TERMINATED != this.getRealState() && this.requestOf != null) {
                if (this.getOriginalRequest().getMethod().equals(var1.getMethod())) {
                    if (var3) {
                        this.requestOf.processRequest(var1, this);
                    } else {
                        this.semRelease();
                    }
                } else if (this.requestOf != null) {
                    this.requestOf.processRequest(var1, this);
                } else {
                    this.semRelease();
                }
            } else {
                if (this.getSIPStack().isDialogCreated(this.getOriginalRequest().getMethod()) && this.getRealState() == TransactionState.TERMINATED && "ACK".equals(var1.getMethod()) && this.requestOf != null) {
                    SIPDialog var4 = this.dialog;
                    if (var4 != null && var4.ackProcessed) {
                        this.semRelease();
                    } else {
                        if (var4 != null) {
                            var4.ackReceived(var1);
                            var4.ackProcessed = true;
                        }

                        this.requestOf.processRequest(var1, this);
                    }
                } else if ("CANCEL".equals(var1.getMethod())) {
                    if (this.sipStack.isLoggingEnabled()) {
                        this.sipStack.logWriter.logDebug("Too late to cancel Transaction");
                    }

                    this.semRelease();

                    try {
                        this.sendMessage(var1.createResponse(200));
                    } catch (IOException var5) {
                    }
                }

                this.sipStack.logWriter.logDebug("Dropping request " + this.getRealState());
            }
        } catch (IOException var6) {
            this.semRelease();
            this.raiseIOExceptionEvent();
        }

    }

    public void sendMessage(SIPMessage var1) throws IOException {
        SIPResponse var2 = (SIPResponse)var1;
        int var3 = var2.getStatusCode();

        try {
            if (this.getOriginalRequest().getTopmostVia().getBranch() != null) {
                var2.getTopmostVia().setBranch(this.getBranch());
            } else {
                var2.getTopmostVia().removeParameter("branch");
            }

            if (!this.getOriginalRequest().getTopmostVia().hasPort()) {
                var2.getTopmostVia().removePort();
            }
        } catch (ParseException var6) {
            var6.printStackTrace();
        }

        if (!var2.getCSeq().getMethod().equals(this.getOriginalRequest().getMethod())) {
            this.sendResponse(var2);
        } else {
            if (this.getRealState() == TransactionState.TRYING) {
                if (var3 / 100 == 1) {
                    this.setState(TransactionState.PROCEEDING);
                } else if (200 <= var3 && var3 <= 699) {
                    if (!this.isInviteTransaction()) {
                        if (!this.isReliable()) {
                            this.setState(TransactionState.COMPLETED);
                            this.enableTimeoutTimer(64);
                        } else {
                            this.setState(TransactionState.TERMINATED);
                        }
                    } else if (var3 / 100 == 2) {
                        this.disableRetransmissionTimer();
                        this.disableTimeoutTimer();
                        this.collectionTime = 64;
                        this.setState(TransactionState.TERMINATED);
                        if (this.dialog != null) {
                            this.dialog.setRetransmissionTicks();
                        }
                    } else {
                        this.setState(TransactionState.COMPLETED);
                        if (!this.isReliable()) {
                            this.enableRetransmissionTimer();
                        }

                        this.enableTimeoutTimer(64);
                    }
                }
            } else if (this.getRealState() == TransactionState.PROCEEDING) {
                if (this.isInviteTransaction()) {
                    if (var3 / 100 == 2) {
                        this.disableRetransmissionTimer();
                        this.disableTimeoutTimer();
                        this.collectionTime = 64;
                        this.setState(TransactionState.TERMINATED);
                        if (this.dialog != null) {
                            this.dialog.setRetransmissionTicks();
                        }
                    } else if (300 <= var3 && var3 <= 699) {
                        this.setState(TransactionState.COMPLETED);
                        if (!this.isReliable()) {
                            this.enableRetransmissionTimer();
                        }

                        this.enableTimeoutTimer(64);
                    }
                } else if (200 <= var3 && var3 <= 699) {
                    this.setState(TransactionState.COMPLETED);
                    if (!this.isReliable()) {
                        this.disableRetransmissionTimer();
                        this.enableTimeoutTimer(64);
                    } else {
                        this.setState(TransactionState.TERMINATED);
                    }
                }
            } else if (TransactionState.COMPLETED == this.getRealState()) {
                return;
            }

            try {
                if (this.sipStack.getLogWriter().isLoggingEnabled()) {
                    this.sipStack.getLogWriter().logDebug("sendMessage : tx = " + this + " getState = " + this.getState());
                }

                this.lastResponse = var2;
                this.sendResponse(var2);
            } catch (IOException var5) {
                this.setState(TransactionState.TERMINATED);
                this.collectionTime = 0;
                throw var5;
            }
        }
    }

    public String getViaHost() {
        return this.getMessageChannel().getViaHost();
    }

    public int getViaPort() {
        return this.getMessageChannel().getViaPort();
    }

    protected void fireRetransmissionTimer() {
        try {
            if (this.sipStack.getLogWriter().isLoggingEnabled()) {
                this.sipStack.getLogWriter().logDebug("fireRetransmissionTimer() -- ");
            }

            if (this.isInviteTransaction() && this.lastResponse != null) {
                if (!this.retransmissionAlertEnabled) {
                    if (this.lastResponse.getStatusCode() / 100 > 2) {
                        super.sendMessage(this.lastResponse);
                    }
                } else {
                    SipProviderImpl var1 = this.getSipProvider();
                    TimeoutEvent var2 = new TimeoutEvent(var1, this, Timeout.RETRANSMIT);
                    var1.handleEvent(var2, this);
                }
            }
        } catch (IOException var3) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.logWriter.logException(var3);
            }

            this.raiseErrorEvent(2);
        }

    }

    private void fireReliableResponseRetransmissionTimer() {
        try {
            super.sendMessage(this.pendingReliableResponse);
        } catch (IOException var2) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.logWriter.logException(var2);
            }

            this.setState(TransactionState.TERMINATED);
            this.raiseErrorEvent(2);
        }

    }

    protected void fireTimeoutTimer() {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.logWriter.logDebug("SIPServerTransaction.fireTimeoutTimer this = " + this + " current state = " + this.getRealState() + " method = " + this.getOriginalRequest().getMethod());
        }

        SIPDialog var1 = this.dialog;
        if (this.getSIPStack().isDialogCreated(this.getOriginalRequest().getMethod()) && (TransactionState.CALLING == this.getRealState() || TransactionState.TRYING == this.getRealState())) {
            var1.setState(3);
        } else if ("BYE".equals(this.getOriginalRequest().getMethod()) && var1 != null && var1.isTerminatedOnBye()) {
            var1.setState(3);
        }

        if (TransactionState.COMPLETED == this.getRealState() && this.isInviteTransaction()) {
            this.raiseErrorEvent(1);
            this.setState(TransactionState.TERMINATED);
            this.sipStack.removeTransaction(this);
        } else if (TransactionState.COMPLETED == this.getRealState() && !this.isInviteTransaction()) {
            this.setState(TransactionState.TERMINATED);
            this.sipStack.removeTransaction(this);
        } else if (TransactionState.CONFIRMED == this.getRealState() && this.isInviteTransaction()) {
            this.setState(TransactionState.TERMINATED);
            this.sipStack.removeTransaction(this);
        } else if (!this.isInviteTransaction() && (TransactionState.COMPLETED == this.getRealState() || TransactionState.CONFIRMED == this.getRealState())) {
            this.setState(TransactionState.TERMINATED);
        } else if (this.isInviteTransaction() && TransactionState.TERMINATED == this.getRealState()) {
            this.raiseErrorEvent(1);
            if (var1 != null) {
                var1.setState(3);
            }
        }

    }

    public SIPResponse getLastResponse() {
        return this.lastResponse;
    }

    public void setOriginalRequest(SIPRequest var1) {
        super.setOriginalRequest(var1);
    }

    public void sendResponse(Response response) throws SipException {
        SIPResponse sipResponse = (SIPResponse)response;
        SIPDialog dialog = this.dialog;
        if (response == null) {
            throw new NullPointerException("null response");
        } else {
            try {
                sipResponse.checkHeaders();
            } catch (ParseException var7) {
                throw new SipException(var7.getMessage());
            }

            if (!sipResponse.getCSeq().getMethod().equals(this.getMethod())) {
                throw new SipException("CSeq method does not match Request method of request that created the tx.");
            } else {
                if ("SUBSCRIBE".equals(this.getMethod()) && response.getStatusCode() / 100 == 2) {
                    if (response.getHeader("Expires") == null) {
                        throw new SipException("Expires header is mandatory in 2xx response of SUBSCRIBE");
                    }

                    Expires var4 = (Expires)this.getOriginalRequest().getExpires();
                    Expires var5 = (Expires)response.getExpires();
                    if (var4 != null && var5.getExpires() > var4.getExpires()) {
                        throw new SipException("Response Expires time exceeds request Expires time : See RFC 3265 3.1.1");
                    }
                }

                if (sipResponse.getStatusCode() == 200 && "INVITE".equals(sipResponse.getCSeq().getMethod()) && sipResponse.getHeader("Contact") == null) {
                    throw new SipException("Contact Header is mandatory for the OK to the INVITE");
//                } else if (!this.isMessagePartOfTransaction((SIPMessage)response)) {
//                    this.isMessagePartOfTransaction((SIPMessage)response);
//                    throw new SipException("Response does not belong to this transaction.");
                } else {
                    ViaList viaList = ((SIPMessage)response).getViaHeaders();
                    if ( !this.isMessagePartOfTransaction((SIPMessage)response) && viaList != null) {
                        Via via = (Via) viaList.getFirst();
                        String branch = via.getBranch();
                        this.setBranch(branch);
                        ((Via) this.getOriginalRequest().getViaHeaders().getFirst()).setSentBy(via.getSentBy());
                    }
                    try {
                        if (this.pendingReliableResponse != null && response.getStatusCode() / 100 == 2 && "application".equalsIgnoreCase(this.pendingReliableResponse.getContentTypeHeader().getContentType()) && "sdp".equalsIgnoreCase(this.pendingReliableResponse.getContentTypeHeader().getContentSubType())) {
                            throw new SipException("cannot send response -- unacked povisional");
                        } else {
                            if (this.pendingReliableResponse != null && sipResponse.isFinalResponse()) {
                                this.provisionalResponseTask.cancel();
                                this.provisionalResponseTask = null;
                            }

                            if (dialog != null) {
                                if (sipResponse.getStatusCode() / 100 == 2 && this.sipStack.isDialogCreated(sipResponse.getCSeq().getMethod())) {
                                    if (dialog.getLocalTag() == null && sipResponse.getTo().getTag() == null) {
                                        sipResponse.getTo().setTag(Utils.generateTag());
                                    } else if (dialog.getLocalTag() != null && sipResponse.getToTag() == null) {
                                        sipResponse.setToTag(dialog.getLocalTag());
                                    } else if (dialog.getLocalTag() != null && sipResponse.getToTag() != null && !dialog.getLocalTag().equals(sipResponse.getToTag())) {
                                        throw new SipException("Tag mismatch dialogTag is " + dialog.getLocalTag() + " responseTag is " + sipResponse.getToTag());
                                    }
                                }

                                if (!sipResponse.getCallId().getCallId().equals(dialog.getCallId().getCallId())) {
                                    throw new SipException("Dialog mismatch!");
                                }
                            }

                            if (dialog != null && dialog.getLocalTag() != null && sipResponse.getTo().getTag() == null && sipResponse.getStatusCode() != 100) {
                                sipResponse.getTo().setTag(dialog.getLocalTag());
                            }

                            String var10 = ((SIPRequest)this.getRequest()).getFrom().getTag();
                            if (var10 != null && sipResponse.getFromTag() != null && !sipResponse.getFromTag().equals(var10)) {
                                throw new SipException("From tag of response does not match sipResponse from tag");
                            } else {
                                if (var10 != null) {
                                    sipResponse.getFrom().setTag(var10);
                                } else if (this.sipStack.isLoggingEnabled()) {
                                    this.sipStack.logWriter.logDebug("WARNING -- Null From tag in request!!");
                                }

                                if (dialog != null && response.getStatusCode() != 100) {
                                    if (!dialog.checkResponseTags(sipResponse)) {
                                        throw new SipException("Response tags dont match with Dialog tags");
                                    }

                                    DialogState var12 = dialog.getState();
                                    dialog.setLastResponse(this, (SIPResponse)response);
                                    if (var12 == null && dialog.getState() == DialogState.TERMINATED) {
                                        DialogTerminatedEvent var6 = new DialogTerminatedEvent(dialog.getSipProvider(), dialog);
                                        dialog.getSipProvider().handleEvent(var6, this);
                                    }
                                } else if (dialog == null && "INVITE".equals(this.getMethod()) && this.retransmissionAlertEnabled && this.retransmissionAlertTimerTask == null && response.getStatusCode() / 100 == 2) {
                                    String var11 = ((SIPResponse)response).getDialogId(true);
                                    this.retransmissionAlertTimerTask = new RetransmissionAlertTimerTask(var11);
                                    this.sipStack.retransmissionAlertTransactions.put(var11, this);
                                    this.sipStack.timer.schedule(this.retransmissionAlertTimerTask, 0L, 500L);
                                }

                                this.sendMessage((SIPResponse)response);
                            }
                        }
                    } catch (IOException var8) {
                        throw new SipException(var8.getMessage());
                    } catch (ParseException var9) {
                        throw new SipException(var9.getMessage());
                    }
                }
            }
        }
    }

    private TransactionState getRealState() {
        return super.getState();
    }

    public TransactionState getState() {
        return this.isInviteTransaction() && TransactionState.TRYING == super.getState() ? TransactionState.PROCEEDING : super.getState();
    }

    public void setState(TransactionState var1) {
        if (var1 == TransactionState.TERMINATED && this.isReliable() && !this.getSIPStack().cacheServerConnections) {
            this.collectionTime = 64;
        }

        super.setState(var1);
    }

    protected void startTransactionTimer() {
        if (this.sipStack.timer != null) {
            TransactionTimer var1 = new TransactionTimer();
            this.sipStack.timer.schedule(var1, 0L, 500L);
        }

    }

    public boolean equals(Object var1) {
        if (!var1.getClass().equals(this.getClass())) {
            return false;
        } else {
            SIPServerTransaction var2 = (SIPServerTransaction)var1;
            return this.getBranch().equalsIgnoreCase(var2.getBranch());
        }
    }

    public Dialog getDialog() {
        return this.dialog;
    }

    public void setDialog(SIPDialog var1, String var2) {
        if (this.sipStack.logWriter.isLoggingEnabled()) {
            this.sipStack.logWriter.logDebug("setDialog " + this + " dialog = " + var1);
        }

        this.dialog = var1;
        if (var2 != null) {
            this.dialog.setAssigned();
        }

        if (this.retransmissionAlertEnabled && this.retransmissionAlertTimerTask != null) {
            this.retransmissionAlertTimerTask.cancel();
            this.retransmissionAlertTimerTask = null;
            this.sipStack.retransmissionAlertTransactions.remove(this.retransmissionAlertTimerTask.dialogId);
        }

        this.retransmissionAlertEnabled = false;
    }

    public void terminate() throws ObjectInUseException {
        this.setState(TransactionState.TERMINATED);
        if (this.retransmissionAlertTimerTask != null) {
            this.retransmissionAlertTimerTask.cancel();
            this.retransmissionAlertTimerTask = null;
            this.sipStack.retransmissionAlertTransactions.remove(this.retransmissionAlertTimerTask.dialogId);
        }

    }

    protected void sendReliableProvisionalResponse(Response var1) throws SipException {
        if (this.pendingReliableResponse != null) {
            throw new SipException("Unacknowledged response");
        } else {
            this.pendingReliableResponse = (SIPResponse)var1;
            RSeq var2 = (RSeq)var1.getHeader("RSeq");
            if (var1.getHeader("RSeq") == null) {
                var2 = new RSeq();
                var1.setHeader(var2);
            }

            try {
                ++this.rseqNumber;
                var2.setSeqNumber((long)this.rseqNumber);
                this.lastResponse = (SIPResponse)var1;
                this.sendMessage((SIPMessage)var1);
                this.provisionalResponseTask = new ProvisionalResponseTask();
                this.sipStack.timer.schedule(this.provisionalResponseTask, 0L, 500L);
            } catch (Exception var4) {
                InternalErrorHandler.handleException(var4);
            }

        }
    }

    public SIPResponse getReliableProvisionalResponse() {
        return this.pendingReliableResponse;
    }

    public boolean prackRecieved() {
        if (this.pendingReliableResponse == null) {
            return false;
        } else {
            this.provisionalResponseTask.cancel();
            this.pendingReliableResponse = null;
            return true;
        }
    }

    public void enableRetransmissionAlerts() throws SipException {
        if (this.getDialog() != null) {
            throw new SipException("Dialog associated with tx");
        } else if (!"INVITE".equals(this.getMethod())) {
            throw new SipException("Request Method must be INVITE");
        } else {
            this.retransmissionAlertEnabled = true;
        }
    }

    public boolean isRetransmissionAlertEnabled() {
        return this.retransmissionAlertEnabled;
    }

    public void disableRetransmissionAlerts() {
        if (this.retransmissionAlertTimerTask != null && this.retransmissionAlertEnabled) {
            this.retransmissionAlertTimerTask.cancel();
            this.retransmissionAlertEnabled = false;
            this.retransmissionAlertTimerTask = null;
            String var1 = this.retransmissionAlertTimerTask.dialogId;
            this.sipStack.retransmissionAlertTransactions.remove(var1);
        }

    }

    public void setAckSeen() {
        this.isAckSeen = true;
    }

    public boolean ackSeen() {
        return this.isAckSeen;
    }

    public void setMapped(boolean var1) {
        this.isMapped = true;
    }

    public void setPendingSubscribe(SIPClientTransaction var1) {
        this.pendingSubscribeTransaction = var1;
    }

    public void releaseSem() {
        if (this.pendingSubscribeTransaction != null) {
            this.pendingSubscribeTransaction.releaseSem();
        } else if (this.inviteTransaction != null && "CANCEL".equals(this.getMethod())) {
            this.inviteTransaction.releaseSem();
        }

        super.releaseSem();
    }

    public void setInviteTransaction(SIPServerTransaction var1) {
        this.inviteTransaction = var1;
    }

    class TransactionTimer extends SIPStackTimerTask {
        public TransactionTimer() {
            if (SIPServerTransaction.this.sipStack.logWriter.isLoggingEnabled()) {
                SIPServerTransaction.this.sipStack.logWriter.logDebug("TransactionTimer() : " + SIPServerTransaction.this.getTransactionId());
            }

        }

        protected void runTask() {
            if (SIPServerTransaction.this.isTerminated()) {
                try {
                    this.cancel();
                } catch (IllegalStateException var2) {
                    if (!SIPServerTransaction.this.sipStack.isAlive()) {
                        return;
                    }
                }

//                LingerTimer var1 = new LingerTimer(SIPServerTransaction.this);
                LingerTimer var1 = new LingerTimer();
                SIPServerTransaction.this.sipStack.timer.schedule(var1, 8000L);
            } else {
                SIPServerTransaction.this.fireTimer();
            }

        }
    }

    class SendTrying extends SIPStackTimerTask {
        protected SendTrying() {
            if (SIPServerTransaction.this.sipStack.isLoggingEnabled()) {
                SIPServerTransaction.this.sipStack.logWriter.logDebug("scheduled timer for " + SIPServerTransaction.this);
            }

        }

        protected void runTask() {
            SIPServerTransaction var1 = SIPServerTransaction.this;
            if (var1.getRealState() == null || TransactionState.TRYING == var1.getRealState()) {
                if (SIPServerTransaction.this.sipStack.isLoggingEnabled()) {
                    SIPServerTransaction.this.sipStack.logWriter.logDebug(" sending Trying current state = " + var1.getRealState());
                }

                try {
                    var1.sendMessage(var1.getOriginalRequest().createResponse(100, "Trying"));
                    if (var1.sipStack.isLoggingEnabled()) {
                        SIPServerTransaction.this.sipStack.logWriter.logDebug(" trying sent " + var1.getRealState());
                    }
                } catch (IOException var3) {
                    if (var1.sipStack.isLoggingEnabled()) {
                        SIPServerTransaction.this.sipStack.logWriter.logError("IO error sending  TRYING");
                    }
                }
            }

        }
    }

    class ListenerExecutionMaxTimer extends SIPStackTimerTask {
        SIPServerTransaction serverTransaction = SIPServerTransaction.this;

        ListenerExecutionMaxTimer() {
        }

        protected void runTask() {
            try {
                if (this.serverTransaction.getState() == null) {
                    this.serverTransaction.terminate();
                    SIPTransactionStack var1 = this.serverTransaction.getSIPStack();
                    var1.removePendingTransaction(this.serverTransaction);
                    var1.removeTransaction(this.serverTransaction);
                }
            } catch (Exception var2) {
                SIPServerTransaction.this.sipStack.getLogWriter().logError("unexpected exception", var2);
            }

        }
    }

    class ProvisionalResponseTask extends SIPStackTimerTask {
        int ticks = 1;
        int ticksLeft;

        public ProvisionalResponseTask() {
            this.ticksLeft = this.ticks;
        }

        protected void runTask() {
            SIPServerTransaction var1 = SIPServerTransaction.this;
            if (var1.isTerminated()) {
                this.cancel();
            } else {
                --this.ticksLeft;
                if (this.ticksLeft == -1) {
                    var1.fireReliableResponseRetransmissionTimer();
                    this.ticksLeft = 2 * this.ticks;
                }
            }

        }
    }

    class RetransmissionAlertTimerTask extends SIPStackTimerTask {
        String dialogId;
        int ticks = 1;
        int ticksLeft;

        public RetransmissionAlertTimerTask(String var2) {
            this.ticksLeft = this.ticks;
        }

        protected void runTask() {
            SIPServerTransaction var1 = SIPServerTransaction.this;
            --this.ticksLeft;
            if (this.ticksLeft == -1) {
                var1.fireRetransmissionTimer();
                this.ticksLeft = 2 * this.ticks;
            }

        }
    }
}
