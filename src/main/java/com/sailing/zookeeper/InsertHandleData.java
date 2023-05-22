package com.sailing.zookeeper;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-04-07 11:06
 */
public class InsertHandleData {
    private static final Logger log = LoggerFactory.getLogger(InsertHandleData.class);

    public static void start(String[] args){
        LogManager.getRootLogger().setLevel(Level.ERROR);
        System.out.println("please input zkIps");
        if (args.length==0) {
            log.error("请添加数据 path 数据");
            return;
        }
        handleData("127.0.0.1:2181", args[0], args[1]);
    }

    private static void queryData(String zkIps, String path,String keyword) {
        try {
            ZookeeperClusterServer configServer = new ZookeeperClusterServer(zkIps);
            String s = configServer.get(path);
            if (s.contains(keyword) || path.contains(keyword)){
                System.out.println(path);
                System.out.println(s);
                System.out.println("=====================");
            }
            List<String> nodeNames = configServer.getAllChildNodeName(path);
            for (String nodeName : nodeNames) {
                queryData(zkIps,path+"/"+nodeName,keyword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleData(String zkIps, String path, String content){
        try {
            ZookeeperClusterServer configServer = new ZookeeperClusterServer(zkIps);
            if (configServer.save(path, content)) {
                System.out.println("创建成功");
            }else {
                System.err.println("创建失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}