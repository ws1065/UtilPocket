
package gov.nist.javax.sip.message;

import gov.nist.core.InternalErrorHandler;
import gov.nist.javax.sip.header.*;
import gov.nist.javax.sip.parser.HeaderParser;
import gov.nist.javax.sip.parser.ParserFactory;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.header.*;
import javax.sip.message.Message;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;

public abstract class SIPMessage extends MessageObject implements Message {
    protected static final String DEFAULT_ENCODING = "gbk";
    private static final String CONTENT_TYPE_LOWERCASE = SIPHeaderNamesCache.toLowerCase("Content-Type");
    private static final String ERROR_LOWERCASE = SIPHeaderNamesCache.toLowerCase("Error-Info");
    private static final String CONTACT_LOWERCASE = SIPHeaderNamesCache.toLowerCase("Contact");
    private static final String VIA_LOWERCASE = SIPHeaderNamesCache.toLowerCase("Via");
    private static final String AUTHORIZATION_LOWERCASE = SIPHeaderNamesCache.toLowerCase("Authorization");
    private static final String ROUTE_LOWERCASE = SIPHeaderNamesCache.toLowerCase("Route");
    private static final String RECORDROUTE_LOWERCASE = SIPHeaderNamesCache.toLowerCase("Record-Route");
    private static final String CONTENT_DISPOSITION_LOWERCASE = SIPHeaderNamesCache.toLowerCase("Content-Disposition");
    private static final String CONTENT_ENCODING_LOWERCASE = SIPHeaderNamesCache.toLowerCase("Content-Encoding");
    private static final String CONTENT_LANGUAGE_LOWERCASE = SIPHeaderNamesCache.toLowerCase("Content-Language");
    private static final String EXPIRES_LOWERCASE = SIPHeaderNamesCache.toLowerCase("Expires");
    protected LinkedList unrecognizedHeaders = new LinkedList();
    protected LinkedList headers = new LinkedList();
    protected From fromHeader;
    protected To toHeader;
    protected CSeq cSeqHeader;
    protected CallID callIdHeader;
    protected ContentLength contentLengthHeader;
    protected MaxForwards maxForwardsHeader;
    protected int size;
    private String messageContent;
    private byte[] messageContentBytes;
    private Object messageContentObject;
    private Hashtable nameTable = new Hashtable();

    public SIPMessage() {
        try {
            this.attachHeader(new ContentLength(0), false);
        } catch (Exception var2) {
        }

    }

    public static boolean isRequestHeader(SIPHeader var0) {
        return var0 instanceof AlertInfo || var0 instanceof InReplyTo || var0 instanceof Authorization || var0 instanceof MaxForwards || var0 instanceof UserAgent || var0 instanceof Priority || var0 instanceof ProxyAuthorization || var0 instanceof ProxyRequire || var0 instanceof ProxyRequireList || var0 instanceof Route || var0 instanceof RouteList || var0 instanceof Subject || var0 instanceof SIPIfMatch;
    }

    public static boolean isResponseHeader(SIPHeader var0) {
        return var0 instanceof ErrorInfo || var0 instanceof ProxyAuthenticate || var0 instanceof Server || var0 instanceof Unsupported || var0 instanceof RetryAfter || var0 instanceof Warning || var0 instanceof WWWAuthenticate || var0 instanceof SIPETag || var0 instanceof RSeq;
    }

    public LinkedList getMessageAsEncodedStrings() {
        LinkedList var1 = new LinkedList();
        synchronized(this.headers) {
            ListIterator var3 = this.headers.listIterator();

            while(var3.hasNext()) {
                SIPHeader var4 = (SIPHeader)var3.next();
                if (var4 instanceof SIPHeaderList) {
                    SIPHeaderList var5 = (SIPHeaderList)var4;
                    var1.addAll(var5.getHeadersAsEncodedStrings());
                } else {
                    var1.add(var4.encode());
                }
            }

            return var1;
        }
    }

    protected String encodeSIPHeaders() {
        StringBuffer var1 = new StringBuffer();
        synchronized(this.headers) {
            ListIterator var3 = this.headers.listIterator();

            while(var3.hasNext()) {
                SIPHeader var4 = (SIPHeader)var3.next();
                if (!(var4 instanceof ContentLength)) {
                    var4.encode(var1);
                }
            }

            return this.contentLengthHeader.encode(var1).append("\r\n").toString();
        }
    }

    public abstract String encodeMessage();

    public abstract String getDialogId(boolean var1);

    public boolean match(Object var1) {
        if (var1 == null) {
            return true;
        } else if (!var1.getClass().equals(this.getClass())) {
            return false;
        } else {
            SIPMessage var2 = (SIPMessage)var1;
            ListIterator var3 = var2.getHeaders();

            label74:
            while(var3.hasNext()) {
                SIPHeader var4 = (SIPHeader)var3.next();
                LinkedList var5 = this.getHeaderList(var4.getHeaderName());
                if (var5 == null || var5.size() == 0) {
                    return false;
                }

                if (var4 instanceof SIPHeaderList) {
                    ListIterator var11 = ((SIPHeaderList)var4).listIterator();

                    boolean var14;
                    do {
                        SIPHeader var12;
                        do {
                            if (!var11.hasNext()) {
                                continue label74;
                            }

                            var12 = (SIPHeader)var11.next();
                        } while(var12 instanceof ContentLength);

                        ListIterator var13 = var5.listIterator();
                        var14 = false;

                        while(var13.hasNext()) {
                            SIPHeader var10 = (SIPHeader)var13.next();
                            if (var10.match(var12)) {
                                var14 = true;
                                break;
                            }
                        }
                    } while(var14);

                    return false;
                } else {
                    SIPHeader var6 = var4;
                    ListIterator var7 = var5.listIterator();
                    boolean var8 = false;

                    while(var7.hasNext()) {
                        SIPHeader var9 = (SIPHeader)var7.next();
                        if (var9.match(var6)) {
                            var8 = true;
                            break;
                        }
                    }

                    if (!var8) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public void merge(Object var1) {
        if (!var1.getClass().equals(this.getClass())) {
            throw new IllegalArgumentException("Bad class " + var1.getClass());
        } else {
            SIPMessage var2 = (SIPMessage)var1;
            Object[] var3 = var2.headers.toArray();

            for(int var4 = 0; var4 < var3.length; ++var4) {
                SIPHeader var5 = (SIPHeader)var3[var4];
                String var6 = var5.getHeaderName();
                LinkedList var7 = this.getHeaderList(var6);
                if (var7 == null) {
                    this.attachHeader(var5);
                } else {
                    ListIterator var8 = var7.listIterator();

                    while(var8.hasNext()) {
                        SIPHeader var9 = (SIPHeader)var8.next();
                        var9.merge(var5);
                    }
                }
            }

        }
    }

    public String encode() {
        StringBuffer var1 = new StringBuffer();
        synchronized(this.headers) {
            ListIterator var3 = this.headers.listIterator();

            while(true) {
                if (!var3.hasNext()) {
                    break;
                }

                SIPHeader var4 = (SIPHeader)var3.next();
                if (!(var4 instanceof ContentLength)) {
                    var1.append(var4.encode());
                }
            }
        }

        var1.append(this.contentLengthHeader.encode()).append("\r\n");
        String var2;
        if (this.messageContentObject != null) {
            var2 = this.getContent().toString();
            var1.append(var2);
        } else if (this.messageContent != null || this.messageContentBytes != null) {
            var2 = null;

            try {
                if (this.messageContent != null) {
                    var2 = this.messageContent;
                } else {
                    var2 = new String(this.messageContentBytes, DEFAULT_ENCODING);
                }
            } catch (UnsupportedEncodingException var6) {
                var2 = "";
            }

            var1.append(var2);
        }

        return var1.toString();
    }

    public byte[] encodeAsBytes() {
        StringBuffer var1 = new StringBuffer();
        synchronized(this.headers) {
            ListIterator var3 = this.headers.listIterator();

            while(true) {
                if (!var3.hasNext()) {
                    break;
                }

                SIPHeader var4 = (SIPHeader)var3.next();
                if (!(var4 instanceof ContentLength)) {
                    var4.encode(var1);
                }
            }
        }

        this.contentLengthHeader.encode(var1);
        var1.append("\r\n");
        byte[] var2 = null;
        byte[] var9 = this.getRawContent();
        if (var9 != null) {
            byte[] var10 = null;

            try {
                var10 = var1.toString().getBytes(DEFAULT_ENCODING);
            } catch (UnsupportedEncodingException var7) {
                InternalErrorHandler.handleException(var7);
            }

            var2 = new byte[var10.length + var9.length];
            System.arraycopy(var10, 0, var2, 0, var10.length);
            System.arraycopy(var9, 0, var2, var10.length, var9.length);
        } else {
            try {
                var2 = var1.toString().getBytes(DEFAULT_ENCODING);
            } catch (UnsupportedEncodingException var6) {
                InternalErrorHandler.handleException(var6);
            }
        }

        return var2;
    }

    public Object clone() {
        SIPMessage var1 = (SIPMessage)super.clone();
        var1.nameTable = new Hashtable();
        var1.fromHeader = null;
        var1.toHeader = null;
        var1.cSeqHeader = null;
        var1.callIdHeader = null;
        var1.contentLengthHeader = null;
        var1.maxForwardsHeader = null;
        if (this.headers != null) {
            var1.headers = new LinkedList();
            Iterator var2 = this.headers.iterator();

            while(var2.hasNext()) {
                SIPHeader var3 = (SIPHeader)var2.next();
                var1.attachHeader((SIPHeader)var3.clone());
            }
        }

        if (this.messageContentBytes != null) {
            var1.messageContentBytes = (byte[])((byte[])this.messageContentBytes.clone());
        }

        if (this.messageContentObject != null) {
            var1.messageContentObject = makeClone(this.messageContentObject);
        }

        return var1;
    }

    public String debugDump() {
        this.stringRepresentation = "";
        this.sprint("SIPMessage:");
        this.sprint("{");

        try {
            Field[] var1 = this.getClass().getDeclaredFields();

            for(int var2 = 0; var2 < var1.length; ++var2) {
                Field var3 = var1[var2];
                Class var4 = var3.getType();
                String var5 = var3.getName();
                if (var3.get(this) != null && Class.forName("gov.nist.javax.sip.header.SIPHeader").isAssignableFrom(var4) && var5.compareTo("headers") != 0) {
                    this.sprint(var5 + "=");
                    this.sprint(((SIPHeader)var3.get(this)).debugDump());
                }
            }
        } catch (Exception var6) {
            InternalErrorHandler.handleException(var6);
        }

        this.sprint("List of headers : ");
        this.sprint(this.headers.toString());
        this.sprint("messageContent = ");
        this.sprint("{");
        this.sprint(this.messageContent);
        this.sprint("}");
        if (this.getContent() != null) {
            this.sprint(this.getContent().toString());
        }

        this.sprint("}");
        return this.stringRepresentation;
    }

    private void attachHeader(SIPHeader var1) {
        if (var1 == null) {
            throw new IllegalArgumentException("null header!");
        } else {
            try {
                if (var1 instanceof SIPHeaderList) {
                    SIPHeaderList var2 = (SIPHeaderList)var1;
                    if (var2.isEmpty()) {
                        return;
                    }
                }

                this.attachHeader(var1, false, false);
            } catch (SIPDuplicateHeaderException var3) {
            }

        }
    }

    public void setHeader(Header var1) {
        SIPHeader var2 = (SIPHeader)var1;
        if (var2 == null) {
            throw new IllegalArgumentException("null header!");
        } else {
            try {
                if (var2 instanceof SIPHeaderList) {
                    SIPHeaderList var3 = (SIPHeaderList)var2;
                    if (var3.isEmpty()) {
                        return;
                    }
                }

                this.removeHeader(var2.getHeaderName());
                this.attachHeader(var2, true, false);
            } catch (SIPDuplicateHeaderException var4) {
                InternalErrorHandler.handleException(var4);
            }

        }
    }

    public void attachHeader(SIPHeader var1, boolean var2) throws SIPDuplicateHeaderException {
        this.attachHeader(var1, var2, false);
    }

    public void attachHeader(SIPHeader var1, boolean var2, boolean var3) throws SIPDuplicateHeaderException {
        if (var1 == null) {
            throw new NullPointerException("null header");
        } else {
            Object var4;
            if (ListMap.hasList(var1) && !SIPHeaderList.class.isAssignableFrom(var1.getClass())) {
                SIPHeaderList var5 = ListMap.getList(var1);
                var5.add(var1);
                var4 = var5;
            } else {
                var4 = var1;
            }

            String var10 = SIPHeaderNamesCache.toLowerCase(((SIPHeader)var4).getName());
            if (var2) {
                this.nameTable.remove(var10);
            } else if (this.nameTable.containsKey(var10) && !(var4 instanceof SIPHeaderList)) {
                if (var4 instanceof ContentLength) {
                    try {
                        ContentLength var11 = (ContentLength)var4;
                        this.contentLengthHeader.setContentLength(var11.getContentLength());
                    } catch (InvalidArgumentException var9) {
                    }
                }

                return;
            }

            SIPHeader var6 = (SIPHeader)this.getHeader(var1.getName());
            if (var6 != null) {
                ListIterator var7 = this.headers.listIterator();

                while(var7.hasNext()) {
                    SIPHeader var8 = (SIPHeader)var7.next();
                    if (var8.equals(var6)) {
                        var7.remove();
                    }
                }
            }

            if (!this.nameTable.containsKey(var10)) {
                this.nameTable.put(var10, var4);
                this.headers.add(var4);
            } else if (var4 instanceof SIPHeaderList) {
                SIPHeaderList var12 = (SIPHeaderList)this.nameTable.get(var10);
                if (var12 != null) {
                    var12.concatenate((SIPHeaderList)var4, var3);
                } else {
                    this.nameTable.put(var10, var4);
                }
            } else {
                this.nameTable.put(var10, var4);
            }

            if (var4 instanceof From) {
                this.fromHeader = (From)var4;
            } else if (var4 instanceof ContentLength) {
                this.contentLengthHeader = (ContentLength)var4;
            } else if (var4 instanceof To) {
                this.toHeader = (To)var4;
            } else if (var4 instanceof CSeq) {
                this.cSeqHeader = (CSeq)var4;
            } else if (var4 instanceof CallID) {
                this.callIdHeader = (CallID)var4;
            } else if (var4 instanceof MaxForwards) {
                this.maxForwardsHeader = (MaxForwards)var4;
            }

        }
    }

    public void removeHeader(String var1, boolean var2) {
        String var3 = SIPHeaderNamesCache.toLowerCase(var1);
        SIPHeader var4 = (SIPHeader)this.nameTable.get(var3);
        if (var4 != null) {
            if (var4 instanceof SIPHeaderList) {
                SIPHeaderList var5 = (SIPHeaderList)var4;
                if (var2) {
                    var5.removeFirst();
                } else {
                    var5.removeLast();
                }

                if (var5.isEmpty()) {
                    ListIterator var6 = this.headers.listIterator();

                    while(var6.hasNext()) {
                        SIPHeader var7 = (SIPHeader)var6.next();
                        if (var7.getName().equalsIgnoreCase(var3)) {
                            var6.remove();
                        }
                    }

                    this.nameTable.remove(var3);
                }
            } else {
                this.nameTable.remove(var3);
                if (var4 instanceof From) {
                    this.fromHeader = null;
                } else if (var4 instanceof To) {
                    this.toHeader = null;
                } else if (var4 instanceof CSeq) {
                    this.cSeqHeader = null;
                } else if (var4 instanceof CallID) {
                    this.callIdHeader = null;
                } else if (var4 instanceof MaxForwards) {
                    this.maxForwardsHeader = null;
                } else if (var4 instanceof ContentLength) {
                    this.contentLengthHeader = null;
                }

                ListIterator var8 = this.headers.listIterator();

                while(var8.hasNext()) {
                    SIPHeader var9 = (SIPHeader)var8.next();
                    if (var9.getName().equalsIgnoreCase(var1)) {
                        var8.remove();
                    }
                }
            }

        }
    }

    public void removeHeader(String var1) {
        if (var1 == null) {
            throw new NullPointerException("null arg");
        } else {
            String var2 = SIPHeaderNamesCache.toLowerCase(var1);
            SIPHeader var3 = (SIPHeader)this.nameTable.remove(var2);
            if (var3 != null) {
                if (var3 instanceof From) {
                    this.fromHeader = null;
                } else if (var3 instanceof To) {
                    this.toHeader = null;
                } else if (var3 instanceof CSeq) {
                    this.cSeqHeader = null;
                } else if (var3 instanceof CallID) {
                    this.callIdHeader = null;
                } else if (var3 instanceof MaxForwards) {
                    this.maxForwardsHeader = null;
                } else if (var3 instanceof ContentLength) {
                    this.contentLengthHeader = null;
                }

                ListIterator var4 = this.headers.listIterator();

                while(var4.hasNext()) {
                    SIPHeader var5 = (SIPHeader)var4.next();
                    if (var5.getName().equalsIgnoreCase(var2)) {
                        var4.remove();
                    }
                }

            }
        }
    }

    public String getTransactionId() {
        Via var1 = null;
        if (!this.getViaHeaders().isEmpty()) {
            var1 = (Via)this.getViaHeaders().getFirst();
        }

        if (var1.getBranch() != null && var1.getBranch().toUpperCase().startsWith("Z9HG4BK")) {
            return this.getCSeq().getMethod().equals("CANCEL") ? (var1.getBranch() + ":" + this.getCSeq().getMethod()).toLowerCase() : var1.getBranch().toLowerCase();
        } else {
            StringBuffer var2 = new StringBuffer();
            From var3 = (From)this.getFrom();
            To var4 = (To)this.getTo();
            if (var3.hasTag()) {
                var2.append(var3.getTag()).append(":");
            }

            String var5 = this.callIdHeader.getCallId();
            var2.append(var5).append(":");
            var2.append(this.cSeqHeader.getSequenceNumber()).append(":").append(this.cSeqHeader.getMethod());
            if (var1 != null) {
                var2.append(":").append(var1.getSentBy().encode());
                if (!var1.getSentBy().hasPort()) {
                    var2.append(":").append(5060);
                }
            }

            if (this.getCSeq().getMethod().equals("CANCEL")) {
                var2.append("CANCEL");
            }

            return var2.toString().toLowerCase();
        }
    }

    public int hashCode() {
        if (this.callIdHeader == null) {
            throw new RuntimeException("Invalid message! Cannot compute hashcode! call-id header is missing !");
        } else {
            return this.callIdHeader.getCallId().hashCode();
        }
    }

    public boolean hasContent() {
        return this.messageContent != null || this.messageContentBytes != null;
    }

    public ListIterator getHeaders() {
        return this.headers.listIterator();
    }

    public void setHeaders(List var1) {
        ListIterator var2 = var1.listIterator();

        while(var2.hasNext()) {
            SIPHeader var3 = (SIPHeader)var2.next();

            try {
                this.attachHeader(var3, false);
            } catch (SIPDuplicateHeaderException var5) {
            }
        }

    }

    public Header getHeader(String var1) {
        return this.getHeaderLowerCase(SIPHeaderNamesCache.toLowerCase(var1));
    }

    private Header getHeaderLowerCase(String var1) {
        if (var1 == null) {
            throw new NullPointerException("bad name");
        } else {
            SIPHeader var2 = (SIPHeader)this.nameTable.get(var1);
            return var2 instanceof SIPHeaderList ? ((SIPHeaderList)var2).getFirst() : var2;
        }
    }

    public ContentType getContentTypeHeader() {
        return (ContentType)this.getHeaderLowerCase(CONTENT_TYPE_LOWERCASE);
    }

    public FromHeader getFrom() {
        return this.fromHeader;
    }

    public void setFrom(FromHeader var1) {
        this.setHeader((Header)var1);
    }

    public ErrorInfoList getErrorInfoHeaders() {
        return (ErrorInfoList)this.getSIPHeaderListLowerCase(ERROR_LOWERCASE);
    }

    public ContactList getContactHeaders() {
        return (ContactList)this.getSIPHeaderListLowerCase(CONTACT_LOWERCASE);
    }

    public Contact getContactHeader() {
        ContactList var1 = this.getContactHeaders();
        return var1 != null ? (Contact)var1.getFirst() : null;
    }

    public ViaList getViaHeaders() {
        return (ViaList)this.getSIPHeaderListLowerCase(VIA_LOWERCASE);
    }

    public void setVia(List var1) {
        ViaList var2 = new ViaList();
        ListIterator var3 = var1.listIterator();

        while(var3.hasNext()) {
            Via var4 = (Via)var3.next();
            var2.add(var4);
        }

        this.setHeader((SIPHeaderList)var2);
    }

    public void setHeader(SIPHeaderList var1) {
        this.setHeader((Header)var1);
    }

    public Via getTopmostVia() {
        return this.getViaHeaders() == null ? null : (Via)((Via)this.getViaHeaders().getFirst());
    }

    public CSeqHeader getCSeq() {
        return this.cSeqHeader;
    }

    public void setCSeq(CSeqHeader var1) {
        this.setHeader((Header)var1);
    }

    public Authorization getAuthorization() {
        return (Authorization)this.getHeaderLowerCase(AUTHORIZATION_LOWERCASE);
    }

    public MaxForwardsHeader getMaxForwards() {
        return this.maxForwardsHeader;
    }

    public void setMaxForwards(MaxForwardsHeader var1) {
        this.setHeader((Header)var1);
    }

    public RouteList getRouteHeaders() {
        return (RouteList)this.getSIPHeaderListLowerCase(ROUTE_LOWERCASE);
    }

    public CallIdHeader getCallId() {
        return this.callIdHeader;
    }

    public void setCallId(CallIdHeader var1) {
        this.setHeader((Header)var1);
    }

    public void setCallId(String var1) throws ParseException {
        if (this.callIdHeader == null) {
            this.setHeader((Header)(new CallID()));
        }

        this.callIdHeader.setCallId(var1);
    }

    public RecordRouteList getRecordRouteHeaders() {
        return (RecordRouteList)this.getSIPHeaderListLowerCase(RECORDROUTE_LOWERCASE);
    }

    public ToHeader getTo() {
        return this.toHeader;
    }

    public void setTo(ToHeader var1) {
        this.setHeader((Header)var1);
    }

    public ContentLengthHeader getContentLength() {
        return this.contentLengthHeader;
    }

    public void setContentLength(ContentLengthHeader var1) {
        try {
            this.contentLengthHeader.setContentLength(var1.getContentLength());
        } catch (InvalidArgumentException var3) {
        }

    }

    public String getMessageContent() throws UnsupportedEncodingException {
        if (this.messageContent == null && this.messageContentBytes == null) {
            return null;
        } else {
            if (this.messageContent == null) {
                ContentType var1 = this.getContentTypeHeader();
                if (var1 != null) {
                    String var2 = var1.getCharset();
                    if (var2 != null) {
                        this.messageContent = new String(this.messageContentBytes, var2);
                    } else {
                        this.messageContent = new String(this.messageContentBytes, DEFAULT_ENCODING);
                    }
                } else {
                    this.messageContent = new String(this.messageContentBytes, DEFAULT_ENCODING);
                }
            }

            return this.messageContent;
        }
    }

    public void setMessageContent(String var1) {
        this.computeContentLength(var1);
        this.messageContent = var1;
        this.messageContentBytes = null;
        this.messageContentObject = null;
    }

    public void setMessageContent(byte[] var1) {
        this.computeContentLength(var1);
        this.messageContentBytes = var1;
        this.messageContent = null;
        this.messageContentObject = null;
    }

    public byte[] getRawContent() {
        try {
            if (this.messageContent == null && this.messageContentBytes == null && this.messageContentObject == null) {
                return null;
            } else if (this.messageContentObject != null) {
                String var6 = this.messageContentObject.toString();
                ContentType var8 = this.getContentTypeHeader();
                byte[] var7;
                if (var8 != null) {
                    String var4 = var8.getCharset();
                    if (var4 != null) {
                        var7 = var6.getBytes(var4);
                    } else {
                        var7 = var6.getBytes(DEFAULT_ENCODING);
                    }
                } else {
                    var7 = var6.getBytes(DEFAULT_ENCODING);
                }

                return var7;
            } else if (this.messageContent != null) {
                ContentType var2 = this.getContentTypeHeader();
                byte[] var1;
                if (var2 != null) {
                    String var3 = var2.getCharset();
                    if (var3 != null) {
                        var1 = this.messageContent.getBytes(var3);
                    } else {
                        var1 = this.messageContent.getBytes(DEFAULT_ENCODING);
                    }
                } else {
                    var1 = this.messageContent.getBytes(DEFAULT_ENCODING);
                }

                return var1;
            } else {
                return this.messageContentBytes;
            }
        } catch (UnsupportedEncodingException var5) {
            InternalErrorHandler.handleException(var5);
            return null;
        }
    }

    public void setMessageContent(String var1, String var2, String var3) {
        if (var3 == null) {
            throw new IllegalArgumentException("messgeContent is null");
        } else {
            ContentType var4 = new ContentType(var1, var2);
            this.setHeader((Header)var4);
            this.messageContent = var3;
            this.messageContentBytes = null;
            this.messageContentObject = null;
            this.computeContentLength(var3);
        }
    }

    public void setContent(Object var1, ContentTypeHeader var2) throws ParseException {
        if (var1 == null) {
            throw new NullPointerException("null content");
        } else {
            this.setHeader((Header)var2);
            this.messageContent = null;
            this.messageContentBytes = null;
            this.messageContentObject = null;
            if (var1 instanceof String) {
                this.messageContent = (String)var1;
            } else if (var1 instanceof byte[]) {
                this.messageContentBytes = (byte[])((byte[])var1);
            } else {
                this.messageContentObject = var1;
            }

            this.computeContentLength(var1);
        }
    }

    public Object getContent() {
        if (this.messageContentObject != null) {
            return this.messageContentObject;
        } else if (this.messageContent != null) {
            return this.messageContent;
        } else {
            return this.messageContentBytes != null ? this.messageContentBytes : null;
        }
    }

    public void setMessageContent(String var1, String var2, byte[] var3) {
        ContentType var4 = new ContentType(var1, var2);
        this.setHeader((Header)var4);
        this.setMessageContent(var3);
        this.computeContentLength(var3);
    }

    private void computeContentLength(Object var1) {
        int var2 = 0;
        if (var1 != null) {
            if (var1 instanceof String) {
                String var3 = null;
                ContentType var4 = this.getContentTypeHeader();
                if (var4 != null) {
                    var3 = var4.getCharset();
                }

                if (var3 == null) {
                    var3 = DEFAULT_ENCODING;
                }

                try {
                    var2 = ((String)var1).getBytes(var3).length;
                } catch (UnsupportedEncodingException var7) {
                    InternalErrorHandler.handleException(var7);
                }
            } else if (var1 instanceof byte[]) {
                var2 = ((byte[])((byte[])var1)).length;
            } else {
                var2 = var1.toString().length();
            }
        }

        try {
            this.contentLengthHeader.setContentLength(var2);
        } catch (InvalidArgumentException var6) {
        }

    }

    public void removeContent() {
        this.messageContent = null;
        this.messageContentBytes = null;
        this.messageContentObject = null;

        try {
            this.contentLengthHeader.setContentLength(0);
        } catch (InvalidArgumentException var2) {
        }

    }

    public ListIterator getHeaders(String var1) {
        if (var1 == null) {
            throw new NullPointerException("null headerName");
        } else {
            SIPHeader var2 = (SIPHeader)this.nameTable.get(SIPHeaderNamesCache.toLowerCase(var1));
            if (var2 == null) {
                return (new LinkedList()).listIterator();
            } else {
                return (ListIterator)(var2 instanceof SIPHeaderList ? ((SIPHeaderList)var2).listIterator() : new HeaderIterator(this, var2));
            }
        }
    }

    public String getHeaderAsFormattedString(String var1) {
        String var2 = var1.toLowerCase();
        return this.nameTable.containsKey(var2) ? this.nameTable.get(var2).toString() : this.getHeader(var1).toString();
    }

    private SIPHeaderList getSIPHeaderListLowerCase(String var1) {
        return (SIPHeaderList)this.nameTable.get(var1);
    }

    private LinkedList getHeaderList(String var1) {
        SIPHeader var2 = (SIPHeader)this.nameTable.get(SIPHeaderNamesCache.toLowerCase(var1));
        if (var2 == null) {
            return null;
        } else if (var2 instanceof SIPHeaderList) {
            return (LinkedList)((LinkedList)((SIPHeaderList)var2).getHeaderList());
        } else {
            LinkedList var3 = new LinkedList();
            var3.add(var2);
            return var3;
        }
    }

    public boolean hasHeader(String var1) {
        return this.nameTable.containsKey(SIPHeaderNamesCache.toLowerCase(var1));
    }

    public boolean hasFromTag() {
        return this.fromHeader != null && this.fromHeader.getTag() != null;
    }

    public boolean hasToTag() {
        return this.toHeader != null && this.toHeader.getTag() != null;
    }

    public String getFromTag() {
        return this.fromHeader == null ? null : this.fromHeader.getTag();
    }

    public void setFromTag(String var1) {
        try {
            this.fromHeader.setTag(var1);
        } catch (ParseException var3) {
        }

    }

    public String getToTag() {
        return this.toHeader == null ? null : this.toHeader.getTag();
    }

    public void setToTag(String var1) {
        try {
            this.toHeader.setTag(var1);
        } catch (ParseException var3) {
        }

    }

    public abstract String getFirstLine();

    public void addHeader(Header var1) {
        SIPHeader var2 = (SIPHeader)var1;

        try {
            if (var1 instanceof ViaHeader) {
                this.attachHeader(var2, false, true);
            } else {
                this.attachHeader(var2, false, false);
            }
        } catch (SIPDuplicateHeaderException var6) {
            try {
                if (var1 instanceof ContentLength) {
                    ContentLength var4 = (ContentLength)var1;
                    this.contentLengthHeader.setContentLength(var4.getContentLength());
                }
            } catch (InvalidArgumentException var5) {
            }
        }

    }

    public void addUnparsed(String var1) {
        this.unrecognizedHeaders.add(var1);
    }

    public void addHeader(String var1) {
        String var2 = var1.trim() + "\n";

        try {
            HeaderParser var3 = ParserFactory.createParser(var1);
            SIPHeader var4 = var3.parse();
            this.attachHeader(var4, false);
        } catch (ParseException var5) {
            this.unrecognizedHeaders.add(var2);
        }

    }

    public ListIterator getUnrecognizedHeaders() {
        return this.unrecognizedHeaders.listIterator();
    }

    public ListIterator getHeaderNames() {
        ListIterator var1 = this.headers.listIterator();
        LinkedList var2 = new LinkedList();

        while(var1.hasNext()) {
            SIPHeader var3 = (SIPHeader)var1.next();
            String var4 = var3.getName();
            var2.add(var4);
        }

        return var2.listIterator();
    }

    public boolean equals(Object var1) {
        if (!var1.getClass().equals(this.getClass())) {
            return false;
        } else {
            SIPMessage var2 = (SIPMessage)var1;
            Collection var3 = this.nameTable.values();
            Iterator var4 = var3.iterator();
            if (this.nameTable.size() != var2.nameTable.size()) {
                return false;
            } else {
                SIPHeader var5;
                SIPHeader var6;
                do {
                    if (!var4.hasNext()) {
                        return true;
                    }

                    var5 = (SIPHeader)var4.next();
                    var6 = (SIPHeader)((SIPHeader)var2.nameTable.get(SIPHeaderNamesCache.toLowerCase(var5.getName())));
                    if (var6 == null) {
                        return false;
                    }
                } while(var6.equals(var5));

                return false;
            }
        }
    }

    public ContentDispositionHeader getContentDisposition() {
        return (ContentDispositionHeader)this.getHeaderLowerCase(CONTENT_DISPOSITION_LOWERCASE);
    }

    public void setContentDisposition(ContentDispositionHeader var1) {
        this.setHeader((Header)var1);
    }

    public ContentEncodingHeader getContentEncoding() {
        return (ContentEncodingHeader)this.getHeaderLowerCase(CONTENT_ENCODING_LOWERCASE);
    }

    public void setContentEncoding(ContentEncodingHeader var1) {
        this.setHeader((Header)var1);
    }

    public ContentLanguageHeader getContentLanguage() {
        return (ContentLanguageHeader)this.getHeaderLowerCase(CONTENT_LANGUAGE_LOWERCASE);
    }

    public void setContentLanguage(ContentLanguageHeader var1) {
        this.setHeader((Header)var1);
    }

    public ExpiresHeader getExpires() {
        return (ExpiresHeader)this.getHeaderLowerCase(EXPIRES_LOWERCASE);
    }

    public void setExpires(ExpiresHeader var1) {
        this.setHeader((Header)var1);
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int var1) {
        this.size = var1;
    }

    public void addLast(Header var1) throws SipException, NullPointerException {
        if (var1 == null) {
            throw new NullPointerException("null arg!");
        } else {
            try {
                this.attachHeader((SIPHeader)var1, false, false);
            } catch (SIPDuplicateHeaderException var3) {
                throw new SipException("Cannot add header - header already exists");
            }
        }
    }

    public void addFirst(Header var1) throws SipException, NullPointerException {
        if (var1 == null) {
            throw new NullPointerException("null arg!");
        } else {
            try {
                this.attachHeader((SIPHeader)var1, false, true);
            } catch (SIPDuplicateHeaderException var3) {
                throw new SipException("Cannot add header - header already exists");
            }
        }
    }

    public void removeFirst(String var1) throws NullPointerException {
        if (var1 == null) {
            throw new NullPointerException("Null argument Provided!");
        } else {
            this.removeHeader(var1, true);
        }
    }

    public void removeLast(String var1) {
        if (var1 == null) {
            throw new NullPointerException("Null argument Provided!");
        } else {
            this.removeHeader(var1, false);
        }
    }

    public abstract String getSIPVersion();

    public abstract void setSIPVersion(String var1) throws ParseException;

    public abstract String toString();
}
