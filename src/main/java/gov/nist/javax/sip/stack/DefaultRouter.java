//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gov.nist.javax.sip.stack;

import gov.nist.core.InternalErrorHandler;
import gov.nist.core.net.AddressResolver;
import gov.nist.javax.sip.SipStackImpl;
import gov.nist.javax.sip.address.GenericURI;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.RequestLine;
import gov.nist.javax.sip.header.Route;
import gov.nist.javax.sip.header.RouteList;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.stack.HopImpl;
import gov.nist.javax.sip.stack.SIPTransactionStack;

import javax.sip.SipException;
import javax.sip.SipStack;
import javax.sip.address.Hop;
import javax.sip.address.Router;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.message.Request;
import java.util.LinkedList;
import java.util.ListIterator;
@SuppressWarnings("AlibabaAvoidUseTimer")
public class DefaultRouter implements Router {
    private SipStackImpl sipStack;
    private Hop defaultRoute;

    private DefaultRouter() {
    }

    public DefaultRouter(SipStack var1, String var2) {
        this.sipStack = (SipStackImpl)var1;
        if(var2 != null) {
            try {
                this.defaultRoute = this.sipStack.getAddressResolver().resolveAddress(new HopImpl(var2));
            } catch (IllegalArgumentException var4) {
                ((SIPTransactionStack)var1).getLogWriter().logError("Invalid default route specification - need host:port/transport");
                throw var4;
            }
        }

    }

    public Hop getNextHop(Request request) throws SipException {
        SIPRequest sipRequest = (SIPRequest)request;
        RequestLine requestLine = sipRequest.getRequestLine();
        if(requestLine == null) {
            return this.defaultRoute;
        } else {
            GenericURI uri = requestLine.getUri();
            if(uri == null) {
                throw new IllegalArgumentException("Bad message: Null requestURI");
            } else {
                RouteList routeHeaders = sipRequest.getRouteHeaders();
                if(routeHeaders != null) {
                    Route route = (Route)routeHeaders.getFirst();
                    URI uri1 = route.getAddress().getURI();
                    if(uri1.isSipURI()) {
                        SipURI sipURI = (SipURI)uri1;
                        if(!sipURI.hasLrParam()) {
                            this.fixStrictRouting(sipRequest);
                            if(this.sipStack.isLoggingEnabled()) {
                                this.sipStack.logWriter.logDebug("Route post processing fixed strict routing");
                            }
                        }

                        Hop var9 = this.createHop(sipURI);
                        if(this.sipStack.isLoggingEnabled()) {
                            this.sipStack.logWriter.logDebug("NextHop based on Route:" + var9);
                        }

                        return var9;
                    } else {
                        throw new SipException("First Route not a SIP URI");
                    }
                } else {
                    Hop var6;
                    if(uri.isSipURI() && ((SipURI)uri).getMAddrParam() != null) {
                        var6 = this.createHop((SipURI)uri);
                        if(this.sipStack.isLoggingEnabled()) {
                            this.sipStack.logWriter.logDebug("Using request URI maddr to route the request = " + var6.toString());
                        }

                        return var6;
                    } else if(this.defaultRoute != null) {
                        if(this.sipStack.isLoggingEnabled()) {
                            this.sipStack.logWriter.logDebug("Using outbound proxy to route the request = " + this.defaultRoute.toString());
                        }

                        return this.defaultRoute;
                    } else if(!uri.isSipURI()) {
                        InternalErrorHandler.handleException("Unexpected non-sip URI", this.sipStack.logWriter);
                        return null;
                    } else {
                        var6 = this.createHop((SipURI)uri);
                        if(var6 != null && this.sipStack.isLoggingEnabled()) {
                            this.sipStack.logWriter.logDebug("Used request-URI for nextHop = " + var6.toString());
                        } else if(this.sipStack.isLoggingEnabled()) {
                            this.sipStack.logWriter.logDebug("returning null hop -- loop detected");
                        }

                        return var6;
                    }
                }
            }
        }
    }

    public void fixStrictRouting(SIPRequest var1) {
        RouteList var2 = var1.getRouteHeaders();
        Route var3 = (Route)var2.getFirst();
        SipUri var4 = (SipUri)var3.getAddress().getURI();
        //会使RequestLine变换成Route
//        var2.removeFirst();
//        AddressImpl var5 = new AddressImpl();
//        var5.setAddess(var1.getRequestURI());
//        Route var6 = new Route(var5);
//        var2.add(var6);
//        var1.setRequestURI(var4);
        if(this.sipStack.getLogWriter().isLoggingEnabled()) {
            this.sipStack.getLogWriter().logDebug("post: fixStrictRouting" + var1);
        }

    }

    private final Hop createHop(SipURI sipURI) {
        String transportParam = sipURI.isSecure()?"tls":sipURI.getTransportParam();
        if(transportParam == null) {
            transportParam = "udp";
        }

        int port;
        if(sipURI.getPort() != -1) {
            port = sipURI.getPort();
        } else if("tls".equalsIgnoreCase(transportParam)) {
            port = 5061;
        } else {
            port = 5060;
        }

        String host = sipURI.getMAddrParam() != null?sipURI.getMAddrParam():sipURI.getHost();
        AddressResolver addressResolver = this.sipStack.getAddressResolver();
        return addressResolver.resolveAddress(new HopImpl(host, port, transportParam));
    }

    public Hop getOutboundProxy() {
        return this.defaultRoute;
    }

    public ListIterator getNextHops(Request var1) {
        try {
            LinkedList var2 = new LinkedList();
            var2.add(this.getNextHop(var1));
            return var2.listIterator();
        } catch (SipException var3) {
            return null;
        }
    }
}
