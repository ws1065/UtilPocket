//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sailing.license.cache;

public class CacheEntity<T> {
    private T value;
    private long createTime = System.currentTimeMillis();
    private long cacheTime;

    public CacheEntity() {
    }

    public CacheEntity(T value, long cacheTime) {
        this.value = value;
        this.cacheTime = cacheTime;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getCacheTime() {
        return this.cacheTime;
    }

    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }
}
