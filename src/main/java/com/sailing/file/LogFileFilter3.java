package com.sailing.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_MS_FORMAT;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2022-08-22 12:55
 */
public class LogFileFilter3 {
    private static String timeREG = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}";
    private static String deviceIDReg = "\\w{20}";
    private static String callIDReg = "[\\w-]{36}";
    private static String bodyCallidReg = "Call-ID:\\ [\\w-]{36}";

    public static void main(String[] args) {
        File folder = new File(args[1]);


        List<File> up = new ArrayList<>();
        List<File> down = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (file.getAbsolutePath().contains("UP")) {
                up.add(file);
            } else if (file.getAbsolutePath().contains("DOWN")) {
                down.add(file);
            }
        }

        List<Entity> inviteEntities = new ArrayList<>();
        Map<String,List<String>> downCatalogBodys = new HashMap<>();
        Map<String,List<String>> upCatalogBodys = new HashMap<>();
        for (File file : up) {
            //读取文件每一行进行判断和过滤
            calc(upCatalogBodys, file);
        }
        for (File file : down) {
            //读取文件每一行进行判断和过滤
            calc(downCatalogBodys, file);
        }

        List<String> error1 = new ArrayList<>();
        List<String> error2 = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : upCatalogBodys.entrySet()) {
            if (downCatalogBodys.containsKey(entry.getKey())) {
                String key = entry.getKey();
                if (entry.getValue().size() != downCatalogBodys.get(key).size()) {
                    error1.add(entry.getKey());
                }
            }else {
                List<String> strings = upCatalogBodys.get(entry.getKey());

                if (strings !=null && !strings.toString().contains("MSG_START_FILE_QUERY_RESP"))
                    error2.add(entry.getKey());
            }
        }

        String sumnumReg = "SumNum>\\d+<\\/SumNum";
        String itemReg = "<\\/IE_RECORD_FILE>";

        Map<String,List<String>> errorCatalogBodys = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : downCatalogBodys.entrySet()) {
            List<String> strings = entry.getValue();
            if (strings !=null && strings.toString().contains("MSG_START_FILE_QUERY_RESP")){
                int sum = 0;
                int item = 0;
                if (entry.getKey().equals("FB1AA64A-F291-4737-A618-DDE65915476A")){
                    System.out.println();
                }
                for (String s : strings) {
                    Pattern compile = Pattern.compile(sumnumReg);
                    Matcher matcher = compile.matcher(s);
                    if (matcher.find()) {
                        String group = matcher.group();
                        sum = Integer.parseInt(group.substring(group.indexOf(">") + 1, group.indexOf("<")));
                    }
                     compile = Pattern.compile(itemReg);
                     matcher = compile.matcher(s);
                     int item1 = 0;
                    while (matcher.find()) {
                        item++;
                        item1++;
                    }
                }
                if (sum !=0 && item != sum) {


                    errorCatalogBodys.put(entry.getKey(), entry.getValue());
                }
            }
        }

        if (!errorCatalogBodys.isEmpty())
        {
            for (Map.Entry<String, List<String>> entry : errorCatalogBodys.entrySet()) {

                System.out.println(entry.getKey()+"");
                for (String s : entry.getValue()) {
                    System.out.println("             "+s);
                }

            }
        }

        System.out.println();
    }

    private static void calc(Map<String, List<String>> downReqBodys, File file) {
        List<String> lines = FileUtil.readLines(file, "utf-8");
        StringBuffer currentBody = new StringBuffer();
        boolean record = false;
        for (String line : lines) {
            String s = "c.s.s.p.TCPSipProcess [206]";
            String s1 = "c.s.s.p.TCPSipProcess [214]";
            //信令消息信息抽取
            if (line.contains(s) ||line.contains(s1)){
                record = true;
                currentBody = new StringBuffer();
                currentBody.append(line).append(System.lineSeparator());
            }else if (line.contains("#") && record){
                record = false;
                currentBody.append(line).append(System.lineSeparator());
                String e = currentBody.toString();

                Pattern compile = Pattern.compile(bodyCallidReg);
                Matcher matcher = compile.matcher(e);

                if (matcher.find()) {
                    String callid = matcher.group();
                    callid = callid.split("Call-ID:")[1].trim();
                    if (e.contains("MSG_START_FILE_QUERY_REQ") || e.contains("MSG_START_FILE_QUERY_RESP")){
                        if (downReqBodys.containsKey(callid)){
                            downReqBodys.get(callid).add(e);
                        }else {
                            List<String> list = new ArrayList<>();
                            list.add(e);
                            downReqBodys.put(callid,list);
                        }
                    }

                    currentBody = new StringBuffer();
                }
            }else if (record){

                currentBody.append(line).append(System.lineSeparator());
            }
        }
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
        if (line.startsWith("[ INFO ]") || line.startsWith("[ WARN ]") || line.startsWith("[ ERROR]") || line.startsWith("[ DEBUG]")) {
            String lastBody1 = currentBody.toString();
            if (lastBody1.contains("\r\n") && lastBody1.contains("sip")) {
                Pattern compile = Pattern.compile(bodyCallidReg);
                Matcher matcher = compile.matcher(lastBody1);
                if (matcher.find()) {
                    String callid = matcher.group();
                    callid = callid.split("Call-ID:")[1].trim();
                    if (bodys.containsKey(callid)) {
                        bodys.get(callid).add(lastBody1);
                    }else {
                        List<String> list = new ArrayList<>();
                        list.add(lastBody1);
                        bodys.put(callid,list);
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