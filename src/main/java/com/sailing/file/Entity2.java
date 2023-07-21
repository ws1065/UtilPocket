package com.sailing.file;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2022-08-22 13:20
 */
@Data
public class Entity2 {
    Date time;
    String deviceID;
    String callID;
    String content;
    String log;
    List<String> stream;
}