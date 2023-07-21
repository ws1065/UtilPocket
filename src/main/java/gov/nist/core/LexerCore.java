//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.core;

import gov.nist.core.StringTokenizer;
import gov.nist.core.Token;

import java.text.ParseException;
import java.util.Hashtable;
@SuppressWarnings("AlibabaAvoidUseTimer")
//增加@为不分词词项目
public class LexerCore extends StringTokenizer {
    public static final int START = 2048;
    public static final int END = 4096;
    public static final int ID = 4095;
    public static final int SAFE = 4094;
    public static final int WHITESPACE = 4097;
    public static final int DIGIT = 4098;
    public static final int ALPHA = 4099;
    public static final int BACKSLASH = 92;
    public static final int QUOTE = 39;
    public static final int AT = 64;
    public static final int SP = 32;
    public static final int HT = 9;
    public static final int COLON = 58;
    public static final int STAR = 42;
    public static final int DOLLAR = 36;
    public static final int PLUS = 43;
    public static final int POUND = 35;
    public static final int MINUS = 45;
    public static final int DOUBLEQUOTE = 34;
    public static final int TILDE = 126;
    public static final int BACK_QUOTE = 96;
    public static final int NULL = 0;
    public static final int EQUALS = 61;
    public static final int SEMICOLON = 59;
    public static final int SLASH = 47;
    public static final int L_SQUARE_BRACKET = 91;
    public static final int R_SQUARE_BRACKET = 93;
    public static final int R_CURLY = 125;
    public static final int L_CURLY = 123;
    public static final int HAT = 94;
    public static final int BAR = 124;
    public static final int DOT = 46;
    public static final int EXCLAMATION = 33;
    public static final int LPAREN = 40;
    public static final int RPAREN = 41;
    public static final int GREATER_THAN = 62;
    public static final int LESS_THAN = 60;
    public static final int PERCENT = 37;
    public static final int QUESTION = 63;
    public static final int AND = 38;
    public static final int UNDERSCORE = 95;
    protected static final Hashtable globalSymbolTable = new Hashtable();
    protected static final Hashtable lexerTables = new Hashtable();
    protected Hashtable currentLexer;
    protected String currentLexerName;
    protected Token currentMatch;
    static final char ALPHA_VALID_CHARS = '\uffff';
    static final char DIGIT_VALID_CHARS = '\ufffe';
    static final char ALPHADIGIT_VALID_CHARS = '�';

    protected void addKeyword(String var1, int var2) {
        Integer var3 = new Integer(var2);
        this.currentLexer.put(var1, var3);
        if(!globalSymbolTable.containsKey(var3)) {
            globalSymbolTable.put(var3, var1);
        }

    }

    public String lookupToken(int var1) {
        if (var1 > 2048) {
            return (String)globalSymbolTable.get(new Integer(var1));
        } else {
            Character var2 = new Character((char)var1);
            return var2.toString();
        }
    }

    protected Hashtable addLexer(String var1) {
        this.currentLexer = (Hashtable)lexerTables.get(var1);
        if (this.currentLexer == null) {
            this.currentLexer = new Hashtable();
            lexerTables.put(var1, this.currentLexer);
        }

        return this.currentLexer;
    }

    public void selectLexer(String var1) {
        this.currentLexerName = var1;
    }

    protected LexerCore() {
        this.currentLexer = new Hashtable();
        this.currentLexerName = "charLexer";
    }

    public LexerCore(String var1, String var2) {
        super(var2);
        this.currentLexerName = var1;
    }

    public String peekNextId() {
        int var1 = this.ptr;
        String var2 = this.ttoken();
        this.savedPtr = this.ptr;
        this.ptr = var1;
        return var2;
    }

    public String getNextId() {
        return this.ttoken();
    }

    public Token getNextToken() {
        return this.currentMatch;
    }

    public Token peekNextToken() throws ParseException {
        return this.peekNextToken(1)[0];
    }

    public Token[] peekNextToken(int var1) throws ParseException {
        int var2 = this.ptr;
        Token[] var3 = new Token[var1];

        for(int var4 = 0; var4 < var1; ++var4) {
            Token var5 = new Token();
            if (this.startsId()) {
                String var6 = this.ttoken();
                var5.tokenValue = var6;
                String var7 = var6.toUpperCase();
                if (this.currentLexer.containsKey(var7)) {
                    Integer var8 = (Integer)this.currentLexer.get(var7);
                    var5.tokenType = var8.intValue();
                } else {
                    var5.tokenType = 4095;
                }
            } else {
                char var9 = this.getNextChar();
                var5.tokenValue = String.valueOf(var9);
                if (isAlpha(var9)) {
                    var5.tokenType = 4099;
                } else if (isDigit(var9)) {
                    var5.tokenType = 4098;
                } else {
                    var5.tokenType = var9;
                }
            }

            var3[var4] = var5;
        }

        this.savedPtr = this.ptr;
        this.ptr = var2;
        return var3;
    }

    public Token match(int var1) throws ParseException {
        if (var1 > 2048 && var1 < 4096) {
            String var4;
            if (var1 == 4095) {
                if (!this.startsId()) {
                    throw new ParseException(this.buffer + "\nID expected", this.ptr);
                }

                var4 = this.getNextId();
                this.currentMatch = new Token();
                this.currentMatch.tokenValue = var4;
                this.currentMatch.tokenType = 4095;
            } else if (var1 == 4094) {
                if (!this.startsSafeToken()) {
                    throw new ParseException(this.buffer + "\nID expected", this.ptr);
                }

                var4 = this.ttokenSafe();
                this.currentMatch = new Token();
                this.currentMatch.tokenValue = var4;
                this.currentMatch.tokenType = 4094;
            } else {
                var4 = this.getNextId();
                Integer var5 = (Integer)this.currentLexer.get(var4.toUpperCase());
                if(var5 == null || var5.intValue() != var1) {
                    throw new ParseException(this.buffer + "\nUnexpected Token : " + var4, this.ptr);
                }

                this.currentMatch = new Token();
                this.currentMatch.tokenValue = var4;
                this.currentMatch.tokenType = var1;
            }
        } else {
            char var2;
            if (var1 > 4096) {
                var2 = this.lookAhead(0);
                if (var1 == 4098) {
                    if (!isDigit(var2)) {
                        throw new ParseException(this.buffer + "\nExpecting DIGIT", this.ptr);
                    }

                    this.currentMatch = new Token();
                    this.currentMatch.tokenValue = String.valueOf(var2);
                    this.currentMatch.tokenType = var1;
                    this.consume(1);
                } else if (var1 == 4099) {
                    if (!isAlpha(var2)) {
                        throw new ParseException(this.buffer + "\nExpecting ALPHA", this.ptr);
                    }

                    this.currentMatch = new Token();
                    this.currentMatch.tokenValue = String.valueOf(var2);
                    this.currentMatch.tokenType = var1;
                    this.consume(1);
                }
            } else {
                var2 = (char)var1;
                char var3 = this.lookAhead(0);
                if (var3 != var2) {
                    throw new ParseException(this.buffer + "\nExpecting  >>>" + var2 + "<<< got >>>" + var3 + "<<<", this.ptr);
                }

                this.consume(1);
            }
        }

        return this.currentMatch;
    }

    public void SPorHT() {
        try {
            for(char var1 = this.lookAhead(0); var1 == 32 || var1 == 9; var1 = this.lookAhead(0)) {
                this.consume(1);
            }
        } catch (ParseException var2) {
        }

    }

    public boolean startsId() {
        try {
            char var1 = this.lookAhead(0);
            if (isAlphaDigit(var1)) {
                return true;
            } else {
                switch(var1) {
                    //增加@为不分词词项目
                    case '@':
                    case '!':
                    case '%':
                    case '\'':
                    case '*':
                    case '+':
                    case '-':
                    case '.':
                    case '_':
                    case '`':
                    case '~':
                        return true;
                    default:
                        return false;
                }
            }
        } catch (ParseException var2) {
            return false;
        }
    }

    public boolean startsSafeToken() {
        try {
            char var1 = this.lookAhead(0);
            if (isAlphaDigit(var1)) {
                return true;
            } else {
                switch(var1) {
                case '!':
                case '"':
                case '#':
                case '$':
                case '\'':
                case '*':
                case '+':
                case '-':
                case '.':
                case '/':
                case ':':
                case ';':
                case '?':
                case '@':
                case '[':
                case ']':
                case '^':
                case '_':
                case '`':
                case '{':
                case '|':
                case '}':
                case '~':
                    return true;
                case '%':
                case '&':
                case '(':
                case ')':
                case ',':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '<':
                case '=':
                case '>':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '\\':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                default:
                    return false;
                }
            }
        } catch (ParseException var2) {
            return false;
        }
    }

    public String ttoken() {
        int var1 = this.ptr;

        try {
            while(this.hasMoreChars()) {
                char var2 = this.lookAhead(0);
                if (isAlphaDigit(var2)) {
                    this.consume(1);
                } else {
                    boolean var3 = false;
                    switch(var2) {
                        //增加‘@’为不分词词项
                        case '@':
                        case '!':
                        case '%':
                        case '\'':
                        case '*':
                        case '+':
                        case '-':
                        case '.':
                        case '_':
                        case '`':
                        case '~':
                            var3 = true;
                    }

                    if(!var3) {
                        break;
                    }

                    this.consume(1);
                }
            }

            return this.buffer.substring(var1, this.ptr);
        } catch (ParseException var4) {
            return null;
        }
    }

    public String ttokenAllowSpace() {
        int var1 = this.ptr;

        try {
            while(this.hasMoreChars()) {
                char var2 = this.lookAhead(0);
                if(isAlphaDigit(var2)) {
                    this.consume(1);
                } else {
                    boolean var3 = false;
                    switch(var2) {
                        case '\t':
                        case ' ':
                        case '!':
                        case '\'':
                        case '*':
                        case '+':
                        case '-':
                        case '.':
                        case '_':
                        case '`':
                        case '~':
                            var3 = true;
                    }

                    if(!var3) {
                        break;
                    }

                    this.consume(1);
                }
            }

            return this.buffer.substring(var1, this.ptr);
        } catch (ParseException var4) {
            return null;
        }
    }

    public String ttokenSafe() {
        int var1 = this.ptr;

        try {
            while(this.hasMoreChars()) {
                char var2 = this.lookAhead(0);
                if(isAlphaDigit(var2)) {
                    this.consume(1);
                } else {
                    boolean var3 = false;
                    switch(var2) {
                        case '!':
                        case '"':
                        case '#':
                        case '$':
                        case '\'':
                        case '*':
                        case '+':
                        case '-':
                        case '.':
                        case '/':
                        case ':':
                        case ';':
                        case '?':
                        case '@':
                        case '[':
                        case ']':
                        case '^':
                        case '_':
                        case '`':
                        case '{':
                        case '|':
                        case '}':
                        case '~':
                            var3 = true;
                        case '%':
                        case '&':
                        case '(':
                        case ')':
                        case ',':
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        case '<':
                        case '=':
                        case '>':
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                        case 'G':
                        case 'H':
                        case 'I':
                        case 'J':
                        case 'K':
                        case 'L':
                        case 'M':
                        case 'N':
                        case 'O':
                        case 'P':
                        case 'Q':
                        case 'R':
                        case 'S':
                        case 'T':
                        case 'U':
                        case 'V':
                        case 'W':
                        case 'X':
                        case 'Y':
                        case 'Z':
                        case '\\':
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                        case 'g':
                        case 'h':
                        case 'i':
                        case 'j':
                        case 'k':
                        case 'l':
                        case 'm':
                        case 'n':
                        case 'o':
                        case 'p':
                        case 'q':
                        case 'r':
                        case 's':
                        case 't':
                        case 'u':
                        case 'v':
                        case 'w':
                        case 'x':
                        case 'y':
                        case 'z':
                    }

                    if(!var3) {
                        break;
                    }

                    this.consume(1);
                }
            }

            return this.buffer.substring(var1, this.ptr);
        } catch (ParseException var4) {
            return null;
        }
    }

    public void consumeValidChars(char[] var1) {
        int var2 = var1.length;

        try {
            while(this.hasMoreChars()) {
                char var3 = this.lookAhead(0);
                boolean var4 = false;

                for(int var5 = 0; var5 < var2; ++var5) {
                    char var6 = var1[var5];
                    switch(var6) {
                        case '�':
                            var4 = isAlphaDigit(var3);
                            break;
                        case '\ufffe':
                            var4 = isDigit(var3);
                            break;
                        case '\uffff':
                            var4 = isAlpha(var3);
                            break;
                        default:
                            var4 = var3 == var6;
                    }

                    if(var4) {
                        break;
                    }
                }

                if(!var4) {
                    break;
                }

                this.consume(1);
            }
        } catch (ParseException var7) {
            ;
        }

    }

    public String quotedString() throws ParseException {
        int var1 = this.ptr + 1;
        if(this.lookAhead(0) != 34) {
            return null;
        } else {
            this.consume(1);

            while(true) {
                char var2 = this.getNextChar();
                if(var2 == 34) {
                    return this.buffer.substring(var1, this.ptr - 1);
                }

                if(var2 == 0) {
                    throw new ParseException(this.buffer + " :unexpected EOL", this.ptr);
                }

                if(var2 == 92) {
                    this.consume(1);
                }
            }
        }
    }

    public String comment() throws ParseException {
        StringBuffer var1 = new StringBuffer();
        if(this.lookAhead(0) != 40) {
            return null;
        } else {
            this.consume(1);

            while(true) {
                char var2 = this.getNextChar();
                if(var2 == 41) {
                    return var1.toString();
                }

                if(var2 == 0) {
                    throw new ParseException(this.buffer + " :unexpected EOL", this.ptr);
                }

                if(var2 == 92) {
                    var1.append(var2);
                    var2 = this.getNextChar();
                    if(var2 == 0) {
                        throw new ParseException(this.buffer + " : unexpected EOL", this.ptr);
                    }

                    var1.append(var2);
                } else {
                    var1.append(var2);
                }
            }
        }
    }

    public String byteStringNoSemicolon() {
        StringBuffer var1 = new StringBuffer();

        try {
            while(true) {
                char var2 = this.lookAhead(0);
                if(var2 == 0 || var2 == 10 || var2 == 59 || var2 == 44) {
                    return var1.toString();
                }

                this.consume(1);
                var1.append(var2);
            }
        } catch (ParseException var3) {
            return var1.toString();
        }
    }

    public String byteStringNoComma() {
        StringBuffer var1 = new StringBuffer();

        try {
            while(true) {
                char var2 = this.lookAhead(0);
                if(var2 == 10 || var2 == 44) {
                    break;
                }

                this.consume(1);
                var1.append(var2);
            }
        } catch (ParseException var3) {
            ;
        }

        return var1.toString();
    }

    public static String charAsString(char var0) {
        return String.valueOf(var0);
    }

    public String charAsString(int var1) {
        return this.buffer.substring(this.ptr, this.ptr + var1);
    }

    public String number() throws ParseException {
        int var1 = this.ptr;

        try {
            if(!isDigit(this.lookAhead(0))) {
                throw new ParseException(this.buffer + ": Unexpected token at " + this.lookAhead(0), this.ptr);
            } else {
                this.consume(1);

                while(true) {
                    char var2 = this.lookAhead(0);
                    if(!isDigit(var2)) {
                        return this.buffer.substring(var1, this.ptr);
                    }

                    this.consume(1);
                }
            }
        } catch (ParseException var3) {
            return this.buffer.substring(var1, this.ptr);
        }
    }

    public int markInputPosition() {
        return this.ptr;
    }

    public void rewindInputPosition(int var1) {
        this.ptr = var1;
    }

    public String getRest() {
        return this.ptr >= this.buffer.length()?null:this.buffer.substring(this.ptr);
    }

    public String getString(char var1) throws ParseException {
        StringBuffer var2 = new StringBuffer();

        while(true) {
            char var3 = this.lookAhead(0);
            if(var3 == 0) {
                throw new ParseException(this.buffer + "unexpected EOL", this.ptr);
            }

            if(var3 == var1) {
                this.consume(1);
                return var2.toString();
            }

            if(var3 == 92) {
                this.consume(1);
                char var4 = this.lookAhead(0);
                if(var4 == 0) {
                    throw new ParseException(this.buffer + "unexpected EOL", this.ptr);
                }

                this.consume(1);
                var2.append(var4);
            } else {
                this.consume(1);
                var2.append(var3);
            }
        }
    }

    public int getPtr() {
        return this.ptr;
    }

    public String getBuffer() {
        return this.buffer;
    }

    public ParseException createParseException() {
        return new ParseException(this.buffer, this.ptr);
    }
}
