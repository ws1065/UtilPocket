package com.sailing.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.HashMap;
import java.util.Map;

public class ChannelSessionManager {
    private Map<ChannelId,Channel> s2c;
    private Map<ChannelId,Channel> c2s;

    public ChannelSessionManager(){
        s2c = new HashMap<ChannelId, Channel>();
        c2s = new HashMap<ChannelId, Channel>();
    }

    public void addSession(Channel client,Channel server){
        c2s.put(client.id(),server);
        s2c.put(server.id(),client);
    }

    public Channel getServerChannel(Channel client){
        return c2s.get(client.id());
    }

    public Channel getClientChannel(Channel server){
        return s2c.get(server.id());
    }
}
