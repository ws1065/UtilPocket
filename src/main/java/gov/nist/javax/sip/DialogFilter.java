//
//package gov.nist.javax.sip;
//
//import gov.nist.core.InternalErrorHandler;
//import gov.nist.javax.sip.ListeningPointImpl;
//import gov.nist.javax.sip.SipProviderImpl;
//import gov.nist.javax.sip.SipStackImpl;
//import gov.nist.javax.sip.address.SipUri;
//import gov.nist.javax.sip.header.*;
//import gov.nist.javax.sip.message.SIPRequest;
//import gov.nist.javax.sip.message.SIPResponse;
//import gov.nist.javax.sip.stack.*;
//
//import javax.sip.*;
//import java.io.IOException;
//
//
///**
// * @program: SignalingForwarding
// * @description: 数据通过udpSocket接收完之后, 通过handleEvent方法把包装后的response数据发送到list中
// * 修改: 在processRequest方法中封装Response的时候将socket数据同步封装
// * @author: wangsw
// * @create: 2019-11-22 16:37
// */
//
//class DialogFilter implements ServerRequestInterface, ServerResponseInterface {
//    protected SIPTransaction transactionChannel;
//    protected ListeningPointImpl listeningPoint;
//    private SipStackImpl sipStack;
//
//    public DialogFilter(SipStackImpl var1) {
//        this.sipStack = var1;
//    }
//
//    public void processRequest(SIPRequest sipRequest, MessageChannel messageChannel) {
//        if(this.sipStack.isLoggingEnabled()) {
//            this.sipStack.getLogWriter().logDebug("PROCESSING INCOMING REQUEST " + sipRequest + " transactionChannel = " + this.transactionChannel + " listening point = " + this.listeningPoint.getIPAddress() + ":" + this.listeningPoint.getPort());
//        }
//
//        if(this.listeningPoint == null) {
//            if(this.sipStack.isLoggingEnabled()) {
//                this.sipStack.getLogWriter().logDebug("Dropping message: No listening point registered!");
//            }
//
//        } else {
//            SipStackImpl sipStack = (SipStackImpl)this.transactionChannel.getSIPStack();
//            gov.nist.javax.sip.SipProviderImpl sipProvider = this.listeningPoint.getProvider();
//            if(sipProvider == null) {
//                if(sipStack.isLoggingEnabled()) {
//                    sipStack.getLogWriter().logDebug("No provider - dropping !!");
//                }
//
//            } else {
//                if(sipStack == null) {
//                    InternalErrorHandler.handleException("Egads! no sip stack!");
//                }
//
//                SIPServerTransaction var5 = (SIPServerTransaction)this.transactionChannel;
//                if(var5 != null && sipStack.isLoggingEnabled()) {
//                    sipStack.getLogWriter().logDebug("transaction state = " + var5.getState());
//                }
//
//                String dialogId = sipRequest.getDialogId(true);
//                SIPDialog dialog = sipStack.getDialog(dialogId);
//                int port;
//                String transportParam;
//                if(dialog != null && sipProvider != dialog.getSipProvider()) {
//                    Contact contact = dialog.getMyContactHeader();
//                    if(contact != null) {
//                        SipUri sipUri = (SipUri)((SipUri)contact.getAddress().getURI());
//                        String host = sipUri.getHost();
//                        port = sipUri.getPort();
//                        transportParam = sipUri.getTransportParam();
//                        if(transportParam == null) {
//                            transportParam = "udp";
//                        }
//
//                        if(port == -1) {
//                            if(!transportParam.equals("udp") && !transportParam.equals("tcp")) {
//                                port = 5061;
//                            } else {
//                                port = 5060;
//                            }
//                        }
//
//                        if(host != null && (!host.equals(this.listeningPoint.getIPAddress()) || port != this.listeningPoint.getPort())) {
//                            if(sipStack.isLoggingEnabled()) {
//                                sipStack.getLogWriter().logDebug("nulling dialog -- listening point mismatch!  " + port + "  lp port = " + this.listeningPoint.getPort());
//                            }
//
//                            dialog = null;
//                        }
//                    }
//                }
//
//                SIPServerTransaction sipServerTransaction;
//                SIPResponse sipResponse;
//                if(dialog != null && sipRequest.getToTag() == null) {
//                    sipServerTransaction = sipStack.findMergedTransaction(sipRequest);
//                    if(sipServerTransaction != null && !sipServerTransaction.isMessagePartOfTransaction(sipRequest)) {
//                        sipResponse = sipRequest.createResponse(482);
//                        sipResponse.setHeader(sipStack.createServerHeaderForStack());
//                        if(sipStack.getLogWriter().isLoggingEnabled()) {
//                            sipStack.getLogWriter().logError("Loop detected while processing request");
//                        }
//
//                        try {
//                            sipProvider.sendResponse(sipResponse);
//                        } catch (SipException var28) {
//                            if(sipStack.getLogWriter().isLoggingEnabled()) {
//                                sipStack.getLogWriter().logError("Error sending response");
//                            }
//                        }
//
//                        return;
//                    }
//                }
//
//                if(sipStack.isLoggingEnabled()) {
//                    sipStack.getLogWriter().logDebug("dialogId = " + dialogId);
//                    sipStack.getLogWriter().logDebug("dialog = " + dialog);
//                }
//
//                if(sipRequest.getHeader("Route") != null && var5.getDialog() != null) {
//                    RouteList routeHeaders = sipRequest.getRouteHeaders();
//                    Route route = (Route)routeHeaders.getFirst();
//                    SipUri sipUri = (SipUri)route.getAddress().getURI();
//                    if(sipUri.getHostPort().hasPort()) {
//                        port = sipUri.getHostPort().getPort();
//                    } else if(this.listeningPoint.getTransport().equalsIgnoreCase("TLS")) {
//                        port = 5061;
//                    } else {
//                        port = 5060;
//                    }
//
//                    transportParam = sipUri.getHost();
//                    if((transportParam.equals(this.listeningPoint.getIPAddress()) || transportParam.equalsIgnoreCase(this.listeningPoint.getSentBy())) && port == this.listeningPoint.getPort()) {
//                        if(routeHeaders.size() == 1) {
//                            sipRequest.removeHeader("Route");
//                        } else {
//                            routeHeaders.removeFirst();
//                        }
//                    }
//                }
//
//                SIPResponse sipResponse1;
//                Server server;
//                if(sipRequest.getMethod().equals("UPDATE")) {
//                    if(sipProvider.isAutomaticDialogSupportEnabled() && dialog == null) {
//                        sipResponse1 = sipRequest.createResponse(481);
//                        server = sipStack.createServerHeaderForStack();
//                        sipResponse1.addHeader(server);
//
//                        try {
//                            sipProvider.sendResponse(sipResponse1);
//                        } catch (SipException var13) {
//                            sipStack.getLogWriter().logError("error sending response", var13);
//                        }
//
//                        if(var5 != null) {
//                            sipStack.removeTransaction(var5);
//                            var5.releaseSem();
//                        }
//
//                        return;
//                    }
//                } else if(sipRequest.getMethod().equals("ACK")) {
//                    if(var5 != null && var5.isInviteTransaction()) {
//                        if(sipStack.isLoggingEnabled()) {
//                            sipStack.getLogWriter().logDebug("Processing ACK for INVITE Tx ");
//                        }
//                    } else {
//                        if(sipStack.isLoggingEnabled()) {
//                            sipStack.getLogWriter().logDebug("Processing ACK for dialog " + dialog);
//                        }
//
//                        if(dialog == null) {
//                            if(sipStack.isLoggingEnabled()) {
//                                sipStack.getLogWriter().logDebug("Dialog does not exist " + sipRequest.getFirstLine() + " isServerTransaction = " + true);
//                            }
//
//                            sipServerTransaction = sipStack.getRetransmissionAlertTransaction(dialogId);
//                            if(sipServerTransaction != null && sipServerTransaction.isRetransmissionAlertEnabled()) {
//                                sipServerTransaction.disableRetransmissionAlerts();
//                            }
//                        } else {
//                            if(!dialog.handleAck(var5)) {
//                                return;
//                            }
//
//                            var5.passToListener();
//                            dialog.addTransaction(var5);
//                            dialog.addRoute(sipRequest);
//                            var5.setDialog(dialog, dialogId);
//                            if(sipStack.isDialogCreated(sipRequest.getMethod())) {
//                                sipStack.putInMergeTable(var5, sipRequest);
//                            }
//
//                            if(sipStack.deliverTerminatedEventForAck) {
//                                try {
//                                    sipStack.addTransaction(var5);
//                                } catch (IOException var27) {
//                                    ;
//                                }
//                            } else {
//                                var5.setMapped(true);
//                            }
//                        }
//                    }
//                } else if(sipRequest.getMethod().equals("PRACK")) {
//                    if(sipStack.isLoggingEnabled()) {
//                        sipStack.getLogWriter().logDebug("Processing PRACK for dialog " + dialog);
//                    }
//
//                    if(dialog == null && sipProvider.isAutomaticDialogSupportEnabled()) {
//                        if(sipStack.isLoggingEnabled()) {
//                            sipStack.getLogWriter().logDebug("Dialog does not exist " + sipRequest.getFirstLine() + " isServerTransaction = " + true);
//                        }
//
//                        if(sipStack.isLoggingEnabled()) {
//                            sipStack.getLogWriter().logDebug("Sending 481 for PRACK - automatic dialog support is enabled -- cant find dialog!");
//                        }
//
//                        sipResponse1 = sipRequest.createResponse(481);
//                        server = sipStack.createServerHeaderForStack();
//                        sipResponse1.addHeader(server);
//
//                        try {
//                            sipProvider.sendResponse(sipResponse1);
//                        } catch (SipException var14) {
//                            sipStack.getLogWriter().logError("error sending response", var14);
//                        }
//
//                        if(var5 != null) {
//                            sipStack.removeTransaction(var5);
//                            var5.releaseSem();
//                        }
//
//                        return;
//                    }
//
//                    if(dialog != null) {
//                        if(!dialog.handlePrack(sipRequest)) {
//                            sipStack.getLogWriter().logDebug("Dropping out of sequence PRACK ");
//                            if(var5 != null) {
//                                sipStack.removeTransaction(var5);
//                                var5.releaseSem();
//                            }
//
//                            return;
//                        }
//
//                        try {
//                            sipStack.addTransaction(var5);
//                            dialog.addTransaction(var5);
//                            dialog.addRoute(sipRequest);
//                        } catch (Exception var26) {
//                            InternalErrorHandler.handleException(var26);
//                        }
//                    } else {
//                        sipStack.getLogWriter().logDebug("Processing PRACK without a DIALOG -- this must be a proxy element");
//                    }
//                } else if(sipRequest.getMethod().equals("BYE")) {
//                    if(dialog != null && !dialog.isRequestConsumable(sipRequest)) {
//                        if(sipStack.isLoggingEnabled()) {
//                            sipStack.getLogWriter().logDebug("Dropping out of sequence BYE");
//                        }
//
//                        if(var5 != null) {
//                            sipStack.removeTransaction(var5);
//                        }
//
//                        return;
//                    }
//
//                    if(dialog == null && sipProvider.isAutomaticDialogSupportEnabled()) {
//                        sipResponse1 = sipRequest.createResponse(481);
//                        server = sipStack.createServerHeaderForStack();
//                        sipResponse1.addHeader(server);
//                        sipStack.getLogWriter().logDebug("dropping request -- automatic dialog support enabled and dialog does not exist!");
//
//                        try {
//                            var5.sendResponse(sipResponse1);
//                        } catch (SipException var15) {
//                            sipStack.getLogWriter().logError("Error in sending response", var15);
//                        }
//
//                        if(var5 != null) {
//                            sipStack.removeTransaction(var5);
//                            var5.releaseSem();
//                            var5 = null;
//                        }
//
//                        return;
//                    }
//
//                    if(var5 != null && dialog != null) {
//                        try {
//                            if(sipProvider == dialog.getSipProvider()) {
//                                sipStack.addTransaction(var5);
//                                dialog.addTransaction(var5);
//                                var5.setDialog(dialog, dialogId);
//                            }
//                        } catch (IOException var25) {
//                            InternalErrorHandler.handleException(var25);
//                        }
//                    }
//
//                    if(sipStack.getLogWriter().isLoggingEnabled()) {
//                        sipStack.getLogWriter().logDebug("BYE Tx = " + var5 + " isMapped =" + var5.isTransactionMapped());
//                    }
//                } else {
//                    Server var38;
//                    if(sipRequest.getMethod().equals("CANCEL")) {
//                        sipServerTransaction = (SIPServerTransaction)sipStack.findCancelTransaction(sipRequest, true);
//                        if(sipStack.getLogWriter().isLoggingEnabled()) {
//                            sipStack.getLogWriter().logDebug("Got a CANCEL, InviteServerTx = " + sipServerTransaction + " cancel Server Tx ID = " + var5 + " isMapped = " + var5.isTransactionMapped());
//                        }
//
//                        if(sipRequest.getMethod().equals("CANCEL")) {
//                            if(sipServerTransaction != null && sipServerTransaction.getState() == SIPTransaction.TERMINATED_STATE) {
//                                if(sipStack.isLoggingEnabled()) {
//                                    sipStack.getLogWriter().logDebug("Too late to cancel Transaction");
//                                }
//
//                                try {
//                                    var5.sendResponse(sipRequest.createResponse(200));
//                                } catch (Exception var29) {
//                                    if(var29.getCause() != null && var29.getCause() instanceof IOException) {
//                                        sipServerTransaction.raiseIOExceptionEvent();
//                                    }
//                                }
//
//                                return;
//                            }
//
//                            if(sipStack.isLoggingEnabled()) {
//                                sipStack.getLogWriter().logDebug("Cancel transaction = " + sipServerTransaction);
//                            }
//                        }
//
//                        if(var5 != null && sipServerTransaction != null && sipServerTransaction.getDialog() != null) {
//                            var5.setDialog((SIPDialog)sipServerTransaction.getDialog(), dialogId);
//                            dialog = (SIPDialog)sipServerTransaction.getDialog();
//                        } else if(sipServerTransaction == null && sipProvider.isAutomaticDialogSupportEnabled() && var5 != null) {
//                            sipResponse = sipRequest.createResponse(481);
//                            var38 = sipStack.createServerHeaderForStack();
//                            sipResponse.addHeader(var38);
//                            if(sipStack.isLoggingEnabled()) {
//                                sipStack.getLogWriter().logDebug("dropping request -- automatic dialog support enabled and INVITE ST does not exist!");
//                            }
//
//                            try {
//                                sipProvider.sendResponse(sipResponse);
//                            } catch (SipException var16) {
//                                InternalErrorHandler.handleException(var16);
//                            }
//
//                            if(var5 != null) {
//                                sipStack.removeTransaction(var5);
//                                var5.releaseSem();
//                            }
//
//                            return;
//                        }
//
//                        if(sipServerTransaction != null) {
//                            try {
//                                if(var5 != null) {
//                                    sipStack.addTransaction(var5);
//                                    var5.setPassToListener();
//                                    var5.setInviteTransaction(sipServerTransaction);
//                                    sipServerTransaction.acquireSem();
//                                }
//                            } catch (Exception var24) {
//                                InternalErrorHandler.handleException(var24);
//                            }
//                        }
//                    } else if(sipRequest.getMethod().equals("INVITE")) {
//                        sipServerTransaction = dialog == null?null:dialog.getInviteTransaction();
//                        if(dialog != null && var5 != null && sipServerTransaction != null && sipRequest.getCSeq().getSeqNumber() > dialog.getRemoteSeqNumber() && sipServerTransaction instanceof SIPServerTransaction && sipServerTransaction.isInviteTransaction() && sipServerTransaction.getState() != TransactionState.COMPLETED && sipServerTransaction.getState() != TransactionState.TERMINATED && sipServerTransaction.getState() != TransactionState.CONFIRMED) {
//                            if(sipStack.isLoggingEnabled()) {
//                                sipStack.getLogWriter().logDebug("Sending 500 response for out of sequence message");
//                            }
//
//                            sipResponse = sipRequest.createResponse(500);
//                            var38 = sipStack.createServerHeaderForStack();
//                            sipResponse.addHeader(var38);
//                            RetryAfter var46 = new RetryAfter();
//
//                            try {
//                                var46.setRetryAfter((int)(10.0D * Math.random()));
//                            } catch (InvalidArgumentException var18) {
//                                var18.printStackTrace();
//                            }
//
//                            sipResponse.addHeader(var46);
//
//                            try {
//                                var5.sendMessage(sipResponse);
//                            } catch (IOException var17) {
//                                var5.raiseIOExceptionEvent();
//                            }
//
//                            return;
//                        }
//
//                        SIPTransaction var40 = dialog == null?null:dialog.getLastTransaction();
//                        if(dialog != null && var40 != null && var40.isInviteTransaction() && var40 instanceof SIPClientTransaction && var40.getState() != TransactionState.COMPLETED && var40.getState() != TransactionState.TERMINATED) {
//                            if(dialog.getRemoteSeqNumber() + 1L == sipRequest.getCSeq().getSeqNumber()) {
//                                dialog.setRemoteSequenceNumber(sipRequest.getCSeq().getSeqNumber());
//                                if(sipStack.isLoggingEnabled()) {
//                                    sipStack.getLogWriter().logDebug("Sending 491 response for out of sequence message");
//                                }
//
//                                sipResponse = sipRequest.createResponse(491);
//                                var38 = sipStack.createServerHeaderForStack();
//                                sipResponse.addHeader(var38);
//
//                                try {
//                                    var5.sendMessage(sipResponse);
//                                } catch (IOException var19) {
//                                    var5.raiseIOExceptionEvent();
//                                }
//
//                                dialog.requestConsumed();
//                            } else if(sipStack.isLoggingEnabled()) {
//                                sipStack.getLogWriter().logDebug("Dropping message -- sequence number is too high!");
//                            }
//
//                            return;
//                        }
//                    }
//                }
//
//                if(dialog != null && var5 != null && !sipRequest.getMethod().equals("BYE") && !sipRequest.getMethod().equals("CANCEL") && !sipRequest.getMethod().equals("ACK") && !sipRequest.getMethod().equals("PRACK")) {
//                    if(!dialog.isRequestConsumable(sipRequest)) {
//                        if(sipStack.isLoggingEnabled()) {
//                            sipStack.getLogWriter().logDebug("Dropping out of sequence message " + dialog.getRemoteSeqNumber() + " " + sipRequest.getCSeq());
//                        }
//
//                        if(dialog.getRemoteSeqNumber() > sipRequest.getCSeq().getSeqNumber()) {
//                            if(sipStack.isLoggingEnabled()) {
//                                sipStack.getLogWriter().logDebug("Sending 500 response for out of sequence message");
//                            }
//
//                            sipResponse1 = sipRequest.createResponse(500);
//                            sipResponse1.setReasonPhrase("Request out of order");
//                            server = sipStack.createServerHeaderForStack();
//                            sipResponse1.addHeader(server);
//
//                            try {
//                                var5.sendMessage(sipResponse1);
//                                sipStack.removeTransaction(var5);
//                                var5.releaseSem();
//                            } catch (IOException var20) {
//                                var5.raiseIOExceptionEvent();
//                                sipStack.removeTransaction(var5);
//                            }
//                        }
//
//                        return;
//                    }
//
//                    try {
//                        if(sipProvider == dialog.getSipProvider()) {
//                            sipStack.addTransaction(var5);
//                            dialog.addTransaction(var5);
//                            dialog.addRoute(sipRequest);
//                            var5.setDialog(dialog, dialogId);
//                        }
//                    } catch (IOException var23) {
//                        var5.raiseIOExceptionEvent();
//                        sipStack.removeTransaction(var5);
//                        return;
//                    }
//                }
//
//                if(sipStack.getLogWriter().isLoggingEnabled()) {
//                    sipStack.getLogWriter().logDebug(sipRequest.getMethod() + " transaction.isMapped = " + var5.isTransactionMapped());
//                }
//
//                RequestEvent var42;
//                if(dialog == null && sipRequest.getMethod().equals("NOTIFY")) {
//                    SIPClientTransaction var39 = sipStack.findSubscribeTransaction(sipRequest, this.listeningPoint);
//                    if(sipStack.getLogWriter().isLoggingEnabled()) {
//                        sipStack.getLogWriter().logDebug("PROCESSING NOTIFY  DIALOG == null " + var39);
//                    }
//
//                    if(sipProvider.isAutomaticDialogSupportEnabled() && var39 == null && !sipStack.deliverUnsolicitedNotify) {
//                        try {
//                            if(sipStack.isLoggingEnabled()) {
//                                sipStack.getLogWriter().logDebug("Could not find Subscription for Notify Tx.");
//                            }
//
//                            SIPResponse var43 = sipRequest.createResponse(481);
//                            var43.setReasonPhrase("Subscription does not exist");
//                            Server var45 = sipStack.createServerHeaderForStack();
//                            var43.addHeader(var45);
//                            sipProvider.sendResponse(var43);
//                            return;
//                        } catch (Exception var30) {
//                            if(var30.getCause() != null && var30.getCause() instanceof IOException) {
//                                sipStack.getLogWriter().logDebug("Exception while sending error response statelessly");
//                            } else {
//                                InternalErrorHandler.handleException(var30);
//                            }
//                        }
//                    }
//
//                    if(var39 != null) {
//                        var5.setPendingSubscribe(var39);
//                        SIPDialog var41 = var39.getDefaultDialog();
//                        if(var41 != null && var41.getDialogId() != null && var41.getDialogId().equals(dialogId)) {
//                            var5.setDialog(var41, dialogId);
//                            if(!var5.isTransactionMapped()) {
//                                this.sipStack.mapTransaction(var5);
//                                var5.setPassToListener();
//
//                                try {
//                                    this.sipStack.addTransaction(var5);
//                                } catch (Exception var21) {
//                                    ;
//                                }
//                            }
//
//                            sipStack.putDialog(var41);
//                            if(var39 != null) {
//                                var41.addTransaction(var39);
//                                var39.setDialog(var41, dialogId);
//                            }
//                        } else {
//                            if(var41 != null && var41.getDialogId() == null) {
//                                var41.setDialogId(dialogId);
//                            } else {
//                                var41 = var39.getDialog(dialogId);
//                            }
//
//                            if(sipStack.getLogWriter().isLoggingEnabled()) {
//                                sipStack.getLogWriter().logDebug("PROCESSING NOTIFY Subscribe DIALOG " + var41);
//                            }
//
//                            if(var41 == null && (sipProvider.isAutomaticDialogSupportEnabled() || var39.getDefaultDialog() != null)) {
//                                Event var44 = (Event)sipRequest.getHeader("Event");
//                                if(sipStack.isEventForked(var44.getEventType())) {
//                                    var41 = SIPDialog.createFromNOTIFY(var39, var5);
//                                }
//                            }
//
//                            if(var41 != null) {
//                                var5.setDialog(var41, dialogId);
//                                var41.setState(DialogState.CONFIRMED.getValue());
//                                sipStack.putDialog(var41);
//                                var39.setDialog(var41, dialogId);
//                                if(!var5.isTransactionMapped()) {
//                                    this.sipStack.mapTransaction(var5);
//                                    var5.setPassToListener();
//
//                                    try {
//                                        this.sipStack.addTransaction(var5);
//                                    } catch (Exception var22) {
//                                        ;
//                                    }
//                                }
//                            }
//                        }
//
//                        if(var5 != null && var5.isTransactionMapped()) {
//                            var42 = new RequestEvent(sipProvider, var5, var41, sipRequest);
//                        } else {
//                            var42 = new RequestEvent(sipProvider, (ServerTransaction)null, var41, sipRequest);
//                        }
//                    } else {
//                        if(sipStack.getLogWriter().isLoggingEnabled()) {
//                            sipStack.getLogWriter().logDebug("could not find subscribe tx");
//                        }
//
//                        var42 = new RequestEvent(sipProvider, (ServerTransaction)null, (Dialog)null, sipRequest);
//                    }
//                } else if(var5 != null && var5.isTransactionMapped()) {
//                    var42 = new RequestEvent(sipProvider, var5, dialog, sipRequest,messageChannel.getPeerHostPort());
//                } else {
//                    var42 = new RequestEvent(sipProvider, (ServerTransaction)null, dialog, sipRequest,messageChannel.getPeerHostPort());
//                }
//
//                sipProvider.handleEvent(var42, var5);
//            }
//        }
//    }
//
//    public void processResponse(SIPResponse sipResponse, MessageChannel messageChannel, SIPDialog sipDialog) {
//        if(this.sipStack.isLoggingEnabled()) {
//            this.sipStack.getLogWriter().logDebug("PROCESSING INCOMING RESPONSE" + sipResponse.encodeMessage());
//        }
//
//        if(this.listeningPoint == null) {
//            if(this.sipStack.isLoggingEnabled()) {
//                this.sipStack.getLogWriter().logError("Dropping message: No listening point registered!");
//            }
//
//        } else {
//            gov.nist.javax.sip.SipProviderImpl sipProvider = this.listeningPoint.getProvider();
//            if(sipProvider == null) {
//                if(this.sipStack.isLoggingEnabled()) {
//                    this.sipStack.getLogWriter().logError("Dropping message:  no provider");
//                }
//
//            } else if(sipProvider.sipListener == null) {
//                if(this.sipStack.isLoggingEnabled()) {
//                    this.sipStack.getLogWriter().logError("No listener -- dropping response!");
//                }
//
//            } else {
//                SIPClientTransaction var5 = (SIPClientTransaction)this.transactionChannel;
//                SipStackImpl var6 = sipProvider.sipStack;
//                if(this.sipStack.isLoggingEnabled()) {
//                    var6.getLogWriter().logDebug("Transaction = " + var5);
//                }
//
//                ResponseEvent responseEvent;
//                if(var5 != null) {
//                    responseEvent = null;
//                    responseEvent = new ResponseEvent(sipProvider, var5, sipDialog, sipResponse);
//                    if(sipResponse.getToTag() != null && sipDialog != null && sipResponse.getStatusCode() != 100) {
//                        sipDialog.setLastResponse(var5, sipResponse);
//                        var5.setDialog(sipDialog, sipDialog.getDialogId());
//                    }
//
//                    sipProvider.handleEvent(responseEvent, var5);
//                } else {
//                    if(sipDialog != null) {
//                        if(sipResponse.getStatusCode() / 100 != 2) {
//                            if(this.sipStack.isLoggingEnabled()) {
//                                this.sipStack.getLogWriter().logDebug("Response is not a final response and dialog is found for response -- dropping response!");
//                            }
//
//                            return;
//                        }
//
//                        if(sipDialog.getState() == DialogState.TERMINATED) {
//                            if(this.sipStack.isLoggingEnabled()) {
//                                this.sipStack.getLogWriter().logDebug("Dialog is terminated -- dropping response!");
//                            }
//
//                            return;
//                        }
//
//                        if(sipResponse.getCSeq().getSeqNumber() == sipDialog.getOriginalLocalSequenceNumber() && sipResponse.getCSeq().getMethod().equals(sipDialog.getMethod())) {
//                            try {
//                                if(this.sipStack.isLoggingEnabled()) {
//                                    this.sipStack.getLogWriter().logDebug("Retransmission of OK detected: Resending last ACK");
//                                }
//
//                                sipDialog.resendAck();
//                                return;
//                            } catch (SipException var8) {
//                                this.sipStack.getLogWriter().logError("could not resend ack", var8);
//                            }
//                        }
//                    }
//
//                    if(this.sipStack.isLoggingEnabled()) {
//                        this.sipStack.getLogWriter().logDebug("could not find tx, handling statelessly Dialog = \t" + sipDialog);
//                    }
//
//                    responseEvent = new ResponseEvent(sipProvider, var5, sipDialog, sipResponse);
//                    sipProvider.handleEvent(responseEvent, var5);
//                }
//            }
//        }
//    }
//
//    public String getProcessingInfo() {
//        return null;
//    }
//
//    public void processResponse(SIPResponse sipResponse, MessageChannel messageChannel) {
//        String var3 = sipResponse.getDialogId(false);
//        SIPDialog sipDialog = this.sipStack.getDialog(var3);
//        String var5 = sipResponse.getCSeq().getMethod();
//        if(this.sipStack.isLoggingEnabled()) {
//            this.sipStack.getLogWriter().logDebug("PROCESSING INCOMING RESPONSE: " + sipResponse.encodeMessage());
//        }
//
//        if(this.listeningPoint == null) {
//            if(this.sipStack.isLoggingEnabled()) {
//                this.sipStack.getLogWriter().logDebug("Dropping message: No listening point registered!");
//            }
//
//        } else {
//            SipProviderImpl sipProvider = this.listeningPoint.getProvider();
//            if(sipProvider == null) {
//                if(this.sipStack.isLoggingEnabled()) {
//                    this.sipStack.getLogWriter().logDebug("Dropping message:  no provider");
//                }
//
//            } else if(sipProvider.sipListener == null) {
//                if(this.sipStack.isLoggingEnabled()) {
//                    this.sipStack.getLogWriter().logDebug("Dropping message:  no sipListener registered!");
//                }
//
//            } else {
//                SIPClientTransaction transactionChannel = (SIPClientTransaction)this.transactionChannel;
//                if(sipDialog == null && transactionChannel != null) {
//                    sipDialog = transactionChannel.getDialog(var3);
//                    if(sipDialog != null && sipDialog.getState() == DialogState.TERMINATED) {
//                        sipDialog = null;
//                    }
//                }
//
//                if(this.sipStack.isLoggingEnabled()) {
//                    this.sipStack.getLogWriter().logDebug("Transaction = " + transactionChannel + " sipDialog = " + sipDialog);
//                }
//
//                if(this.transactionChannel != null) {
//                    String var8 = ((SIPRequest)this.transactionChannel.getRequest()).getFromTag();
//                    if(var8 == null ^ sipResponse.getFrom().getTag() == null) {
//                        this.sipStack.getLogWriter().logDebug("From tag mismatch -- dropping response");
//                        return;
//                    }
//
//                    if(var8 != null && !var8.equalsIgnoreCase(sipResponse.getFrom().getTag())) {
//                        this.sipStack.getLogWriter().logDebug("From tag mismatch -- dropping response");
//                        return;
//                    }
//                }
//
//                if(this.sipStack.isDialogCreated(var5) && sipResponse.getStatusCode() != 100 && sipResponse.getFrom().getTag() != null && sipResponse.getTo().getTag() != null && sipDialog == null) {
//                    if(sipProvider.isAutomaticDialogSupportEnabled()) {
//                        if(this.transactionChannel != null) {
//                            if(sipDialog == null) {
//                                sipDialog = new SIPDialog(this.transactionChannel, sipResponse);
//                                this.transactionChannel.setDialog(sipDialog, sipResponse.getDialogId(false));
//                            }
//                        } else {
//                            sipDialog = new SIPDialog(sipProvider, sipResponse);
//                        }
//                    }
//                } else if(sipDialog != null && transactionChannel == null) {
//                    if(sipResponse.getStatusCode() / 100 != 2) {
//                        if(this.sipStack.isLoggingEnabled()) {
//                            this.sipStack.getLogWriter().logDebug("staus code != 200 ; statusCode = " + sipResponse.getStatusCode());
//                        }
//
//                        return;
//                    }
//
//                    if(sipDialog.getState() == DialogState.TERMINATED) {
//                        if(this.sipStack.isLoggingEnabled()) {
//                            this.sipStack.getLogWriter().logDebug("Dialog is terminated -- dropping response!");
//                        }
//
//                        return;
//                    }
//
//                    if(sipResponse.getCSeq().getSeqNumber() == sipDialog.getOriginalLocalSequenceNumber() && sipResponse.getCSeq().getMethod().equals(sipDialog.getMethod()) && sipDialog.isAckSeen()) {
//                        try {
//                            if(this.sipStack.isLoggingEnabled()) {
//                                this.sipStack.getLogWriter().logDebug("resending ACK");
//                            }
//
//                            sipDialog.resendAck();
//                            return;
//                        } catch (SipException var9) {
//                            ;
//                        }
//                    }
//                }
//
//                if(this.sipStack.isLoggingEnabled()) {
//                    this.sipStack.getLogWriter().logDebug("sending response to TU for processing ");
//                }
//
//                if(sipDialog != null && sipResponse.getStatusCode() != 100 && sipResponse.getTo().getTag() != null) {
//                    sipDialog.setLastResponse(transactionChannel, sipResponse);
//                }
//
//                ResponseEvent var10 = new ResponseEvent(sipProvider, transactionChannel, sipDialog, sipResponse,messageChannel.getPeerHostPort());
//                sipProvider.handleEvent(var10, transactionChannel);
//            }
//        }
//    }
//}
