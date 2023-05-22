package com.sailing.zookeeper;


import com.alibaba.fastjson.JSONObject;
import com.sailing.dscg.entity.keepalive.CbStat;
import com.sailing.dscg.entity.keepalive.SggStat;
import com.sailing.dscg.zookeeper.Node;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ZookeeperConfigServer<T> {

    private String basePath = "/vscg/config/"; //配置节点基础路径
    private String zookeeperIps;
    private Integer zookeeperTimeout = 30000;

    public ZookeeperConfigServer(String zookeeperIps) {
        this.zookeeperIps = zookeeperIps;
    }

    private void getConnect(ZooKeeperBase zooKeeperBase) throws IOException, InterruptedException {
        zooKeeperBase.connect(zookeeperIps,zookeeperTimeout);
    }
    /**
     * 创建节点
     * @param id 节点ID
     * @param t 节点对象
     * @return 创建节点
     */
    public boolean create(String id,T t,Class<T> clazz) throws Exception {
        String nodeName = getNodeName(clazz);
        return create(nodeName,id,t);
    }

    /**
     * 创建节点
     * @param nodeName
     * @param id 节点ID
     * @param t 节点对象
     * @return 创建节点
     */
    public boolean create(String nodeName,String id,T t) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
            String confData = JSONObject.toJSONString(t);
            String path = basePath+nodeName;
            if(StringUtils.isNotBlank(id)) path += "/"+id;
            return zooKeeperBase.createNode(path, confData.getBytes());
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }

    /**
     * 更新节点
     * @param id
     * @param t
     * @return 更新service配置
     */
    public boolean update(String id,T t,Class<T> clazz) throws Exception {
        String nodeName = getNodeName(clazz);
        return update(nodeName,id,t);
    }

    /**
     * 更新节点
     * @param nodeName 节点名称
     * @param id 节点ID
     * @param obj 数据对象
     * @return 更新service配置
     */
    public boolean update(String nodeName,String id,Object obj) throws Exception {
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
            String confData = JSONObject.toJSONString(obj);
            String path = basePath+nodeName;
            if(StringUtils.isNotBlank(id)) path += "/"+id;
            return zooKeeperBase.setData(path, confData.getBytes());
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }

    /**
     * @return 查询所有节点
     */
    public List<T> queryAll(Class<T> clazz) throws Exception{
        String nodeName = getNodeName(clazz);
        return queryAll(clazz,nodeName);
    }

    /**
     * @return 查询所有节点
     */
    public List<T> queryAll(Class<T> clazz,String nodeName) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
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
     * 获取单个对象
     * @param id
     * @param clazz
     * @return 根据serviceID查询配置信息
     */
    public T get(String id,Class<T> clazz) throws Exception{
        String nodeName = getNodeName(clazz);
        return get(id,nodeName,clazz);
    }

    /**
     * 获取单个对象
     * @param id
     * @param clazz
     * @return 根据serviceID查询配置信息
     */
    public T get(String id,String nodeName,Class<T> clazz) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
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
     * 获取单个对象
     * @param clazz
     * @return 根据serviceID查询配置信息
     */
    public T getByPath(String path,Class<T> clazz) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
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

    public boolean delNode(String id,Class<T> clazz) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
            String nodeName = getNodeName(clazz);
            String path = basePath+nodeName;
            if(StringUtils.isNotBlank(id)) path += "/"+id;
            return zooKeeperBase.delNode(path);
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
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
        if("".equals(nodeName)){
            throw new Exception("未找到Node注解名称！");
        }
        return nodeName;
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
            getConnect(zooKeeperBase);
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
     * 创建临时节点
     * @param path
     * @param sessionTimeout
     * @return
     */
    public boolean snatchZookeeperNode(String path,int sessionTimeout){
        ZooKeeperBase zooKeeperBase = null;
        String	createPath=null;
        try {
            zooKeeperBase = ZooKeeperBase.getInstance();
            getConnect(zooKeeperBase);
            createPath = zooKeeperBase.createTemporaryNode(path,"true");
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
                            Thread.sleep(sessionTimeout*1000);
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
     * 获取路径下所有子节点的名称
     * @param path
     * @return
     * @throws Exception
     */
    public List<String> getAllChildNodeName(String path)throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
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
     * 创建简单节点
     *
     * @return 创建节点
     */
    public boolean addNewNode(String path,String content)throws Exception {
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
            return zooKeeperBase.createNode(path, content.getBytes());
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
     * 查询节点中的数据
     *
     * @return
     */
    public String queryExistNode(String path)throws Exception {
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
            return zooKeeperBase.getDate(path);
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
     * 修改节点
     *
     * @return 创建节点
     */
    public boolean updateOldNode(String path,String content)throws Exception {
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
            return zooKeeperBase.setData(path, content.getBytes());
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
            getConnect(zooKeeperBase);
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
     * 存在更新，不存在新增
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
    public boolean addOrUpdateData(String path,String data)throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
            //存在执行更新操作，不存在执行新增操作
            if(zooKeeperBase.nodeExists(path)){
                return zooKeeperBase.setData(path,data.getBytes());
            }else {
                return zooKeeperBase.createNode(path,data.getBytes());
            }
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
            getConnect(zooKeeperBase);
            return zooKeeperBase.nodeExists(path);
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
     * 简单删除节点,加个判断节点是否存在的逻辑
     * @param path
     * @return
     */
    public boolean delNodeIfExists(String path)throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            getConnect(zooKeeperBase);
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
