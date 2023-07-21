//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package gov.nist.javax.sip.header;

import gov.nist.javax.sip.header.ParametersHeader;

import javax.sip.InvalidArgumentException;
import javax.sip.header.SubscriptionStateHeader;
import java.text.ParseException;

public class SubscriptionState extends ParametersHeader implements SubscriptionStateHeader {
    private static final long serialVersionUID = -6673833053927258745L;
    protected int expires = -1;
    protected int retryAfter = -1;
    protected String reasonCode;
    protected String state;

    public SubscriptionState() {
        super("Subscription-State");
    }

    public int getExpires() {
        return this.expires;
    }

    public void setExpires(int var1) throws InvalidArgumentException {
        if (var1 < 0) {
            throw new InvalidArgumentException("JAIN-SIP Exception, SubscriptionState, setExpires(), the expires parameter is  < 0");
        } else {
            this.expires = var1;
        }
    }

    public int getRetryAfter() {
        return this.retryAfter;
    }

    public void setRetryAfter(int var1) throws InvalidArgumentException {
            this.retryAfter = var1;
    }

    public String getReasonCode() {
        return this.reasonCode;
    }

    public void setReasonCode(String var1) throws ParseException {
        if (var1 == null) {
            throw new NullPointerException("JAIN-SIP Exception, SubscriptionState, setReasonCode(), the reasonCode parameter is null");
        } else {
            this.reasonCode = var1;
        }
    }

    public String getState() {
        return this.state;
    }

    public void setState(String var1) throws ParseException {
        if (var1 == null) {
            throw new NullPointerException("JAIN-SIP Exception, SubscriptionState, setState(), the state parameter is null");
        } else {
            this.state = var1;
        }
    }

    public String encodeBody() {
        return this.encodeBody(new StringBuffer()).toString();
    }

    protected StringBuffer encodeBody(StringBuffer var1) {
        if (this.state != null) {
            var1.append(this.state);
        }

        if (this.reasonCode != null) {
            var1.append(";reason=").append(this.reasonCode);
        }

        if (this.expires != -1) {
            var1.append(";expires=").append(this.expires);
        }

        if (this.retryAfter != -1) {
            var1.append(";retry-after=").append(this.retryAfter);
        }

        if (!this.parameters.isEmpty()) {
            var1.append(";");
            this.parameters.encode(var1);
        }

        return var1;
    }
}
