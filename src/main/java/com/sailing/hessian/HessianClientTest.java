package com.sailing.hessian;

import com.caucho.hessian.client.HessianProxyFactory;

import java.net.MalformedURLException;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-12-25 19:13
 */
public class HessianClientTest {

    public static void main(String[] args) {
        run();
    }
    public static void run(){
        while (true) {
            ExecKeepAlive execCommend = getExecCommend("127.0.0.1:80");
            String respData = execCommend.keepalived(0);
            System.out.println(respData);
        }
    }
    public static ExecKeepAlive getExecCommend(String ip) {
        if (ip != null && !ip.isEmpty()) {
            String url = "";
            if (ip.indexOf(":") > 0) {
                url = "http://" + ip + "/SailingKeepAliveService";
            } else {
                url = "http://" + ip + ":8080/SailingKeepAliveService";
            }
            HessianProxyFactory factory = new HessianProxyFactory();
            factory.setReadTimeout(40000);
            factory.setConnectTimeout(40000);
            try {
                return (ExecKeepAlive) factory.create(ExecKeepAlive.class, url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 获取执行命令通信接口对象
     * @param ip
     * @return
     */
    public static ExecKeepAlive getExecSelfCommend(String ip) {
        if (ip != null && !ip.isEmpty()) {
            String url = "";
            if (ip.indexOf(":") > 0) {
                url = "http://" + ip + "/SailingKeepAliveService";
            } else {
                url = "http://" + ip + ":2233/SailingKeepAliveService";
            }
            HessianProxyFactory factory = new HessianProxyFactory();
            try {
                return (ExecKeepAlive) factory.create(ExecKeepAlive.class, url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}