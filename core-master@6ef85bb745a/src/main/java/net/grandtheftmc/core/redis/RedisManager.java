package net.grandtheftmc.core.redis;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.redis.data.DataType;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Map;

/**
 * Created by Adam on 14/06/2017.
 */
public class RedisManager {

    public static final String channel = "gtmcore_msgs";

    public static boolean publishMessage(DataType type, Map<String, Object> data) {

        try {
            //Jedis e = Core.getInstance().getJedis();
            //Core.log("Publishing channel=" + channel + ". msg=" + serialized);

            JSONObject obj = new JSONObject();
            obj.put("datatype", type.name());
            data.keySet().forEach(key -> obj.put(key, data.get(key)));

            Jedis jedis = RedisFactory.getPool().getResource();
            jedis.publish(channel, obj.toString());
            jedis.close();
            return true;
        } catch (JedisConnectionException e) {
            Core.log("Unable to connect to Jedis!!");
            e.printStackTrace();
        }

        return false;
    }

    public static boolean redisEnabled(){
        return RedisFactory.getPool() != null;
    }
}
