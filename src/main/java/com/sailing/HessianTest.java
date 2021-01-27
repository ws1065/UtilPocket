package com.sailing;

import com.caucho.hessian.client.HessianProxyFactory;
import com.sailing.common.RespData;
import com.sailing.common.entity.NodeStat;
import com.sailing.heartBeat.ExecKeepAlive;

import java.net.MalformedURLException;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2020-12-25 19:13
 */
public class HessianTest {

    public static void run(){
        ExecKeepAlive execCommend = getExecCommend("192.168.56.99:28080");
        RespData respData = execCommend.keepAlive(new NodeStat());
        System.out.println(respData);
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
            factory.setReadTimeout(1000);
            factory.setConnectTimeout(1000);
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