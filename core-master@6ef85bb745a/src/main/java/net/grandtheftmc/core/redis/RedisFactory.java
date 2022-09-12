package net.grandtheftmc.core.redis;

import net.grandtheftmc.core.Core;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Adam on 16/06/2017.
 */
public class RedisFactory {

    private static JedisPool pool;

    public RedisFactory(String server, String passwd, int port, Runnable onCreate){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(8);
        config.setMinIdle(2);
        config.setMaxIdle(4);
        config.setBlockWhenExhausted(false);
        this.pool = new JedisPool(config, server, port, 0, passwd);

        //Callback
        onCreate.run();
    }

    public static JedisPool getPool(){
        return pool;
    }

}
