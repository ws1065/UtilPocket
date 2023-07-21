package com.sailing.nio.common;

import com.sailing.nio.DatagramChannelAdapter;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

/**
 * @program: demo
 * @description:
 * @author: wangsw
 * @create: 2021-10-18 15:08
 */
public class CloseUtils {
    public static void close(Selector readSelector) {
        if (readSelector != null) {
            try {
                readSelector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void close(DatagramChannel readSelector) {
        if (readSelector != null) {
            try {
                readSelector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void close(DatagramChannelAdapter readSelector) {
        if (readSelector != null) {
            try {
                readSelector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}