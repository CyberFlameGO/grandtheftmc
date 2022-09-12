package net.grandtheftmc.jedis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.grandtheftmc.ServerType;
import net.grandtheftmc.ServerTypeId;
import redis.clients.jedis.JedisPubSub;

import java.util.List;

/**
 * Created by Luke Bingham on 19/08/2017.
 */
public final class JMessageReader extends JedisPubSub {

    private final ServerTypeId serverTypeId;
    private final Gson gson = new Gson();
    private final JsonParser jsonParser = new JsonParser();

    public JMessageReader(ServerTypeId serverTypeId) {
        this.serverTypeId = serverTypeId;
    }

    @Override
    public final void onMessage(String channel, String message) {
        if (!isValid(message)) return;

        try {
            JsonObject label = (JsonObject) jsonParser.parse(message);
            String messageName = label.get("name").getAsString();
            int senderId = label.get("senderId").getAsInt();
            String senderType = label.get("senderType").getAsString();
            int recipientId = label.get("recipientId").getAsInt();
            String recipientType = label.get("recipientType").getAsString();

            if(!"GLOBAL".equalsIgnoreCase(recipientType)) {
                if(!this.serverTypeId.getServerType().name().equals(recipientType)) return;
                if(recipientId != -1) if(this.serverTypeId.getId() != recipientId) return;
            }
//            if (recipient.equalsIgnoreCase("all") || recipient.equalsIgnoreCase(serverName)) {
                Class<? extends JMessage> messageClass = (Class<? extends JMessage>) Class.forName(messageName);
                JMessage msg = gson.fromJson(label.getAsJsonObject("content"), messageClass);

                List<JMessageListener> listenerList = JedisModule.listeners.get(messageClass);
                if (listenerList != null) listenerList.forEach(c -> c.onReceive(new ServerTypeId(ServerType.valueOf(senderType), senderId), msg));
//            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void onPMessage(String s, String s1, String s2) {

    }

    @Override
    public final void onSubscribe(String s, int i) {

    }

    @Override
    public final void onUnsubscribe(String s, int i) {

    }

    @Override
    public final void onPUnsubscribe(String s, int i) {

    }

    @Override
    public final void onPSubscribe(String s, int i) {

    }

    private boolean isValid(String str) {
        try {
            jsonParser.parse(str);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
