package com.sailing;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @program: demo
 * @description: zk监听
 * @author: wangsw
 * @create: 2021-01-21 15:36
 */
@Slf4j
public class ZKWatch {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {





        // Watcher实例
        Watcher wh = new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println("回调watcher实例： 路径" + event.getPath() + " 类型："
                        + event.getType());
            }
        };
        ZooKeeper zk = new ZooKeeper("172.20.52.61:2181", 500000, wh);
        System.out.println("---------------------");
// 创建一个节点root，数据是mydata,不进行ACL权限控制，节点为永久性的(即客户端shutdown了也不会消失)
        zk.exists("/root", true);
        zk.create("/root", "mydata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
        System.out.println("---------------------");
// 在root下面创建一个childone znode,数据为childone,不进行ACL权限控制，节点为永久性的
        zk.exists("/root/childone", true);
        zk.create("/root/childone", "childone".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
        System.out.println("---------------------");
// 删除/root/childone这个节点，第二个参数为版本，－1的话直接删除，无视版本
        zk.exists("/root/childone", true);
        zk.delete("/root/childone", -1);
        System.out.println("---------------------");
        zk.exists("/root", true);
        zk.delete("/root", -1);
        System.out.println("---------------------");
// 关闭session
        zk.close();

    }
}