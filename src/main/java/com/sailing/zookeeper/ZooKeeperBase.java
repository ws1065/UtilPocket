package com.sailing.zookeeper;

import com.alibaba.fastjson.JSONObject;
import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ZooKeeperBase implements Watcher{

    private ZooKeeper zooKeeper = null;

    private static ZooKeeperBase zooKeeperBase;

    // CountDownLatch 用于停止（等待）主进程，直到客户端与ZooKeeper集合连接
    final static CountDownLatch connectedSignal = new CountDownLatch(1);

    /**
     * 获取Zookeeper连接实例
     * @return
     */
    public static ZooKeeperBase getInstance(){
        if (zooKeeperBase == null) {
            zooKeeperBase = new ZooKeeperBase();
            return zooKeeperBase;
        } else
            return zooKeeperBase;
    }


    public boolean isConnect() {
        if (zooKeeper!=null)
        return this.zooKeeper.getState().equals(ZooKeeper.States.CONNECTED);
        else
            return false;
    }
    /**
     * 获取zookeeper连接
     * @return
     * @throws IOException
     * @throws InterruptedException
     */

    public void connect(String host,Integer timeout) throws IOException, InterruptedException {
        System.setProperty("jute.maxbuffer",String.valueOf(10240000));
        this.zooKeeper = new ZooKeeper(host, timeout,this);
        if(!this.zooKeeper.getState().equals(ZooKeeper.States.CONNECTED)){
            while(true){
                if(this.zooKeeper.getState().equals(ZooKeeper.States.CONNECTED)){
                    break;
                }
            }
        }
        connectedSignal.await();
    }

    // 关闭zk
    public void close() throws InterruptedException {
        if(this.zooKeeper!=null){
            this.zooKeeper.close();
        }
    }

    /**
     * 判断节点是否存在
     * @param path
     * @return
     * @throws Exception
     */
    public Boolean nodeExists(String path) throws Exception {
        Stat stat = this.zooKeeper.exists(path, true);
        return stat == null ? false : true;
    }
    /**
     * 获取节点下的最终节点全路径集合
     * @param nodePath
     * @return
     */
    public List<String> getLastChildrenFullPath(String nodePath){
        List<String> paths = new ArrayList<>();
        try {
            List<String> nodePathList = getChildren(nodePath);
            if(nodePathList!=null && !nodePathList.isEmpty()){
                for (String path:nodePathList) {
                    List<String> childPaths = getLastChildrenFullPath(nodePath+"/"+path);
                    if(childPaths!=null && !childPaths.isEmpty()){
                        for (String childPath:childPaths) {
                            paths.add(path+"/"+childPath);
                        }
                    }else{
                        paths.add(path);
                    }
                }
            }
            return paths;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
    /**
     * 创建节点
     * @param path
     * @param data
     * @throws Exception
     */
    public Boolean createNode(String path, byte[] data) throws Exception {
        if(!this.nodeExists(path)){
            String listPath[] = path.split("/");
            String prePath = "";
            for(int i=1; i<listPath.length-1; i++){
                prePath = prePath + "/" + listPath[i];
                if(!this.nodeExists(prePath)){
                    this.zooKeeper.create(prePath,"".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
                }
            }
            this.zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 删除节点
     * @param path
     * @throws Exception
     */
    public Boolean delNode(String path) throws Exception {
        if(this.nodeExists(path)){
            this.zooKeeper.delete(path, -1);
            return true;
        }
        return false;
    }

    // 获取某节点内数据
    public String getDate(String path) throws Exception{
        if (this.nodeExists(path)){
            Stat stat = new Stat();
            String s = new String(this.zooKeeper.getData(path, null, stat));
            return s;
        }
        return null;
    }

    // 更新节点内数据
    public boolean setData(String path, byte[] data) throws Exception {
        if (this.nodeExists(path)) {
            this.zooKeeper.setData(path, data, -1);
            return true;
        }
        return false;
    }

    //更新节点json数据中某个值
    public boolean setJsonValue(String path, String key, String value) throws Exception{
        String data = getDate(path);

        if (data==null) {
            return false;
        }

        Map<String,String> map = JSONObject.parseObject(data,Map.class);
        map.put(key,value);
        return setData(path, JSONObject.toJSONString(map).getBytes());
    }

    //更新节点json数据中某几个值
    public boolean setJsonValues(String path, Map<String,Object> updateMap) throws Exception{
        String data = getDate(path);
        if (data==null) {
            return false;
        }
        Map<String,Object> map = JSONObject.parseObject(data,Map.class);
        map.putAll(updateMap);
        return setData(path, JSONObject.toJSONString(map).getBytes());
    }

    // 获取某节点的子节点,先判定该节点是否存在
    public List<String> getChildren(String path) throws Exception {
        if (this.nodeExists(path)){
            return this.zooKeeper.getChildren(path, false);
        }
        return null;
    }

    /**
     * 创建临时节点
     * @param path
     * @param value
     * @return
     * @throws Exception
     */
    public String createTemporaryNode(String path,String value)throws Exception {
        return this.zooKeeper.create(path, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    /**
     * 创建临时节点
     * @param path
     * @param data
     * @throws Exception
     */
    public Boolean createTempNode(String path, byte[] data) throws Exception {
        if(!this.nodeExists(path)){
            String listPath[] = path.split("/");
            String prePath = "";
            for(int i=1; i<listPath.length-1; i++){
                prePath = prePath + "/" + listPath[i];
                if(!this.nodeExists(prePath)){
                    this.zooKeeper.create(prePath,"".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
                }
            }
            this.zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == KeeperState.SyncConnected) {
            connectedSignal.countDown();
        }
    }
}
