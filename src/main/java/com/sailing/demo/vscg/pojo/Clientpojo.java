package com.sailing.demo.vscg.pojo;


import lombok.Data;

@Data
public class Clientpojo {

    private String clientip ;
    private int serverport ;

    public String getClientip() {
        return clientip;
    }

    public void setClientip(String clientip) {
        this.clientip = clientip;
    }

    public int getServerport() {
        return serverport;
    }

    public void setServerport(int serverport) {
        this.serverport = serverport;
    }



    @Override
    public String toString() {
        return "Clientpojo{" +
                "clientip='" + clientip + '\'' +
                ", serverport=" + serverport +
                '}';
    }






}
