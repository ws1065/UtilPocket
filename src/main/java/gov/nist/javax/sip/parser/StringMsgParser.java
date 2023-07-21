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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Slf4j
public class StringMsgParser {
    protected boolean readBody;
    private ParseExceptionListener parseExceptionListener;


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

    public SIPMessage parseSIPMessage(byte[] rawData) throws ParseException, UnsupportedEncodingException {
        if (rawData != null && rawData.length != 0) {
            int rawDataFlag = 0;
            try {
                while(rawData[rawDataFlag] < 32) {
                    ++rawDataFlag;
                    if (rawData.length -1 == rawDataFlag) {
                        log.warn("解析错误:"+new String(rawData));
                        return null;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException var12) {
                log.error("解析错误:"+new String(rawData),var12);
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
                    if (rawDataFlag != rawData.length)
                        while(rawData[rawDataFlag] != 13 && rawData[rawDataFlag] != 10) {
                            ++rawDataFlag;
                        }
                } catch (ArrayIndexOutOfBoundsException var11) {
                    log.error("解析错误:"+new String(rawData),var11);
                    return null;
                }
                int currentLineLength = rawDataFlag - lastLineDataFlag;

                try {
                    lineData = new String(rawData, lastLineDataFlag, currentLineLength, "gbk");
                } catch (UnsupportedEncodingException var10) {
                    log.error("解析错误:"+lineData+"|"+new String(rawData),var10);
                    return null;
                }

                lineData = this.trimEndOfLine(lineData);
                if (lineData.length() == 0) {
                    if (StringUtils.isNotBlank(lastLineData)){
                        try {
                            this.processHeader(lastLineData, sipMessage);
                        }catch (Exception e){
                            log.error("解析错误"+lastLineData+"|"+new String(rawData,"gb2312"));
                            return null;
                        }
                    }else {
                        log.error(lastLineData + "is Null:{}",new String(rawData));
                        return null;
                    }
                } else if (isFirstLineFlag) {
                    try {
                        sipMessage = this.processFirstLine(lineData);
                    }catch (Exception e){
                        log.error("解析错误"+lineData+"|"+new String(rawData,"gb2312"));
                        return null;
                    }
                } else {
                    char firstChar = lineData.charAt(0);
                    if (firstChar != '\t' && firstChar != ' ') {
                        if (lastLineData != null && sipMessage != null) {
                            try {
                                this.processHeader(lastLineData, sipMessage);
                            }catch (Exception e){
                                log.error("解析错误"+lastLineData+"|"+new String(rawData,"gb2312"));
                                return null;
                            }
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
                if (rawData.length > rawDataFlag) {
                    if (rawData[rawDataFlag] == 13 && rawData.length > rawDataFlag + 1 && rawData[rawDataFlag + 1] == 10) {
                        ++rawDataFlag;
                    }

                    ++rawDataFlag;
                    isFirstLineFlag = false;
                }
            } while(lineData.length() > 0);

            if (sipMessage == null) {
                throw new ParseException("Bad message", 0);
            } else {
                sipMessage.setSize(rawDataFlag);
                if (this.readBody && sipMessage.getContentLength() != null && sipMessage.getContentLength().getContentLength() != 0) {
                    lastLineDataFlag = rawData.length - rawDataFlag;
                    byte[] body = new byte[lastLineDataFlag];
                    System.arraycopy(rawData, rawDataFlag, body, 0, lastLineDataFlag);
                    sipMessage.setMessageContent(new String(body, "gbk"));
                    //sipMessage.setContentLength(new ContentLength(contentLength));
                }
                return sipMessage;
            }
        } else {
            return null;
        }
    }
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
                int contentLength = sipMessage.getContentLength().getContentLength();
                sipMessage.setMessageContent(var7);
                sipMessage.setContentLength(new ContentLength(contentLength));
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
                log.error("解析错误:|"+var1,var5);
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
                log.error("解析错误:"+var1,var4);
            }
        }

        return (SIPMessage)sipMessage;
    }

    private void processHeader(String lineData, SIPMessage sipMessage) throws ParseException {
        if (lineData != null && lineData.length() != 0) {
            HeaderParser headerParser = null;
            if (Lexer.getHeaderName(lineData).equals("Date") && Lexer.getHeaderValue(lineData).trim().matches("^\\d\\d\\d\\d-\\d\\d-\\d\\dT.*")) {
                DateFormat inFormat;
                if (Lexer.getHeaderValue(lineData).trim().matches("^\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d")) {
                    inFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                } else {
                    inFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                }
                DateFormat outFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss.S z", Locale.ENGLISH);
                try {
                    inFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    Date inDate = inFormat.parse(Lexer.getHeaderValue(lineData).replaceAll("T"," ").trim());
                    outFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    String gmtDate = outFormat.format(inDate);
                    lineData = "Date: "+gmtDate;
                } catch (ParseException e) {
                    log.error("sip解析中head解析出现问题,msg:{}", e.getMessage());
                }
            }else if (Lexer.getHeaderName(lineData).equals("Date") ){
                String value = Lexer.getHeaderValue(lineData).trim();
                DateFormat outFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss.S z", Locale.ENGLISH);
                outFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                lineData = "Date: "+outFormat.format(new Date(value));
            }
            try {
                headerParser = ParserFactory.createParser(lineData + "\n");
            } catch (ParseException var7) {
                throw var7;
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
                    throw var8;
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

    public RequestLine parseSIPRequestLine(String var1) throws ParseException {
        var1 = var1 + "\n";
        return (new RequestLineParser(var1)).parse();
    }

    public StatusLine parseSIPStatusLine(String var1) throws ParseException {
        var1 = var1 + "\n";
        return (new StatusLineParser(var1)).parse();
    }

}
