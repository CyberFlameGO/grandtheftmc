package net.grandtheftmc.Bungee.redisbungee;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import net.grandtheftmc.Bungee.redisbungee.data.DataType;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

public class RedisManager {
    private final String messageChannel = "gtm_messages";
    private final RedisBungeeAPI redisBungeeAPI;

    public RedisManager(RedisBungeeAPI redisBungeeAPI) {
        this.redisBungeeAPI = redisBungeeAPI;
    }

    public void sendMessage(String serialized) {
        this.redisBungeeAPI.sendChannelMessage(this.messageChannel, serialized);
    }

    public UUID getUUIDFromName(String name) {
        return this.redisBungeeAPI.getUuidFromName(name, false);
    }

    public boolean isPlayerOnline(String name) {
        UUID uuid = getUUIDFromName(name);
        if (uuid == null) return false;
        return this.redisBungeeAPI.isPlayerOnline(uuid);
    }

    public RedisBungeeAPI getRedisAPI() {
        return this.redisBungeeAPI;
    }

    public String getMessageChannel() {
        return this.messageChannel;
    }

    public String serialize(DataType dataType, Map<String, Object> data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("datatype", dataType.name());
        data.keySet().forEach(key -> jsonObject.put(key, data.get(key)));
        return jsonObject.toString();
    }
}
