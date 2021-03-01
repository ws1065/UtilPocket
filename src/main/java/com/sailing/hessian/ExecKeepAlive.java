package com.sailing.hessian;


/**
 * @program: packetresend
 * @description: 心跳通道
 * @author: wangsw
 * @create: 2020-11-30 16:20
 */
public interface ExecKeepAlive {

    RespData keepAlive(NodeStat nodeStat);
    RespData keepAlives(NodeStat nodeStat, NodeStat otherNodeStat);

}