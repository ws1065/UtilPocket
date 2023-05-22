package com.sailing.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_MS_FORMAT;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2022-08-22 12:55
 */
public class LogFileFilter温州私有 {
    private static String timeREG = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}";
    private static String deviceIDReg = "\\w{20}";
    private static String callIDReg = "[\\w-]{36}";
    private static String bodyCallidReg = "Call-ID:\\ [\\w-]{36}";

    public static void main(String[] args) {
//        String pathname = "C:\\Users\\wangw\\Desktop\\Desktop";
        String pathname = "D:\\Documents\\WeChat Files\\wxid_lsur2emrh56942\\FileStorage\\File\\2022-09\\VSCG_TASK-20220309020841-UP_20220921.0";
        if (args.length!=0){
            pathname = args[1];
        }
        File folder = new File(pathname);


        List<File> up = new ArrayList<>();
        List<File> down = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (file.getAbsolutePath().contains("UP")) {
                up.add(file);
            } else if (file.getAbsolutePath().contains("DOWN")) {
                down.add(file);
            }
        }

        List<Entity> keepaliveEntities = new ArrayList<>();
        List<Entity> registerEntities = new ArrayList<>();
        List<Entity> inviteEntities = new ArrayList<>();
        Map<String,List<String>> downBodys = new HashMap<>();
        for (File file : down) {
            //读取文件每一行进行判断和过滤
            List<String> lines = FileUtil.readLines(file, "utf-8");
            StringBuffer currentBody = new StringBuffer();
            for (String line : lines) {
                //INVITE消息是：通过创建通道的请求，确认包含的Callid，然后在body中筛选对应的callid获得所有的INVITE消息信息。

                //信令消息信息抽取
                currentBody = msgBody(currentBody, downBodys, line);

                //通道请求消息抽取
                //inviteMsg( inviteEntities, line);
            }
        }
        resultDOWN(inviteEntities,downBodys);

        Map<String,List<String>> downBodysnew = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : downBodys.entrySet()) {
            String s = ((ArrayList) (entry.getValue())).toString();
            if (s.contains("INVITE") && s.contains("41.216.64.105")) {
                downBodysnew.put(entry.getKey(),entry.getValue());
            }
        }
        Map<String,List<String>>  upBodys = new HashMap<>();

        for (File file : up) {
            StringBuffer currentBody = new StringBuffer();
            List<String> lines = FileUtil.readLines(file, "utf-8");
            for (String line : lines) {
                //信令消息信息抽取
                currentBody = msgBody(currentBody, upBodys, line);
            }
        }
        Map<String,List<String>> upBodysnew = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : upBodys.entrySet()) {
            String s = ((ArrayList) (entry.getValue())).toString();
            if (s.contains("INVITE") && s.contains("41.216.64.105")) {
                upBodysnew.put(entry.getKey(),entry.getValue());
            }
        }
        for (Map.Entry<String, List<String>> entry : downBodysnew.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            for (int i = 0; i < value.size(); i++) {
                value.set(i," DOWN "+value.get(i));
            }
            if (upBodysnew.containsKey(key)){
                upBodysnew.get(key).addAll(value);
            }else {
                upBodysnew.put(key, value);
            }


        }



        for (Map.Entry<String, List<String>> listEntry : upBodysnew.entrySet()) {
            System.out.println(listEntry.getKey());
            for (String s : listEntry.getValue()) {
                s = s.replaceAll("\0","");
                s = s.replaceAll("RECEIVE DATA:","");
                s = s.replaceAll("\r\n","  ");
                System.out.println("             "+ s);
            }

        }



//        resultUP(inviteEntities, upBodys);


//        //输出日期排序
//        for (Entity entity : inviteEntities) {
//            if (entity.getUpBodys() !=null)
//                Collections.sort(entity.getUpBodys(), (o1, o2) -> compareTime(o1, o2));
//            if (entity.getDownBodys() !=null)
//                Collections.sort(entity.getDownBodys(), (o1, o2) -> compareTime(o1, o2));
//        }
//        //筛选所选择的设备ID
//        inviteEntities = inviteEntities.stream().filter(e -> {
//            if (deviceID.size()>0)
//                return  deviceID.contains(e.getDeviceID());
//            else
//                return true;
//        }).collect(Collectors.toList());
        System.out.println();
    }

    private static int compareTime(String o1, String o2) {
        Pattern compile = Pattern.compile(timeREG);
        Matcher matcher = compile.matcher(o1);
        if (matcher.find()) {
            String time1 = matcher.group();
            matcher = compile.matcher(o2);
            if (matcher.find()) {
                String time2 = matcher.group();
                return DateUtil.parse(time1).compareTo(DateUtil.parse(time2));
            }
        }
        return 0;
    }

    private static void resultUP(List<Entity> entities, Map<String, List<String>> bodys) {
        for (Entity entity : entities) {
            String callID = entity.getCallID();
            if (bodys.containsKey(callID)) {
                List<String> bodys1 = bodys.get(callID);
                if (entity.getUpBodys() == null) {
                    entity.setUpBodys(bodys1);
                }else {
                    entity.getUpBodys().addAll(bodys1);
                }
            }
        }
    }
    private static void resultDOWN(List<Entity> entities, Map<String, List<String>> bodys) {
        for (Entity entity : entities) {
            String callID = entity.getCallID();
            if (bodys.containsKey(callID)) {
                List<String> bodys1 = bodys.get(callID);
                if (entity.getDownBodys() == null) {
                    entity.setDownBodys(bodys1);
                }else {
                    entity.getDownBodys().addAll(bodys1);
                }
            }
        }
    }

    private static StringBuffer msgBody( StringBuffer currentBody, Map<String, List<String>> bodys, String line) {
        if (line.startsWith("[ INFO ]") || line.startsWith("[ WARN ]") || line.startsWith("[ ERROR]") || line.startsWith("[ DEBUG]") ) {
            String lastBody1 = currentBody.toString();
            if (lastBody1.contains("\r\n") && lastBody1.contains("sip") && lastBody1.contains("RECEIVE DATA")) {
                for (String s : lastBody1.split("#")) {


                    Pattern compile = Pattern.compile(bodyCallidReg);
                    Matcher matcher = compile.matcher(s);
                    if (matcher.find()) {
                        String callid = matcher.group();
                        callid = callid.split("Call-ID:")[1].trim();
                        if (bodys.containsKey(callid)) {
                            bodys.get(callid).add(s);
                        } else {
                            List<String> list = new ArrayList<>();
                            list.add(s);
                            bodys.put(callid, list);
                        }
                    }
                }
            }
            currentBody = new StringBuffer();

            currentBody.append(line +"\r\n");
        }else {
            currentBody.append(line +"\r\n");
        }
        return currentBody;
    }


    private static void inviteMsg( List<Entity> entities,  String line) {
        if (line.contains("[start] sgg:")) {
            Pattern compile = Pattern.compile(deviceIDReg);
            Matcher matcher = compile.matcher(line);
            String deviceID = null;
            String time = null;
            String callID = null;
            if (matcher.find()) {
                deviceID = matcher.group();
                if (deviceID == null){
                    System.out.println();
                }
                System.out.println(deviceID);
            }

            System.out.println(line);
            compile = Pattern.compile(timeREG);
            matcher = compile.matcher(line);
            if (matcher.find()) {
                time = matcher.group();
                System.out.println(time);
            }

            compile = Pattern.compile(callIDReg);
            matcher = compile.matcher(line);
            if (matcher.find()) {
                callID = matcher.group();
                System.out.println(callID);
            }
            if (deviceID == null) {
                System.out.println();
            }
            if (time !=null && callID != null) {
                Entity entity = new Entity();
                entity.setTime(DateUtil.parse(time,NORM_DATETIME_MS_FORMAT));
                entity.setCallID(callID);
                entity.setDeviceID(deviceID);
                entities.add(entity);
            }

        }
    }
}