//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.sailing.license;

public enum ProductEnum {
    DSCG("DSCG", "数据安全交换系统"),
    VSCG("VSCG", "视频安全交换系统"),
    SGG("SGG", "安全隔离与信息交换系统"),
    UGG("UGG", "安全隔离与单向传输系统"),
    USCG("USCG", "单向安全交换平台"),
    NTA("NTA", "网络流量审计与分析系统"),
    DCAS("DCAS", "数据一致性对账平台");

    private String code;
    private String name;

    private ProductEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
