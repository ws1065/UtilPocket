//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.javax.sip.header;

import gov.nist.core.Host;
import gov.nist.core.HostPort;
import gov.nist.core.NameValue;
import gov.nist.core.NameValueList;
import gov.nist.javax.sip.stack.HopImpl;

import javax.sip.InvalidArgumentException;
import javax.sip.address.Hop;
import javax.sip.header.ViaHeader;
import java.text.ParseException;
@SuppressWarnings("AlibabaAvoidUseTimer")
public class Via extends ParametersHeader implements ViaHeader {
    private static final long serialVersionUID = 5281728373401351378L;
    public static final String BRANCH = "branch";
    public static final String RECEIVED = "received";
    public static final String MADDR = "maddr";
    public static final String TTL = "ttl";
    public static final String RPORT = "rport";
    protected Protocol sentProtocol = new Protocol();
    protected HostPort sentBy;
    protected String comment;

    public Via() {
        super("Via");
    }

    public boolean equals(Object var1) {
        if(var1 == this) {
            return true;
        } else if(!(var1 instanceof ViaHeader)) {
            return false;
        } else {
            ViaHeader var2 = (ViaHeader)var1;
            return this.getProtocol().equalsIgnoreCase(var2.getProtocol()) && this.getTransport().equalsIgnoreCase(var2.getTransport()) && this.getHost().equalsIgnoreCase(var2.getHost()) && this.getPort() == var2.getPort() && this.equalParameters(var2);
        }
    }

    public String getProtocolVersion() {
        return this.sentProtocol == null?null:this.sentProtocol.getProtocolVersion();
    }

    public Protocol getSentProtocol() {
        return this.sentProtocol;
    }

    public HostPort getSentBy() {
        return this.sentBy;
    }

    public Hop getHop() {
        HopImpl var1 = new HopImpl(this.sentBy.getHost().getHostname(), this.sentBy.getPort(), this.sentProtocol.getTransport());
        return var1;
    }

    public NameValueList getViaParms() {
        return this.parameters;
    }

    public String getComment() {
        return this.comment;
    }

    public boolean hasPort() {
        return this.getSentBy().hasPort();
    }

    public boolean hasComment() {
        return this.comment != null;
    }

    public void removePort() {
        this.sentBy.removePort();
    }

    public void removeComment() {
        this.comment = null;
    }

    public void setProtocolVersion(String var1) {
        if(this.sentProtocol == null) {
            this.sentProtocol = new Protocol();
        }

        this.sentProtocol.setProtocolVersion(var1);
    }

    public void setHost(Host var1) {
        if(this.sentBy == null) {
            this.sentBy = new HostPort();
        }

        this.sentBy.setHost(var1);
    }

    public void setSentProtocol(Protocol var1) {
        this.sentProtocol = var1;
    }

    public void setSentBy(HostPort var1) {
        this.sentBy = var1;
    }

    public void setComment(String var1) {
        this.comment = var1;
    }

    protected String encodeBody() {
        return this.encodeBody(new StringBuffer()).toString();
    }

    protected StringBuffer encodeBody(StringBuffer var1) {
        this.sentProtocol.encode(var1);
        var1.append(" ");
        this.sentBy.encode(var1);
        if(!this.parameters.isEmpty()) {
            var1.append(";");
            this.parameters.encode(var1);
        }

        if(this.comment != null) {
            var1.append(" ").append("(").append(this.comment).append(")");
        }

        return var1;
    }

    public void setHost(String var1) throws ParseException {
        if(this.sentBy == null) {
            this.sentBy = new HostPort();
        }

        try {
            Host var2 = new Host(var1);
            this.sentBy.setHost(var2);
        } catch (Exception var3) {
            throw new NullPointerException(" host parameter is null");
        }
    }

    public String getHost() {
        if(this.sentBy == null) {
            return null;
        } else {
            Host var1 = this.sentBy.getHost();
            return var1 == null?null:var1.getHostname();
        }
    }

    public void setPort(int var1) throws InvalidArgumentException {
        if(var1 == -1 || var1 >= 1 && var1 <= '\uffff') {
            if(this.sentBy == null) {
                this.sentBy = new HostPort();
            }

            this.sentBy.setPort(var1);
        } else {
            throw new InvalidArgumentException("Port value out of range -1, [1..65535]");
        }
    }

    @Deprecated
    public void setRPort() {
        try {
            this.setParameter("rport", "");
        } catch (ParseException var2) {
            var2.printStackTrace();
        }

    }
    public void setRPort(String port) {
        try {
            this.setParameter("rport", port);
        } catch (ParseException var2) {
            var2.printStackTrace();
        }

    }

    public int getPort() {
        return this.sentBy == null?-1:this.sentBy.getPort();
    }

    public int getRPort() {
        String var1 = this.getParameter("rport");
        return var1 != null?(new Integer(var1)).intValue():-1;
    }

    public String getTransport() {
        return this.sentProtocol == null?null:this.sentProtocol.getTransport();
    }

    public void setTransport(String var1) throws ParseException {
        if(var1 == null) {
            throw new NullPointerException("JAIN-SIP Exception, Via, setTransport(), the transport parameter is null.");
        } else {
            if(this.sentProtocol == null) {
                this.sentProtocol = new Protocol();
            }

            this.sentProtocol.setTransport(var1);
        }
    }

    public String getProtocol() {
        return this.sentProtocol == null?null:this.sentProtocol.getProtocol();
    }

    public void setProtocol(String var1) throws ParseException {
        if(var1 == null) {
            throw new NullPointerException("JAIN-SIP Exception, Via, setProtocol(), the protocol parameter is null.");
        } else {
            if(this.sentProtocol == null) {
                this.sentProtocol = new Protocol();
            }

            this.sentProtocol.setProtocol(var1);
        }
    }

    public int getTTL() {
        int var1 = this.getParameterAsInt("ttl");
        return var1;
    }

    public void setTTL(int var1) throws InvalidArgumentException {
        if(var1 < 0 && var1 != -1) {
            throw new InvalidArgumentException("JAIN-SIP Exception, Via, setTTL(), the ttl parameter is < 0");
        } else {
            this.setParameter(new NameValue("ttl", new Integer(var1)));
        }
    }

    public String getMAddr() {
        return this.getParameter("maddr");
    }

    public void setMAddr(String var1) throws ParseException {
        if(var1 == null) {
            throw new NullPointerException("JAIN-SIP Exception, Via, setMAddr(), the mAddr parameter is null.");
        } else {
            Host var2 = new Host();
            var2.setAddress(var1);
            NameValue var3 = new NameValue("maddr", var2);
            this.setParameter(var3);
        }
    }

    public String getReceived() {
        return this.getParameter("received");
    }

    public void setReceived(String var1) throws ParseException {
        if(var1 == null) {
            throw new NullPointerException("JAIN-SIP Exception, Via, setReceived(), the received parameter is null.");
        } else {
            this.setParameter("received", var1);
        }
    }

    public String getBranch() {
        return this.getParameter("branch");
    }

    public void setBranch(String var1) throws ParseException {
        if(var1 != null && var1.length() != 0) {
            this.setParameter("branch", var1);
        } else {
            throw new NullPointerException("JAIN-SIP Exception, Via, setBranch(), the branch parameter is null or length 0.");
        }
    }

    public Object clone() {
        Via var1 = (Via)super.clone();
        if(this.sentProtocol != null) {
            var1.sentProtocol = (Protocol)this.sentProtocol.clone();
        }

        if(this.sentBy != null) {
            var1.sentBy = (HostPort)this.sentBy.clone();
        }

        return var1;
    }
}
