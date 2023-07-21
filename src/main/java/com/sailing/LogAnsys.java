package com.sailing;

import com.google.common.collect.Maps;
import com.sailing.dscg.common.DateTool;
import oshi.util.FileUtil;

import java.util.*;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-07-13 23:25
 */
public class LogAnsys {

    public static void main(String[] args) {
        System.out.println();
        List<String> file = FileUtil.readFile("C:\\Users\\sailing\\Desktop\\cb.log");
        HashMap<String, Map<Long,String>> map = new HashMap<>();
        HashMap<String, Long> times = new HashMap<>();
        for (String s : file) {
            if (!s.trim().equals("")) {
                try {
                    String key = s.substring(s.indexOf("[", s.indexOf("[", s.indexOf("[") + 1) + 1) + 1,
                            s.indexOf("]", s.indexOf("]", s.indexOf("]") + 1) + 1));
                    if (map.containsKey(key)) {
                        map.get(key).put(DateTool.StringToDate(s.substring(s.indexOf("[") + 1, s.indexOf("]")), "yyyy-MM-dd HH:mm:ss.S").getTime(),s);
                    } else {
                        LinkedHashMap<Long,String> map1 = Maps.newLinkedHashMap();
                        map1.put(DateTool.StringToDate(s.substring(s.indexOf("[") + 1, s.indexOf("]")), "yyyy-MM-dd HH:mm:ss.S").getTime(),s);
                        map.put(key, map1);
                    }
                } catch (Exception e) {
                    System.out.println();
                }
            }
        }
        for (Map.Entry<String, Map<Long, String>> entry : map.entrySet()) {
            entry.getValue().entrySet().stream().sorted(Map.Entry.<Long, String>comparingByValue())
                    .forEachOrdered(e-> System.out.println(e.getValue()));
            System.out.println();
            System.out.println();
        }
        for (Map.Entry<String, Map<Long, String>> entry : map.entrySet()) {
            long max = -1;
            long min = Long.MIN_VALUE;
            for (Map.Entry<Long, String> stringEntry : entry.getValue().entrySet()) {
                String s = stringEntry.getValue();
                long time = DateTool.StringToDate(s.substring(s.indexOf("[") + 1, s.indexOf("]")), "yyyy-MM-dd HH:mm:ss.S").getTime();
                if (max == -1 || max < time){
                    max = time;
                }
                if (max == -1 || min > time){
                    min = time;
                }
            }
            times.put(entry.getKey(),max-min);
        }
        HashMap<String, String> map1 = Maps.newLinkedHashMap();
        times.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue()).forEachOrdered(e->map1.put(e.getKey(),DateTool.dateToString(new Date(e.getValue()))));
        System.out.println();
    }
}