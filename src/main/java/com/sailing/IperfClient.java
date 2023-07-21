package com.sailing;

import com.alibaba.fastjson.JSONObject;
import com.sailing.zookeeper.NodeInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-05-28 21:46
 */
public class IperfClient {
    public static void main(String[] args) throws Exception {


        String cmd1 = "iperf -u -c 172.20.52.94 -t9999 -p" ;
                String cmd2 =" -i1 -b4M  > /dev/null  &";

        for (int i = Integer.parseInt(args[1]); i <= Integer.parseInt(args[2]); i++) {
            String shell = cmd1 + (i) + cmd2;
            System.out.println(shell);
            new Thread(()->{
                try {
                    SshExecuter.execSh(shell);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
        Thread.sleep(Integer.MAX_VALUE);


    }
    /**
     * @Description: 将集群化的配置数据传递到zk里面去
     * @Param:
     * @return: [zkIps, ip1, ip2, ip3, ip4, ip5, ip6]
     * @Author: wangsw
     * @date:
     */
    public static void saveDataToZk(String zkIps, String ip1, String ip2, String ip3, String ip4, String ip5, String ip6) {
        File file = new File("cluster-info.txt");
        if (file.exists()) {
            try {

                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                String contents;
                StringBuffer sb = new StringBuffer();
                while ((contents = reader.readLine()) != null) {
                    sb.append(contents);
                }
                String text = sb.toString();
                //String data = handleData(text);
                Map<String, String> object = JSONObject.parseObject(text, Map.class);
                for (Map.Entry<String, String> entry : object.entrySet()) {
                    //  handleData(zkIps, entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("ERROR FIle not fount");
        }
    }

    public void handleData(String data) {
        Map<String, String> sMap = new HashMap<>();

        Map<String, Object> map = JSONObject.parseObject(data, Map.class);
        Map<String, String> upPlatform = (Map<String, String>) map.get("upPlatform");
        Map<String, String> downPlatform = (Map<String, String>) map.get("downPlatform");
        List<NodeInfo> nodeInfos = (List<NodeInfo>) map.get("nodeInfos");
        String upHost = upPlatform.get("host");
        int upPort = Integer.parseInt(upPlatform.get("port"));
        String downHost = downPlatform.get("host");
        int downPort = Integer.parseInt(downPlatform.get("port"));


        Map channel1 = new HashMap();
        channel1.put("id", "C236BB555CA24B92885B2F790B6E7BFB");
        channel1.put("inOut", "down");
        //channel1.put("ips", Arrays.asList(ip1, ip2));
        channel1.put("name", "通道");
        ArrayList<Map<String, String>> list = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", "819349AED1AB41BF83ACB95B5E82541A");
        hashMap.put("listenerIp", "192.168.91.131");
        hashMap.put("listenerPort", "8000");
        hashMap.put("targetIp", "192.168.91.131");
        hashMap.put("targetPort", "8000");
        hashMap.put("type", "cb");
        list.add(hashMap);
        hashMap = new HashMap<>();
        hashMap.put("id", "8B7AB3F71E5143219A68E262C25CC95E");
        hashMap.put("listenerIp", "192.168.91.131");
        hashMap.put("listenerPort", "8000");
        hashMap.put("targetIp", "192.168.91.131");
        hashMap.put("targetPort", "8000");
        hashMap.put("type", "cb");
        list.add(hashMap);


        channel1.put("passageways", list);


        sMap.put("/vscg/config/resource_ClusterConfig/C236BB555CA24B92885B2F790B6E7BFB",
                JSONObject.toJSONString(channel1));


        Map downPlatforms = new HashMap();
        downPlatforms.put("createTime", "2021-04-06 11:57:53");
        downPlatforms.put("createUser", "admin");
        downPlatforms.put("id", "B05FAEE016ED41498D89C1E85FD736B7");
        downPlatforms.put("ip", downHost);
        downPlatforms.put("maxPort", 25535);
        downPlatforms.put("minPort", 12);
        downPlatforms.put("name", "下级平台");
        downPlatforms.put("number", "12");
        downPlatforms.put("port", downPort);
        downPlatforms.put("type", "down");
        sMap.put("/vscg/config/resource_PlatformConfig/B05FAEE016ED41498D89C1E85FD736B7"
                , JSONObject.toJSONString(downPlatforms));

        Map upPlatforms = new HashMap();
        upPlatforms.put("createTime", "2021-04-06 11:57:53");
        upPlatforms.put("createUser", "admin");
        upPlatforms.put("id", "5FDD88E5C93E434BBE8AB7AF07DEED37");
        upPlatforms.put("ip", upHost);
        upPlatforms.put("maxPort", 25535);
        upPlatforms.put("minPort", 1);
        upPlatforms.put("name", "FAST_3C7E");
        upPlatforms.put("number", "12");
        upPlatforms.put("port", upPort);
        upPlatforms.put("type", "up");
        sMap.put("/vscg/config/resource_PlatformConfig/5FDD88E5C93E434BBE8AB7AF07DEED37"
                , JSONObject.toJSONString(downPlatforms));


    }
}