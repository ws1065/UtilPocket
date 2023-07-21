package com.sailing;

import lombok.extern.slf4j.Slf4j;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2022-10-18 10:09
 */
@Slf4j
public class Temp {
    public static void main(String[] args) {
//StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 1000; i++) {
            new Thread(()->{
                while (true) {
//                    sb.append("sta");
                    log.debug("debug");
                    log.debug("debug");
                }
            }).start();
        }
    }
}