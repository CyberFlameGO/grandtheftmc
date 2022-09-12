package net.grandtheftmc.jedis;

import net.grandtheftmc.ServerTypeId;

/**
 * Created by Luke Bingham on 19/08/2017.
 */
public interface JMessageListener<T extends JMessage> {
    void onReceive(ServerTypeId sender, T message);
}
