package net.grandtheftmc.jedis;

import net.grandtheftmc.ServerType;
import net.grandtheftmc.ServerTypeId;

import java.util.HashMap;

/**
 * Created by Luke Bingham on 19/08/2017.
 */
public class JedisManager {

    private final HashMap<JedisChannel, JedisModule> jedisModules;

    public JedisManager() {
        this.jedisModules = new HashMap<JedisChannel, JedisModule>();
    }

    public JedisModule getModule(JedisChannel channel) {
        return this.jedisModules.getOrDefault(channel, null);
    }

    public void initModule(ServerTypeId serverTypeId, JedisChannel channel, String vlan, int port, String password) {
        this.jedisModules.putIfAbsent(channel, new JedisModule(serverTypeId, channel, vlan, port, password));
    }

    public HashMap<JedisChannel, JedisModule> getJedisModules() {
        return this.jedisModules;
    }
}
