package com.sailing.hessian;

import com.caucho.hessian.server.HessianServlet;
import com.sailing.dscg.entity.RespData;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-06-21 20:35
 */
public class HessianServerTest extends HessianServlet implements ExecKeepAlive {
    public static void main(String[] args) {

    }

    @Override
    public RespData keepAlive(NodeStat nodeStat) {
        RespData respData = new RespData();
        return respData;
    }

    @Override
    public RespData keepAlives(NodeStat nodeStat, NodeStat otherNodeStat) {
        return null;
    }

    @Override
    public String keepalived(int flag) {
        return null;
    }
}