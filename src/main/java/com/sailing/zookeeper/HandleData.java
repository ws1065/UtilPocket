package com.sailing.zookeeper;

import com.alibaba.fastjson.JSONObject;
import com.sailing.dscg.common.Tools;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.*;

import static com.sailing.zookeeper.InsertHandleData.handleData;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-04-07 11:06
 */
public class HandleData {
    private static final Logger log = LoggerFactory.getLogger(HandleData.class);

    public static void start(){
        LogManager.getRootLogger().setLevel(Level.ERROR);
        Scanner scanner = new Scanner(System.in);
        System.out.println("please input zkIps");
        String zkIps = scanner.nextLine();

        while (true) {
            System.out.println("please select model(insert/insertbatch/query/querybatch)(input 'q' exit,No input check)");
            String model = scanner.nextLine();
            if ("q".equalsIgnoreCase(model)) break ;
            while (true) {
                switch (model){
                    case "insert":
                        System.out.println("please input path(input 'q' exit,No input check)");
                        String path = scanner.nextLine();
                        if ("q".equalsIgnoreCase(path)) break ;

                        System.out.println("please input content(input 'q' exit,No input check)");
                        String content = scanner.nextLine();
                        if ("q".equalsIgnoreCase(content)) break ;
                        handleData(zkIps, path, content);
                        break;
                    case "insertbatch":
                        System.out.println("please input batchJson path");
                        System.out.println("content eg: {\"/vscg/tmp\":\"{\\\"version\\\":\\\"V4.0\\\"}\"}");
                        content = scanner.nextLine();
                        if ("q".equalsIgnoreCase(content)) break ;
                        File file = new File(content);
                        if (file.exists()){
                            try {

                                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                                String contents;
                                StringBuffer sb = new StringBuffer();
                                while (( contents= reader.readLine())!=null){
                                    sb.append(contents);
                                }
                                Map<String,String> object = JSONObject.parseObject(sb.toString(), Map.class);
                                for (Map.Entry<String, String> entry : object.entrySet()) {
                                    handleData(zkIps, entry.getKey(), entry.getValue());
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }else {
                            System.out.println("文件路径不存在："+content);
                        }

                        break;
                    case "query":
                        System.out.println("please input path(input 'q' exit,No input check)");
                        String paths = scanner.nextLine();
                        if ("q".equalsIgnoreCase(paths)) break ;
                        System.out.println("please input keyword(input 'q' exit,No input check)");
                        String keyword = scanner.nextLine();
                        if ("q".equalsIgnoreCase(keyword)) break ;
                        queryData(zkIps, paths,keyword);
                        break;
                    case "querybatch":
                        System.out.println("please input path(input 'q' exit,No input check)");
                        paths = scanner.nextLine();
                        if ("q".equalsIgnoreCase(paths)) break ;
                        Map map = queryData(zkIps, paths);
                        System.out.println(JSONObject.toJSONString(map));
                        break;
                }


            }
        }
    }

    private static Map queryData(String zkIps, String path) {
        Map<String,String> node = new HashMap<>();
        try {
            ZookeeperClusterServer configServer = new ZookeeperClusterServer(zkIps);
            String s = configServer.get(path);
            node.put(path,s);
            List<String> nodeNames = configServer.getAllChildNodeName(path);
            for (String nodeName : nodeNames) {
                Map map = queryData(zkIps, path + "/" + nodeName);
                node.putAll(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return node;
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