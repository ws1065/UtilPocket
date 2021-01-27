package com.sailing.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: packetresend
 * @description: 节点状态实体类
 * @author: wangsw
 * @create: 2020-11-30 14:25
 */
@Data
public class NodeStat implements Serializable {
    private boolean engine;
//    private double cpu;
//    private double mem;
//    private double disk;
//    private long netWorkIn;
//    private long netWorkOut;
    private boolean ips;
    private String upDown;
    private String masterBackup;

    void setFail(){
        engine = false;
    }

}