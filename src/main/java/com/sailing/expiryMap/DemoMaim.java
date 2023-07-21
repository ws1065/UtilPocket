package com.sailing.expiryMap;

import com.sailing.dscg.common.DateTool;

import java.util.UUID;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-09-22 13:43
 */
public class DemoMaim {
    public static void main(String[] args) throws InterruptedException {
        ExpiryMap<String,String> map = new ExpiryMap<>();
        for (int i = 0; i < 150; i++) {
            map.put(UUID.randomUUID().toString(), DateTool.getCurrDateString());
        }
        //Thread.sleep(20*1000);
        //boolean b = map.containsKey(UUID.randomUUID().toString());
        System.out.println();
    }
}