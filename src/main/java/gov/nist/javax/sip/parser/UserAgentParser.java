//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.javax.sip.parser;

import gov.nist.core.Token;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.UserAgent;

import java.text.ParseException;
@SuppressWarnings("AlibabaAvoidUseTimer")
public class UserAgentParser extends HeaderParser {
    public UserAgentParser(String var1) {
        super(var1);
    }

    protected UserAgentParser(Lexer var1) {
        super(var1);
    }

    public SIPHeader parse() throws ParseException {
        UserAgent var1 = new UserAgent();

        try {
            this.headerName(2065);
            if (this.lexer.lookAhead(0) == '\n') {
                throw this.createParseException("empty header");
            } else {
                for(; this.lexer.lookAhead(0) != '\n' && this.lexer.lookAhead(0) != 0; this.lexer.SPorHT()) {
                    String var2;
                    if (this.lexer.lookAhead(0) == '(') {
                        var2 = this.lexer.comment();
                        var1.addProductToken('(' + var2 + ')');
                    } else {
                        this.getLexer().SPorHT();
                        var2 = null;

                        Token var13;
                        try {
                            var13 = this.lexer.match(4095);
                        } catch (ParseException var11) {
                            throw this.createParseException("expected a product");
                        }

                        StringBuffer var3 = new StringBuffer(var13.getTokenValue());
                        //将if 修改为 while，因为原作者只考虑到了类似Softphone/Beta1.5情况，但是咱们为DVR/NVR/DVS，所以后面的NVR DVS在wile循环中处理
                        //另外在agent中有空格“ ”下为FOR循环处理，“/”字符在while中处理
                        //LexerCore.ttoken()为当前解析器的分词器，判断是否为a-z的字符，返回字符串
                        while (this.lexer.peekNextToken().getTokenType() == 47) {
                            this.lexer.match(47);
                            this.getLexer().SPorHT();
                            Token var4 = null;

                            try {
                                var4 = this.lexer.match(4095);
                            } catch (ParseException var10) {
                                throw this.createParseException("expected product-version");
                            }
                            var3.append("/");
                            var3.append(var4.getTokenValue());
                        }
                        while (this.lexer.peekNextToken().getTokenType() == 64) {
                            this.lexer.match(64);
                            this.getLexer().SPorHT();
                            Token var4 = null;

                            try {
                                var4 = this.lexer.match(4095);
                            } catch (ParseException var10) {
                                throw this.createParseException("expected product-version");
                            }
                            var3.append("@");
                            var3.append(var4.getTokenValue());
                        }
                        var1.addProductToken(var3.toString());
                    }
                }

                return var1;
            }
        } finally {
            ;
        }
    }

    public static void main(String[] var0) throws ParseException {
        String[] var1 = new String[]{"User-Agent: Softphone/Beta1.5 \n", "User-Agent:Nist/Beta1 (beta version) \n", "User-Agent: Nist UA (beta version)\n", "User-Agent: Nist1.0/Beta2 Ubi/vers.1.0 (very cool) \n"};
String agent = "User-Agent: Embedded Net DVR/NVR/DVS \n";
        String agent1 = "User-Agent: CNTRANS @ THE FORCE AI \n";
        UserAgentParser var3 = new UserAgentParser(agent1);
        UserAgent var4 = (UserAgent)var3.parse();
        System.out.println("encoded = " + var4.encode());
//        for(int var2 = 0; var2 < var1.length; ++var2) {
//            UserAgentParser var3 = new UserAgentParser(var1[var2]);
//            UserAgent var4 = (UserAgent)var3.parse();
//            System.out.println("encoded = " + var4.encode());
//        }

    }
}
