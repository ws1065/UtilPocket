package com.sailing.demo.vscg.pojo;


import lombok.Data;

@Data
public class Serverpojo {
    private String serverip;
    private int serverport ;

    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }

    public int getServerport() {
        return serverport;
    }

    public void setServerport(int serverport) {
        this.serverport = serverport;
    }

    @Override
    public String toString() {
        return "Serverpojo{" +
                "serverip='" + serverip + '\'' +
                ", serverport=" + serverport +
                '}';
    }
}
