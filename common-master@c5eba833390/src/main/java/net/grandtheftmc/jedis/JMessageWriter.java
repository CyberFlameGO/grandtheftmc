package net.grandtheftmc.jedis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.grandtheftmc.ServerType;
import net.grandtheftmc.ServerTypeId;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by Luke Bingham on 19/08/2017.
 */
public final class JMessageWriter {

    private final ServerTypeId serverTypeId;
    private final Gson gson = new Gson();
    private final JedisPool pool;
    private final JedisChannel channel;

    public JMessageWriter(ServerTypeId serverTypeId, JedisPool pool, JedisChannel channel) {
        this.serverTypeId = serverTypeId;
        this.pool = pool;
        this.channel = channel;
    }

    public final void publishPacket(Object message, ServerTypeId recipient) {
        JsonObject label = new JsonObject();
        label.addProperty("name", message.getClass().getName());
        label.addProperty("senderId", serverTypeId.getId());
        label.addProperty("senderType", serverTypeId.getServerType().name());
        label.addProperty("recipientId", recipient.getId());
        label.addProperty("recipientType", recipient.getServerType().name());
        label.add("content", gson.toJsonTree(message));

        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel.getChannel(), label.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void publishPacket(Object message) {
        JsonObject label = new JsonObject();
        label.addProperty("name", message.getClass().getName());
        label.addProperty("senderId", serverTypeId.getId());
        label.addProperty("senderType", serverTypeId.getServerType().name());
        label.addProperty("recipientId", -1);
        label.addProperty("recipientType", ServerType.GLOBAL.name());
        label.add("content", gson.toJsonTree(message));

        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel.getChannel(), label.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
