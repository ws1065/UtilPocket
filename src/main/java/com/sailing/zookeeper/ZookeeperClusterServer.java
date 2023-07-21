package com.sailing.zookeeper;


import com.alibaba.fastjson.JSONObject;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.entity.keepalive.CbStat;
import com.sailing.dscg.entity.keepalive.SggStat;
import com.sailing.dscg.entity.synchronous.SynType;
import com.sailing.dscg.zookeeper.Node;
import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class ZookeeperClusterServer<T>   {


    private Integer zookeeperTimeout = 30000;
    private String zookeeperIps;

    public ZookeeperClusterServer( String zookeeperIps) {
        this.zookeeperIps = zookeeperIps;
    }

    private String basePath = "/vscg/cluster/";

    public static void main(String[] args) throws Exception {


        System.out.println(DateTool.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss.S"));
        new Thread(()->{System.out.println("TH "+DateTool.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss.S"));}).start();
        System.out.println(DateTool.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss.S"));
        ZookeeperClusterServer<SggStat> server = new ZookeeperClusterServer<>("172.20.52.70:2181");

//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
//        SggStat stat = new SggStat();
//        stat.setStat(true);
//        stat.setModifyTime("9624006700000");
//        server.save("172.20.52.70-192.168.20.70",stat,SggStat.class);
//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
//        System.out.println(server.get("172.20.52.70-192.168.20.70", SggStat.class).getModifyTime());
    }


    private void zkConnect(ZooKeeperBase zooKeeperBase) throws IOException, InterruptedException {
        if (!zooKeeperBase.isConnect())
            zooKeeperBase.connect(zookeeperIps,zookeeperTimeout);
    }
    /**
     * 新增或修改节点
     * @param id
     * @param t
     * @return 更新service配置
     */
    public boolean save(String id,T t,Class<T> clazz) throws Exception {
        String nodeName = getNodeName(clazz);
        return save(nodeName,id,t);
    }
    /**
     * 新增或修改节点
     * @param id
     * @param t
     * @return 更新service配置
     */
    public boolean saveTemp(String id,T t,Class<T> clazz) throws Exception {
        String nodeName = getNodeName(clazz);
        return save(nodeName,id,t);
    }
    /**
     * 新增或修改节点
     * @param path 全路径路径
     * @param content 内容
     * @return 更新service配置
     */
    public boolean save(String path,String content) throws Exception {
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            if(!zooKeeperBase.nodeExists(path)){
                return zooKeeperBase.createNode(path,content.getBytes());
            }else{
                return zooKeeperBase.setData(path, content.getBytes());
            }
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }
    /**
     * 新增或修改节点
     * @param nodeName 节点名称
     * @param id 节点ID
     * @param obj 数据对象
     * @return 更新service配置
     */
    public boolean save(String nodeName,String id,Object obj) throws Exception {
        boolean result = false;
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            String confData = JSONObject.toJSONString(obj);
            String path = basePath+nodeName;
            if(StringUtils.isNotBlank(id)) path += "/"+id;

            if(!zooKeeperBase.nodeExists(path)){
                result = zooKeeperBase.createNode(path, confData.getBytes());
            }else{
                result = zooKeeperBase.setData(path, confData.getBytes());
            }
            return result;
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }







    /**
     * 创建节点
     * @param nodeName
     * @param id 节点ID
     * @param t 节点对象
     * @return 创建节点
     */
    public boolean saveTemp(String nodeName,String id,T t) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        boolean result = false;
        try{
            zkConnect(zooKeeperBase);
            String confData = JSONObject.toJSONString(t);
            String path = basePath+nodeName;
            if(StringUtils.isNotBlank(id)) path += "/"+id;
            result = zooKeeperBase.createTempNode(path, confData.getBytes());
            return true;
        }catch (Exception e){
            if (zooKeeperBase != null) {
                zooKeeperBase.close();
            }
            throw e;
        }
    }
    /**
     * 创建临时节点
     * @param path
     * @param sessionTimeout
     * @return
     */
    public boolean saveTemp(String path,long sessionTimeout){
        return saveTemp(path, sessionTimeout,"true");
    }

    public boolean saveTemp(String path, long sessionTimeout,String content) {
        ZooKeeperBase zooKeeperBase = null;
        String	createPath=null;
        try {
            zooKeeperBase = ZooKeeperBase.getInstance();
            zkConnect(zooKeeperBase);
            createPath = zooKeeperBase.createTemporaryNode(path,content);
            if(StringUtils.isNotBlank(createPath)){
                return true;
            }
        }catch (Exception e){
            //报异常返回false
            return false;
        }finally {
            if(StringUtils.isBlank(createPath)){
                //为空时为失败，失败就立刻关闭zk连接
                try{
                    zooKeeperBase.close();
                }catch (Exception e){

                }
            }else {
                //zk自己无法超时，还是需要手动关闭，另开一个线程睡眠后手动关闭
                final ZooKeeperBase zooKeeperBase1=zooKeeperBase;
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(sessionTimeout *1000);
                            zooKeeperBase1.close();
                        }catch (Exception e){

                        }

                    }
                }.start();
            }

        }
        return false;
    }

    /**
     * 查询节点中的数据
     *
     * @return
     */
    public String getNodeData(String path)throws Exception {
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            return zooKeeperBase.getDate(path);
        }catch (Exception e){
            throw e;
        }finally {
            try {
                zooKeeperBase.close();
            } catch (Exception e) {
            }
        }
    }



    /**
     * @return 查询所有节点
     */
    public List<T> getAll(Class<T> clazz) throws Exception{
        String nodeName = getNodeName(clazz);
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            String path = basePath+nodeName;
            List<String> list = zooKeeperBase.getChildren(path);
            List<T> nodes = new ArrayList<>();
            if(list!=null && !list.isEmpty()){
                for (String config:list) {
                    String nodeConfig = zooKeeperBase.getDate(path+"/"+config);
                    T t1 = JSONObject.parseObject(nodeConfig,clazz);
                    nodes.add(t1);
                }
            }
            return nodes;
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }

    /**
     *  仅用于特定需求的查询
     *   传递basePath
     * @return 查询所有节点
     */
    public List<T> getAll(Class<T> clazz,String basePath) throws Exception{
        String nodeName = getNodeName(clazz);
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            String path = basePath+nodeName;
            List<String> list = zooKeeperBase.getChildren(path);
            List<T> nodes = new ArrayList<>();
            if(list!=null && !list.isEmpty()){
                for (String config:list) {
                    String nodeConfig = zooKeeperBase.getDate(path+"/"+config);
                    T t1 = JSONObject.parseObject(nodeConfig,clazz);
                    nodes.add(t1);
                }
            }
            return nodes;
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }
    /***
     * 获取子节点
     * @param path
     * @return
     * @throws Exception
     */
    public List<String> getChildren(String path) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            return zooKeeperBase.getChildren(path);
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }

    public List<String> getLastChildrenFullPath(String nodePath) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            return zooKeeperBase.getLastChildrenFullPath(nodePath);
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }


    /**
     * 获取单个对象
     * @param id
     * @param clazz
     * @return 根据serviceID查询配置信息
     */
    public T get(String id,Class<T> clazz) throws Exception{
        return get(id, clazz,basePath);
    }
    public T get(String id,Class<T> clazz,String basePath) throws Exception{
        String nodeName = getNodeName(clazz);
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            String path = basePath+nodeName;
            if(StringUtils.isNotBlank(id)) path += "/"+id;
            String config = zooKeeperBase.getDate(path);
            if(config!=null){
                T t = JSONObject.parseObject(config,clazz);
                return t;
            }
            return null;
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }

    /**
     * 根据路径获取内容
     * @param path
     * @return 返回节点内容
     */
    public String get(String path) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            return zooKeeperBase.getDate(path);
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }
//    /**
//     * 获取单个对象
//     * @param id
//     * @param clazz
//     * @return 根据serviceID查询配置信息
//     */
//    public T get(String id,Class<T> clazz) throws Exception{
//        String nodeName = getNodeName(clazz);
//        return get(id,nodeName,clazz);
//    }



    /**
     * 获取单个对象
     * @param clazz
     * @return 根据serviceID查询配置信息
     */
    public T getByPath(String path,Class<T> clazz) throws Exception{
        String nodeName = getNodeName(clazz);
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            String config = zooKeeperBase.getDate(path);
            if(config!=null){
                T t = JSONObject.parseObject(config,clazz);
                return t;
            }
            return null;
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }


    /**
     * 获取指定路径下所有的文件数据，返回map，
     * 其中key为文件全路径，value为文件里面的内容
     * @param path
     * @return
     */
    public Map<String,String> getPathMapData(String path)throws Exception{
        Map<String,String> data=new HashMap<>();
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            List<String> list=new ArrayList<>();
            getAllPath(zooKeeperBase,list,path);
            for(String node:list){
                String nodeConfig = zooKeeperBase.getDate(node);
                data.put(node,nodeConfig);
            }
            zooKeeperBase.close();
        }catch (Exception e){
            throw e;
        }
        return data;
    }

    /**
     * 遍历查询指定的文件目录下所有的文件路径
     * @param zooKeeperBase
     * @param allNode
     * @param path
     * @throws Exception
     */
    private  void getAllPath(ZooKeeperBase zooKeeperBase, List<String> allNode, String path)throws Exception{
        List<String> list = zooKeeperBase.getChildren(path);
        if(list==null||list.isEmpty()){
            allNode.add(path);
            return;
        }
        for(String s:list){
            String path1=path+"/"+s;
            getAllPath(zooKeeperBase,allNode,path1);
        }
    }



    /**
     * 获取路径下所有子节点的名称
     * @param path
     * @return
     * @throws Exception
     */
    public List<String> getAllChildNodeName(String path)throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            return zooKeeperBase.getChildren(path);
        }catch (Exception e){
            throw e;
        }finally {
            try {
                zooKeeperBase.close();
            }catch (Exception e){
            }
        }
    }


    /**
     * 简单删除节点
     * @param path
     * @return
     */
    public boolean delOldNode(String path)throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            return zooKeeperBase.delNode(path);
        }catch (Exception e){
            throw e;
        }finally {
            try {
                zooKeeperBase.close();
            }catch (Exception e){
            }
        }
    }


    /**
     * 判断节点是否存在
     * @param path
     * @return
     */
    public boolean nodeExists(String path)throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            return zooKeeperBase.nodeExists(basePath+path);
        }catch (Exception e){
            throw e;
        }finally {
            try {
                zooKeeperBase.close();
            }catch (Exception e){
            }
        }
    }
    /**
     * 获取类注解节点名称
     * @param clazz
     * @return
     * @throws Exception
     */
    private String getNodeName(Class<T> clazz) throws Exception {
        Node node = clazz.getAnnotation(Node.class);
        String nodeName = "";
        if(node!=null){
            nodeName = node.name();
        }
        if(StringUtils.isBlank(nodeName)){
            throw new Exception("未找到Node注解名称！");
        }
        return nodeName;
    }
    /**
     * 删除节点
     * @param id
     * @param clazz
     * @return
     * @throws Exception
     */
    public boolean delNode(String id,Class<T> clazz) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            String nodeName = getNodeName(clazz);
            zkConnect(zooKeeperBase);
            String path = basePath+nodeName+"/"+id;
            String content = get(path);
            return zooKeeperBase.delNode(path);
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }
    /**
     * 简单删除节点,加个判断节点是否存在的逻辑
     * @param path
     * @return
     */
    public boolean delNodeIfExists(String path)throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zkConnect(zooKeeperBase);
            if(zooKeeperBase.nodeExists(path)){
                return zooKeeperBase.delNode(path);
            }
            return true;
        }catch (Exception e){
            throw e;
        }finally {
            try {
                zooKeeperBase.close();
            }catch (Exception e){
            }
        }
    }
}
