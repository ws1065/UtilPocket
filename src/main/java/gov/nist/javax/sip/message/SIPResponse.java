//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.javax.sip.message;

import gov.nist.core.InternalErrorHandler;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.*;

import javax.sip.message.Response;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.ListIterator;
@SuppressWarnings("AlibabaAvoidUseTimer")
public final class SIPResponse extends gov.nist.javax.sip.message.SIPMessage implements Response {
    protected StatusLine statusLine;

    public static String getReasonPhrase(int var0) {
        String var1 = null;
        switch(var0) {
            case 100:
                var1 = "Trying";
                break;
            case 180:
                var1 = "Ringing";
                break;
            case 181:
                var1 = "Call is being forwarded";
                break;
            case 182:
                var1 = "Queued";
                break;
            case 183:
                var1 = "Session progress";
                break;
            case 200:
                var1 = "OK";
                break;
            case 202:
                var1 = "Accepted";
                break;
            case 300:
                var1 = "Multiple choices";
                break;
            case 301:
                var1 = "Moved permanently";
                break;
            case 302:
                var1 = "Moved Temporarily";
                break;
            case 305:
                var1 = "Use proxy";
                break;
            case 380:
                var1 = "Alternative service";
                break;
            case 400:
                var1 = "Bad request";
                break;
            case 401:
                var1 = "Unauthorized";
                break;
            case 402:
                var1 = "Payment required";
                break;
            case 403:
                var1 = "Forbidden";
                break;
            case 404:
                var1 = "Not found";
                break;
            case 405:
                var1 = "Method not allowed";
                break;
            case 406:
                var1 = "Not acceptable";
                break;
            case 407:
                var1 = "Proxy Authentication required";
                break;
            case 408:
                var1 = "Request timeout";
                break;
            case 410:
                var1 = "Gone";
                break;
            case 412:
                var1 = "Conditional request failed";
                break;
            case 413:
                var1 = "Request entity too large";
                break;
            case 414:
                var1 = "Request-URI too large";
                break;
            case 415:
                var1 = "Unsupported media type";
                break;
            case 416:
                var1 = "Unsupported URI Scheme";
                break;
            case 420:
                var1 = "Bad extension";
                break;
            case 421:
                var1 = "Etension Required";
                break;
            case 423:
                var1 = "Interval too brief";
                break;
            case 480:
                var1 = "Temporarily Unavailable";
                break;
            case 481:
                var1 = "Call leg/Transaction does not exist";
                break;
            case 482:
                var1 = "Loop detected";
                break;
            case 483:
                var1 = "Too many hops";
                break;
            case 484:
                var1 = "Address incomplete";
                break;
            case 485:
                var1 = "Ambiguous";
                break;
            case 486:
                var1 = "Busy here";
                break;
            case 487:
                var1 = "Request Terminated";
                break;
            case 488:
                var1 = "Not Accpetable here";
                break;
            case 489:
                var1 = "Bad Event";
                break;
            case 491:
                var1 = "Request Pending";
                break;
            case 493:
                var1 = "Undecipherable";
                break;
            case 500:
                var1 = "Server Internal Error";
                break;
            case 501:
                var1 = "Not implemented";
                break;
            case 502:
                var1 = "Bad gateway";
                break;
            case 503:
                var1 = "Service unavailable";
                break;
            case 504:
                var1 = "Gateway timeout";
                break;
            case 505:
                var1 = "SIP version not supported";
                break;
            case 513:
                var1 = "Message Too Large";
                break;
            case 600:
                var1 = "Busy everywhere";
                break;
            case 603:
                var1 = "Decline";
                break;
            case 604:
                var1 = "Does not exist anywhere";
                break;
            case 606:
                var1 = "Session Not acceptable";
                break;
            default:
                var1 = "Unkown Reason";
        }

        return var1;
    }

    public void setStatusCode(int var1) throws ParseException {
        if(var1 >= 100 && var1 <= 800) {
            if(this.statusLine == null) {
                this.statusLine = new StatusLine();
            }

            this.statusLine.setStatusCode(var1);
        } else {
            throw new ParseException("bad status code", 0);
        }
    }

    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    public int getStatusCode() {
        return this.statusLine.getStatusCode();
    }

    public void setReasonPhrase(String var1) {
        if(var1 == null) {
            throw new IllegalArgumentException("Bad reason phrase");
        } else {
            if(this.statusLine == null) {
                this.statusLine = new StatusLine();
            }

            this.statusLine.setReasonPhrase(var1);
        }
    }

    public String getReasonPhrase() {
        return this.statusLine != null && this.statusLine.getReasonPhrase() != null?this.statusLine.getReasonPhrase():"";
    }

    public static boolean isFinalResponse(int var0) {
        return var0 >= 200 && var0 < 700;
    }

    public boolean isFinalResponse() {
        return isFinalResponse(this.statusLine.getStatusCode());
    }

    public void setStatusLine(StatusLine var1) {
        this.statusLine = var1;
    }

    public SIPResponse() {
    }

    public String debugDump() {
        String var1 = super.debugDump();
        this.stringRepresentation = "";
        this.sprint("gov.nist.javax.sip.message.SIPResponse");
        this.sprint("{");
        if(this.statusLine != null) {
            this.sprint(this.statusLine.debugDump());
        }

        this.sprint(var1);
        this.sprint("}");
        return this.stringRepresentation;
    }

    public void checkHeaders() throws ParseException {
        if(this.getCSeq() == null) {
            throw new ParseException("CSeq Is missing ", 0);
        } else if(this.getTo() == null) {
            throw new ParseException("To Is missing ", 0);
        } else if(this.getFrom() == null) {
            throw new ParseException("From Is missing ", 0);
        } else if(this.getViaHeaders() == null) {
            throw new ParseException("Via Is missing ", 0);
        } else if(this.getCallId() == null) {
            throw new ParseException("Call-ID Is missing ", 0);
        } else if(this.getStatusCode() > 699) {
            throw new ParseException("Unknown error code!" + this.getStatusCode(), 0);
        }
    }

    public String encode() {
        String var1;
        if(this.statusLine != null) {
            var1 = this.statusLine.encode() + super.encode();
        } else {
            var1 = super.encode();
        }

        return var1;
    }

    public String encodeMessage() {
        String var1;
        if(this.statusLine != null) {
            var1 = this.statusLine.encode() + super.encodeSIPHeaders();
        } else {
            var1 = super.encodeSIPHeaders();
        }

        return var1;
    }

    public LinkedList getMessageAsEncodedStrings() {
        LinkedList var1 = super.getMessageAsEncodedStrings();
        if(this.statusLine != null) {
            var1.addFirst(this.statusLine.encode());
        }

        return var1;
    }

    public Object clone() {
        SIPResponse var1 = (SIPResponse)super.clone();
        if(this.statusLine != null) {
            var1.statusLine = (StatusLine)this.statusLine.clone();
        }

        return var1;
    }

    public boolean equals(Object var1) {
        if(!this.getClass().equals(var1.getClass())) {
            return false;
        } else {
            SIPResponse var2 = (SIPResponse)var1;
            return this.statusLine.equals(var2.statusLine) && super.equals(var1);
        }
    }

    public boolean match(Object var1) {
        if(var1 == null) {
            return true;
        } else if(!var1.getClass().equals(this.getClass())) {
            return false;
        } else if(var1 == this) {
            return true;
        } else {
            SIPResponse var2 = (SIPResponse)var1;
            StatusLine var3 = var2.statusLine;
            return this.statusLine == null && var3 != null?false:(this.statusLine == var3?super.match(var1):this.statusLine.match(var2.statusLine) && super.match(var1));
        }
    }

    public byte[] encodeAsBytes() {
        byte[] var1 = null;
        if(this.statusLine != null) {
            try {
                var1 = this.statusLine.encode().getBytes("UTF-8");
            } catch (UnsupportedEncodingException var4) {
                InternalErrorHandler.handleException(var4);
            }
        }

        byte[] var2 = super.encodeAsBytes();
        byte[] var3 = new byte[var1.length + var2.length];
        System.arraycopy(var1, 0, var3, 0, var1.length);
        System.arraycopy(var2, 0, var3, var1.length, var2.length);
        return var3;
    }

    public String getDialogId(boolean var1) {
        CallID var2 = (CallID)this.getCallId();
        From var3 = (From)this.getFrom();
        To var4 = (To)this.getTo();
        StringBuffer var5 = new StringBuffer(var2.getCallId());
        if(!var1) {
            if(var3.getTag() != null) {
                var5.append(":");
                var5.append(var3.getTag());
            }

            if(var4.getTag() != null) {
                var5.append(":");
                var5.append(var4.getTag());
            }
        } else {
            if(var4.getTag() != null) {
                var5.append(":");
                var5.append(var4.getTag());
            }

            if(var3.getTag() != null) {
                var5.append(":");
                var5.append(var3.getTag());
            }
        }

        return var5.toString().toLowerCase();
    }

    public String getDialogId(boolean var1, String var2) {
        CallID var3 = (CallID)this.getCallId();
        From var4 = (From)this.getFrom();
        StringBuffer var5 = new StringBuffer(var3.getCallId());
        if(!var1) {
            if(var4.getTag() != null) {
                var5.append(":");
                var5.append(var4.getTag());
            }

            if(var2 != null) {
                var5.append(":");
                var5.append(var2);
            }
        } else {
            if(var2 != null) {
                var5.append(":");
                var5.append(var2);
            }

            if(var4.getTag() != null) {
                var5.append(":");
                var5.append(var4.getTag());
            }
        }

        return var5.toString().toLowerCase();
    }

    private final void setBranch(Via var1, String var2) {
        String var3;
        if("ACK".equals(var2)) {
            if(this.statusLine.getStatusCode() >= 300) {
                var3 = this.getTopmostVia().getBranch();
            } else {
                var3 = Utils.generateBranchId();
            }
        } else {
            if(!"CANCEL".equals(var2)) {
                return;
            }

            var3 = this.getTopmostVia().getBranch();
        }

        try {
            var1.setBranch(var3);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

    }

    public SIPRequest createRequest(SipUri var1, Via var2, CSeq var3) {
        SIPRequest var4 = new SIPRequest();
        String var5 = var3.getMethod();
        var4.setMethod(var5);
        var4.setRequestURI(var1);
        this.setBranch(var2, var5);
        var4.setHeader(var2);
        var4.setHeader(var3);
        ListIterator var6 = this.getHeaders();

        while(var6.hasNext()) {
            SIPHeader var7 = (SIPHeader)var6.next();
            if(!gov.nist.javax.sip.message.SIPMessage.isResponseHeader(var7) && !(var7 instanceof ViaList) && !(var7 instanceof CSeq) && !(var7 instanceof ContentType) && !(var7 instanceof ContentLength) && !(var7 instanceof RequireList) && !(var7 instanceof ContactList) && !(var7 instanceof RecordRouteList)) {
                if(var7 instanceof To) {
                    var7 = (SIPHeader)var7.clone();
                } else if(var7 instanceof From) {
                    var7 = (SIPHeader)var7.clone();
                }

                try {
                    var4.attachHeader(var7, false);
                } catch (SIPDuplicateHeaderException var10) {
                    var10.printStackTrace();
                }
            }
        }

        try {
            var4.attachHeader(new MaxForwards(70), false);
        } catch (Exception var9) {
            ;
        }

        return var4;
    }

    public String getFirstLine() {
        return this.statusLine == null?null:this.statusLine.encode();
    }

    public void setSIPVersion(String var1) {
        this.statusLine.setSipVersion(var1);
    }

    public String getSIPVersion() {
        return this.statusLine.getSipVersion();
    }

    public String toString() {
        return this.statusLine == null?"":this.statusLine.encode() + super.encode();
    }

    public SIPRequest createRequest(SipUri var1, Via var2, CSeq var3, From var4, To var5) {
        SIPRequest var6 = new SIPRequest();
        String var7 = var3.getMethod();
        var6.setMethod(var7);
        var6.setRequestURI(var1);
        this.setBranch(var2, var7);
        var6.setHeader(var2);
        var6.setHeader(var3);
        ListIterator var8 = this.getHeaders();

        while(var8.hasNext()) {
            Object var9 = (SIPHeader)var8.next();
            if(!SIPMessage.isResponseHeader((SIPHeader)var9) && !(var9 instanceof ViaList) && !(var9 instanceof CSeq) && !(var9 instanceof ContentType) && !(var9 instanceof ContentLength) && !(var9 instanceof RecordRouteList) && !(var9 instanceof RequireList) && !(var9 instanceof ContactList) && !(var9 instanceof ContentLength)) {
                if(var9 instanceof To) {
                    var9 = var5;
                } else if(var9 instanceof From) {
                    var9 = var4;
                }

                try {
                    var6.attachHeader((SIPHeader)var9, false);
                } catch (SIPDuplicateHeaderException var12) {
                    var12.printStackTrace();
                }
            }
        }

        try {
            var6.attachHeader(new MaxForwards(70), false);
        } catch (Exception var11) {
            ;
        }

        return var6;
    }


}
