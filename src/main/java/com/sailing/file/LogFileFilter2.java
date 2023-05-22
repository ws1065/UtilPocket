package com.sailing.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang.StringUtils;

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
public class LogFileFilter2 {
    private static String timeREG = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}";
    private static String callIDReg = "callId:[\\w-]+";
    private static String deviceIDReg = "\\w{20}";

    private static  String contentReg = "content:[\\w ]+";

    public static void main(String[] args) {
        String pathname = "D:\\Documents\\WeChat Files\\wxid_lsur2emrh56942\\FileStorage\\File\\2022-08\\下级日志";
        String pathname1 = "D:\\Documents\\WeChat Files\\wxid_lsur2emrh56942\\FileStorage\\File\\2022-08\\下级vscg_app日志";

        String type = "INVITE";
        String time = "";
        String deviceId = "";
        pathname = args[0];
        pathname1 = args[1];
        deviceId = args[2];
        time = args[3];

        calc(pathname, pathname1, type, time, deviceId);


    }// Map的value值降序排序

    private static void calc(String pathname, String pathname1, String type, String time, String deviceId) {
        File folder = new File(pathname);
        File folderStream = new File(pathname1);


        List<File> up = new ArrayList<>();
        List<File> down = new ArrayList<>();
        for (File file : folder.listFiles()) {
            if (file.getAbsolutePath().contains("UP")) {
                up.add(file);
            } else if (file.getAbsolutePath().contains("DOWN")) {
                down.add(file);
            }
        }

        List<File> upStram = new ArrayList<>();
        List<File> downStram = new ArrayList<>();
        for (File file : folderStream.listFiles()) {
            downStram.add(file);
        }

        Map<String,List<Entity2>> sipBodys = new HashMap<>();
        Map<String,List<String>> streamBodys = new HashMap<>();

        String callIdReg = "callId=[\\w-]+";

        for (File file : downStram) {
            //读取文件每一行进行判断和过滤
            List<String> lines = FileUtil.readLines(file, "utf-8");
            for (String line : lines) {
                Matcher matcher = Pattern.compile(callIdReg).matcher(line);
                if (matcher.find()) {
                    String callID = matcher.group().replaceAll("callId=", "");

                    if (!streamBodys.containsKey(callID)){
                        List<String> objects = new ArrayList<String>();
                        objects.add(line);
                        streamBodys.put(callID,objects);
                    }else {
                        streamBodys.get(callID).add(line);
                    }
                }
            }
        }

        for (File file : down) {
            //读取文件每一行进行判断和过滤
            List<String> lines = FileUtil.readLines(file, "utf-8");
            String deviceIDReg = "\\d{20}";
            for (String line : lines) {
                Entity2 entity2 = new Entity2();
                entity2.setLog(line);
                Matcher matcher = Pattern.compile(callIDReg).matcher(line);
                if (matcher.find()) {
                    String callID = matcher.group().replaceAll("callId:", "");
                    entity2.setCallID(callID);
                    matcher = Pattern.compile(contentReg).matcher(line);
                    if (matcher.find()) {
                        entity2.setContent((matcher.group().replaceAll("content:","")));
                    }
                    matcher = Pattern.compile(timeREG).matcher(line);
                    if (matcher.find()) {
                        entity2.setTime(DateUtil.parse(matcher.group()));
                    }
                    matcher = Pattern.compile(deviceIDReg).matcher(line);
                    if (matcher.find()) {
                        entity2.setDeviceID(matcher.group());
                    }
                    if (!sipBodys.containsKey(callID)){
                        List<Entity2> objects = new ArrayList<Entity2>();
                        objects.add(entity2);
                        sipBodys.put(callID,objects);
                    }else {
                        sipBodys.get(callID).add(entity2);
                    }
                }
            }
        }
        Iterator<Map.Entry<String, List<Entity2>>> iterator = sipBodys.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Entity2>> entry = iterator.next();

            boolean isInvite = false;
            //如果包含就不删
            List<Entity2> value = entry.getValue();
            if (StringUtils.isNotBlank(type)) {
                for (Entity2 entity2 : value) {
                    if (entity2.getLog().contains(type)) {
                        isInvite = true;
                        break;
                    }
                }
            }else {
                isInvite = true;
            }


            boolean isTime = false;
            if (StringUtils.isNotBlank(time)) {
                for (Entity2 entity2 : value) {
                    if (entity2.getTime().getTime() > DateUtil.parse(time).getTime()) {
                        isTime = true;
                        break;
                    }
                }
            }else {
                isTime = true;
            }


            boolean isDeviceId = false;
            if (StringUtils.isNotBlank(deviceId)) {
                for (Entity2 entity2 : value) {
                    if (entity2.getDeviceID() != null && entity2.getDeviceID().contains(deviceId)) {
                        isDeviceId = true;
                        break;
                    }
                }
            }else {
                isDeviceId = true;
            }



            if (!isInvite || !isTime || !isDeviceId)
                iterator.remove();
        }

        for (Map.Entry<String, List<Entity2>> entry : sipBodys.entrySet()) {
            entry.getValue().sort(new Comparator<Entity2>() {
                @Override
                public int compare(Entity2 o1, Entity2 o2) {
                    return (int)(o1.getTime().getTime() - o2.getTime().getTime());
                }
            });

            String minKey = entry.getKey().substring(0, 11);
            if (streamBodys.containsKey(minKey)){
                List<String> strings = streamBodys.get(minKey);
                if (!entry.getValue().isEmpty()) {
                    for (Entity2 entity2 : entry.getValue()) {
                        entity2.setStream(strings);
                    }
                }
            }

        }
        for (Map.Entry<String, List<Entity2>> entry : sipBodys.entrySet()) {
            System.out.println(entry.getKey());
            for (Entity2 entity2 : entry.getValue()) {
                System.out.println("       "+DateUtil.format(entity2.getTime(),"MM-dd HH:mm:ss.S")+" "+entity2.getContent()+"  DeviceId"+entity2.getDeviceID());
            }
            Entity2 entity2 = entry.getValue().get(0);
            if (entity2.getStream()!=null) {
                for (String stream : entity2.getStream()) {
                    System.out.println("              " + stream);
                }
            }
        }
        System.out.println();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortDescend(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });

        Map<K, V> returnMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
    }

    // Map的value值升序排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortAscend(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return compare;
            }
        });

        Map<K, V> returnMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
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