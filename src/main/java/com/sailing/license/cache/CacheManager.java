//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sailing.license.cache;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {
    private static ConcurrentHashMap<String, CacheEntity> cacheMap = new ConcurrentHashMap();

    public CacheManager() {
    }

    public static void refresh() {
        Iterator var0 = cacheMap.keySet().iterator();

        while(var0.hasNext()) {
            String key = (String)var0.next();
            if (isExpire(key)) {
                remove(key);
            }
        }

    }

    public static boolean put(String key, Object value) {
        if (key.isEmpty()) {
            return false;
        } else {
            CacheEntity<Object> cacheEntity = new CacheEntity();
            cacheEntity.setCacheTime(0L);
            cacheEntity.setValue(value);
            cacheMap.put(key, cacheEntity);
            return true;
        }
    }

    public static boolean put(String key, Object value, long cacheTime) {
        if (key.isEmpty()) {
            return false;
        } else {
            CacheEntity<Object> cacheEntity = new CacheEntity();
            cacheEntity.setCacheTime(cacheTime);
            cacheEntity.setValue(value);
            cacheMap.put(key, cacheEntity);
            return true;
        }
    }

    public static boolean remove(String key) {
        if (key.isEmpty()) {
            return false;
        } else if (!cacheMap.containsKey(key)) {
            return true;
        } else {
            cacheMap.remove(key);
            return true;
        }
    }

    public static Object get(String key) {
        if (!key.isEmpty() && !isExpire(key)) {
            CacheEntity cacheEntity = (CacheEntity)cacheMap.get(key);
            return null == cacheEntity ? null : cacheEntity.getValue();
        } else {
            return null;
        }
    }

    private static boolean isExpire(String key) {
        if (key.isEmpty()) {
            return false;
        } else if (cacheMap.containsKey(key)) {
            CacheEntity cacheEntity = (CacheEntity)cacheMap.get(key);
            long createTime = cacheEntity.getCreateTime();
            long currentTime = System.currentTimeMillis();
            long cacheTime = cacheEntity.getCacheTime();
            return cacheTime > 0L && currentTime - createTime > cacheTime;
        } else {
            return false;
        }
    }

    public static int getCacheSize() {
        return cacheMap.size();
    }

    static {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                CacheManager.refresh();
            }
        }, 0L, 60000L);
    }
}
