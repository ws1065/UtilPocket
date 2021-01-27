//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.javax.sip.parser;

import gov.nist.core.Host;
import gov.nist.core.HostNameParser;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.GenericURI;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.address.TelephoneNumber;
import gov.nist.javax.sip.header.*;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import gov.nist.javax.sip.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

//对于SIP数据进行处理　ｐａｒｓｅｒ
@SuppressWarnings("AlibabaAvoidUseTimer")
public class StringMsgParser {
    protected boolean readBody;
    private ParseExceptionListener parseExceptionListener;
    private String rawStringMessage;
    private static Logger log = LoggerFactory.getLogger(StringMsgParser.class);

    public StringMsgParser() {
        this.readBody = true;
    }

    public StringMsgParser(ParseExceptionListener var1) {
        this();
        this.parseExceptionListener = var1;
    }

    public void setParseExceptionListener(ParseExceptionListener var1) {
        this.parseExceptionListener = var1;
    }

    public SIPMessage parseSIPMessage(byte[] rawData) throws ParseException {
        if (new String(rawData).contains("<FileSize>1872368</FileSize>")){
            System.out.println();
        }
        if (rawData != null && rawData.length != 0) {
            int rawDataFlag = 0;
            try {
                //去除空包字符
                //32	(space)
                while(rawData[rawDataFlag] < 32) {
                    ++rawDataFlag;
                }
            } catch (ArrayIndexOutOfBoundsException var12) {
                return null;
            }

            String lineData = null;
            String lastLineData = null;
            boolean isFirstLineFlag = true;
            SIPMessage sipMessage = null;

            int lastLineDataFlag;
            do {
                lastLineDataFlag = rawDataFlag;

                try {
                    //查找换行符
                    while(rawData[rawDataFlag] != 13 && rawData[rawDataFlag] != 10) {
                        ++rawDataFlag;
                    }
                } catch (ArrayIndexOutOfBoundsException var11) {
                    break;
                }
                int currentLineLength = rawDataFlag - lastLineDataFlag;

                try {
                    lineData = new String(rawData, lastLineDataFlag, currentLineLength, "UTF-8");
                } catch (UnsupportedEncodingException var10) {
                    throw new ParseException("Bad message encoding!", 0);
                }

                lineData = this.trimEndOfLine(lineData);
                if (lineData.length() == 0) {
                    if (lastLineData != null && sipMessage != null) {
                        this.processHeader(lastLineData, sipMessage);
                    }
                } else if (isFirstLineFlag) {
                    sipMessage = this.processFirstLine(lineData);
                } else {
                    char firstChar = lineData.charAt(0);
                    if (firstChar != '\t' && firstChar != ' ') {
                        if (lastLineData != null && sipMessage != null) {
                            this.processHeader(lastLineData, sipMessage);
                        }

                        lastLineData = lineData;
                    } else {
                        if (lastLineData == null) {
                            throw new ParseException("Bad header continuation.", 0);
                        }

                        lastLineData = lastLineData + lineData.substring(1);
                    }
                }
                //不超过rawData的边界  并且当前位和下一位都是换行符
                if (rawData[rawDataFlag] == 13 && rawData.length > rawDataFlag + 1 && rawData[rawDataFlag + 1] == 10) {
                    ++rawDataFlag;
                }

                ++rawDataFlag;
                isFirstLineFlag = false;
            } while(lineData.length() > 0);

            if (sipMessage == null) {
                throw new ParseException("Bad message", 0);
            } else {
                sipMessage.setSize(rawDataFlag);
                if (this.readBody && sipMessage.getContentLength() != null && sipMessage.getContentLength().getContentLength() != 0) {
                    lastLineDataFlag = rawData.length - rawDataFlag;
                    byte[] var13 = new byte[lastLineDataFlag];
                    System.arraycopy(rawData, rawDataFlag, var13, 0, lastLineDataFlag);
                    sipMessage.setMessageContent(var13);
                }

                return sipMessage;
            }
        } else {
            return null;
        }
    }
    //sip格式的SIP转化为SIPMessage对象
    public SIPMessage parseSIPMessage(String inStr) throws Exception {
            if (inStr != null && inStr.length() != 0) {
                int num = 0;
                int start = 0;

                try {
                    //过滤掉开头的空格
                    while(inStr.charAt(num) < ' ') {
                        ++num;
                    }
                } catch (Exception e) {
                    log.error("ERROR FOUND",e);
                    return null;
                }

                String line = null;
                String var4 = null;
                boolean booleans = true;
                SIPMessage sipMessage = null;

                do {
                    char var8;
                    try {
                        //获得下一行行末尾的位置
                        start = num;
                        for(var8 = inStr.charAt(num);var8 != '\r' && var8 != '\n'; var8 = inStr.charAt(num)) {
                            ++num;
                        }
                    } catch (Exception e) {
                        log.error("ERROR FOUND",e);
                        break;
                    }

                    //获得一行
                    line = inStr.substring(start, num);
                    line = trimEndOfLine(line);
                    //使用三个方法生成head
                    if (line.length() == 0) {
                        if (var4 != null) {
                            processHeader(var4, sipMessage);
                        }
                    } else if (booleans) {
                        sipMessage = processFirstLine(line);
                    } else {
                        var8 = line.charAt(0);
                        if (var8 != '\t' && var8 != ' ') {
                            var4 = line;
                            processHeader(var4, sipMessage);
                        } else {
                            if (var4 == null) {
                                throw new ParseException("Bad header continuation.", 0);
                            }
                            var4 = var4 + line.substring(1);
                        }
                    }
                    //处理结束的那一行的
                    if (inStr.charAt(num) == '\r' && inStr.length() > num + 1 && inStr.charAt(num + 1) == '\n') {
                        ++num;
                    }
                    ++num;
                    booleans = false;
                } while(line.length() > 0);

                sipMessage.setSize(num);
                if (this.readBody && sipMessage.getContentLength() != null && sipMessage.getContentLength().getContentLength() != 0) {
                    String var7 = inStr.substring(num);
                    sipMessage.setMessageContent(var7);
                }
                return sipMessage;
            }
            return null;
    }
    private String trimEndOfLine(String lineData) {
        if (lineData == null) {
            return lineData;
        } else {
            int endFlag;
            //要是末尾标识大于0(lineData有数据) 并且末尾标识是特殊字符
            //则缩小末尾标识
            for(endFlag = lineData.length() - 1; endFlag >= 0 && lineData.charAt(endFlag) <= ' '; --endFlag) {
                ;
            }
            //要是末尾标识没有变小 直接返回
            if (endFlag == lineData.length() - 1) {
                return lineData;
            //要是末尾标识变小,就需要去除尾部数据
            } else {
                return endFlag == -1 ? "" : lineData.substring(0, endFlag + 1);
            }
        }
    }

    private SIPMessage processFirstLine(String var1) throws ParseException {
        Object sipMessage;
        if (!var1.startsWith("SIP/2.0")) {
            sipMessage = new SIPRequest();

            try {
                RequestLine requestLineHead = (new RequestLineParser(var1 + "\n")).parse();
                ((SIPRequest)sipMessage).setRequestLine(requestLineHead);
            } catch (ParseException var5) {
                if (this.parseExceptionListener == null) {
                    throw var5;
                }

                this.parseExceptionListener.handleException(var5, (SIPMessage)sipMessage, RequestLine.class, var1, this.rawStringMessage);
            }
        } else {
            sipMessage = new SIPResponse();

            try {
                StatusLine statusLineHead = (new StatusLineParser(var1 + "\n")).parse();
                ((SIPResponse)sipMessage).setStatusLine(statusLineHead);
            } catch (ParseException var4) {
                if (this.parseExceptionListener == null) {
                    throw var4;
                }

                this.parseExceptionListener.handleException(var4, (SIPMessage)sipMessage, StatusLine.class, var1, this.rawStringMessage);
            }
        }

        return (SIPMessage)sipMessage;
    }

    private void processHeader(String lineData, SIPMessage sipMessage) throws ParseException {
        if (lineData != null && lineData.length() != 0) {
            HeaderParser headerParser = null;
            if (Lexer.getHeaderValue(lineData).trim().matches("^\\d\\d\\d\\d-\\d\\d-\\d\\dT.*")) {
                DateFormat inFormat;
                if (Lexer.getHeaderValue(lineData).trim().matches("^\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d")) {
                    inFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                } else {
                    inFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                }
                DateFormat outFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                try {
                    Date inDate = inFormat.parse(Lexer.getHeaderValue(lineData).replaceAll("T", " ").trim());
                    outFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    String gmtDate = outFormat.format(inDate);
                    lineData = "Date: " + gmtDate;
                } catch (ParseException e) {
                    log.error("sip解析中head解析出现问题,msg:{}", e.getMessage());
                }
            }
            try {
                headerParser = ParserFactory.createParser(lineData + "\n");
            } catch (ParseException var7) {
                this.parseExceptionListener.handleException(var7, sipMessage, (Class)null, lineData, this.rawStringMessage);
                return;
            }

            try {
                SIPHeader sipHeader = headerParser.parse();
                sipMessage.attachHeader(sipHeader, false);
            } catch (ParseException var8) {
                if (this.parseExceptionListener != null) {
                    String var5 = Lexer.getHeaderName(lineData);
                    Class var6 = NameMap.getClassFromName(var5);
                    if (var6 == null) {
                        var6 = ExtensionHeaderImpl.class;
                    }

                    this.parseExceptionListener.handleException(var8, sipMessage, var6, lineData, this.rawStringMessage);
                }
            }

        }
    }

    public AddressImpl parseAddress(String var1) throws ParseException {
        AddressParser var2 = new AddressParser(var1);
        return var2.address();
    }

    public Host parseHost(String var1) throws ParseException {
        Lexer var2 = new Lexer("charLexer", var1);
        return (new HostNameParser(var2)).host();
    }

    public TelephoneNumber parseTelephoneNumber(String var1) throws ParseException {
        return (new URLParser(var1)).parseTelephoneNumber();
    }

    public SipUri parseSIPUrl(String var1) throws ParseException {
        try {
            return (new URLParser(var1)).sipURL();
        } catch (ClassCastException var3) {
            throw new ParseException(var1 + " Not a SIP URL ", 0);
        }
    }

    public GenericURI parseUrl(String var1) throws ParseException {
        return (new URLParser(var1)).parse();
    }

    public SIPHeader parseSIPHeader(String var1) throws ParseException {
        int var2 = 0;
        int var3 = var1.length() - 1;

        try {
            while(var1.charAt(var2) <= ' ') {
                ++var2;
            }

            while(var1.charAt(var3) <= ' ') {
                --var3;
            }
        } catch (ArrayIndexOutOfBoundsException var9) {
            throw new ParseException("Empty header.", 0);
        }

        StringBuffer var4 = new StringBuffer(var3 + 1);
        int var5 = var2;
        int var6 = var2;

        for(boolean var7 = false; var5 <= var3; ++var5) {
            char var8 = var1.charAt(var5);
            if (var8 != '\r' && var8 != '\n') {
                if (var7) {
                    var7 = false;
                    if (var8 != ' ' && var8 != '\t') {
                        var6 = var5;
                    } else {
                        var4.append(' ');
                        var6 = var5 + 1;
                    }
                }
            } else if (!var7) {
                var4.append(var1.substring(var6, var5));
                var7 = true;
            }
        }

        var4.append(var1.substring(var6, var5));
        var4.append('\n');
        HeaderParser var10 = ParserFactory.createParser(var4.toString());
        if (var10 == null) {
            throw new ParseException("could not create parser", 0);
        } else {
            return var10.parse();
        }
    }

    public StatusLine parseSIPStatusLine(String var1) throws ParseException {
        var1 = var1 + "\n";
        return (new StatusLineParser(var1)).parse();
    }

    public static void aVoid(String[] var0) throws Exception {
        String[] var1 = new String[]{"SIP/2.0 200 OK\r\nTo: \"The Little Blister\" <sip:LittleGuy@there.com>;tag=469bc066\r\nFrom: \"The Master Blaster\" <sip:BigGuy@here.com>;tag=11\r\nVia: SIP/2.0/UDP 139.10.134.246:5060;branch=z9hG4bK8b0a86f6_1030c7d18e0_17;received=139.10.134.246\r\nCall-ID: 1030c7d18ae_a97b0b_b@8b0a86f6\r\nCSeq: 1 SUBSCRIBE\r\nContact: <sip:172.16.11.162:5070>\r\nContent-Length: 0\r\n\r\n", "SIP/2.0 180 Ringing\r\nVia: SIP/2.0/UDP 172.18.1.29:5060;branch=z9hG4bK43fc10fb4446d55fc5c8f969607991f4\r\nTo: \"0440\" <sip:0440@212.209.220.131>;tag=2600\r\nFrom: \"Andreas\" <sip:andreas@e-horizon.se>;tag=8524\r\nCall-ID: f51a1851c5f570606140f14c8eb64fd3@172.18.1.29\r\nCSeq: 1 INVITE\r\nMax-Forwards: 70\r\nRecord-Route: <sip:212.209.220.131:5060>\r\nContent-Length: 0\r\n\r\n", "REGISTER sip:nist.gov SIP/2.0\r\nVia: SIP/2.0/UDP 129.6.55.182:14826\r\nMax-Forwards: 70\r\nFrom: <sip:mranga@nist.gov>;tag=6fcd5c7ace8b4a45acf0f0cd539b168b;epid=0d4c418ddf\r\nTo: <sip:mranga@nist.gov>\r\nCall-ID: c5679907eb954a8da9f9dceb282d7230@129.6.55.182\r\nCSeq: 1 REGISTER\r\nContact: <sip:129.6.55.182:14826>;methods=\"INVITE, MESSAGE, INFO, SUBSCRIBE, OPTIONS, BYE, CANCEL, NOTIFY, ACK, REFER\"\r\nUser-Agent: RTC/(Microsoft RTC)\r\nEvent:  registration\r\nAllow-Events: presence\r\nContent-Length: 0\r\n\r\nINVITE sip:littleguy@there.com:5060 SIP/2.0\r\nVia: SIP/2.0/UDP 65.243.118.100:5050\r\nFrom: M. Ranganathan  <sip:M.Ranganathan@sipbakeoff.com>;tag=1234\r\nTo: \"littleguy@there.com\" <sip:littleguy@there.com:5060> \r\nCall-ID: Q2AboBsaGn9!?x6@sipbakeoff.com \r\nCSeq: 1 INVITE \r\nContent-Length: 247\r\n\r\nv=0\r\no=4855 13760799956958020 13760799956958020 IN IP4  129.6.55.78\r\ns=mysession session\r\np=+46 8 52018010\r\nc=IN IP4  129.6.55.78\r\nt=0 0\r\nm=audio 6022 RTP/AVP 0 4 18\r\na=rtpmap:0 PCMU/8000\r\na=rtpmap:4 G723/8000\r\na=rtpmap:18 G729A/8000\r\na=ptime:20\r\n"};

        for(int var2 = 0; var2 < 20; ++var2) {
            class ParserThread implements Runnable {
                String[] messages;

                public ParserThread(String[] var1) {
                    this.messages = var1;
                }

                public void run() {
                    for(int var1 = 0; var1 < this.messages.length; ++var1) {
                        StringMsgParser var2 = new StringMsgParser();

                        try {
                            SIPMessage var3 = var2.parseSIPMessage(this.messages[var1]);
                            System.out.println(" i = " + var1 + " branchId = " + var3.getTopmostVia().getBranch());
                        } catch (Exception e) {
                            log.error("error found",e);
                        }
                    }

                }
            }

            (new Thread(new ParserThread(var1))).start();
        }

    }
}
