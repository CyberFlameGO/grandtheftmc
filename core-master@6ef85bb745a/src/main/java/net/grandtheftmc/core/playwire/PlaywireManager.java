package net.grandtheftmc.core.playwire;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.playwire.listeners.WSListener;
import net.grandtheftmc.core.util.Component;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by Timothy Lampen on 2017-12-07.
 */
public class PlaywireManager implements Component<PlaywireManager, Core>{
/**
 * This package has been disabled for now.
 */

    private WebSocket websocket;
    private static String URI = "rewards.grandtheftmc.net";
    private static String TOKEN = "NotAToken", ID = "70";

    @Override
    public PlaywireManager onEnable(Core plugin) {
        new BukkitRunnable() {
            public void run() {
                if(websocket==null || !websocket.isOpen()) {
                    openWebSocket(URI, TOKEN);
                    registerChannel("ServerNotificationChannel");
                }
            }
        }.runTaskTimerAsynchronously(Core.getInstance(), 0, 1000);
        return this;
    }

    private void openWebSocket(String uri, String serverToken) {
        String endpoint = "wss://" + uri + "/cable?token=" + serverToken;
        try {
            websocket = new WebSocketFactory().createSocket(endpoint);
            websocket.addListener(new WSListener());
            websocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerChannel(String channelName) {
        JSONObject channelSubscribe = new JSONObject();
        channelSubscribe.put("command", "subscribe");
        channelSubscribe.put("identifier", new JSONObject().put("channel", channelName).toString());
        websocket.sendText(channelSubscribe.toString());
    }

    /**
     * @param uuid the uuid of the player.
     * @return the link that the player goes to to watch the ad.
     */
    public static String getPlaywireLink(UUID uuid) {
        return "http://" + URI + "/?server_id=" + ID + "&player_uuid=" + uuid;
    }
}
