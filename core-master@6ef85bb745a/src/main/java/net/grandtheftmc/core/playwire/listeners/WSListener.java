package net.grandtheftmc.core.playwire.listeners;

import com.neovisionaries.ws.client.*;
import net.grandtheftmc.core.playwire.SocketMessage;
import net.grandtheftmc.core.playwire.SocketMessageType;
import net.grandtheftmc.core.playwire.events.AsyncPlaywireRecieveEvent;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;

/**
 * Created by Timothy Lampen on 2017-12-06.
 */
public class WSListener implements WebSocketListener{

    public void onStateChanged(WebSocket webSocket, WebSocketState webSocketState) throws Exception {

    }

    public void onConnectError(WebSocket webSocket, WebSocketException e) throws Exception {

    }

    public void onConnected(WebSocket webSocket, Map<String, List<String>> map) throws Exception {
        System.out.println("[playwire-ads] Websocket Connected");
    }

    public void onDisconnected(WebSocket webSocket, WebSocketFrame webSocketFrame, WebSocketFrame webSocketFrame1, boolean b) throws Exception {
        System.out.println("[playwire-ads] Websocket Disconnected!");
    }

    public void onTextMessage(WebSocket webSocket, String text) throws Exception {
        SocketMessage message = new SocketMessage(text);
        String compare = message.hasType() ? message.getType().toUpperCase() : message.getState().toUpperCase();
        SocketMessageType type = SocketMessageType.valueOf(compare);

        AsyncPlaywireRecieveEvent event = new AsyncPlaywireRecieveEvent(message.getPlayerUUID(), type);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void onError(WebSocket webSocket, WebSocketException e) throws Exception {
        System.out.println("[playwire-ads] ========================================");
        System.out.println("[playwire-ads] WebsocketException: " + e);
        System.out.println("[playwire-ads] ========================================");
    }

    public void onFrame(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onContinuationFrame(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onTextFrame(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onBinaryFrame(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onCloseFrame(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onPingFrame(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onPongFrame(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onBinaryMessage(WebSocket webSocket, byte[] bytes) throws Exception {

    }

    public void onSendingFrame(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onFrameSent(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onFrameUnsent(WebSocket webSocket, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onThreadCreated(WebSocket webSocket, ThreadType threadType, Thread thread) throws Exception {

    }

    public void onThreadStarted(WebSocket webSocket, ThreadType threadType, Thread thread) throws Exception {

    }

    public void onThreadStopping(WebSocket webSocket, ThreadType threadType, Thread thread) throws Exception {

    }

    public void onFrameError(WebSocket webSocket, WebSocketException e, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onMessageError(WebSocket webSocket, WebSocketException e, List<WebSocketFrame> list) throws Exception {

    }

    public void onMessageDecompressionError(WebSocket webSocket, WebSocketException e, byte[] bytes) throws Exception {

    }

    public void onTextMessageError(WebSocket webSocket, WebSocketException e, byte[] bytes) throws Exception {

    }

    public void onSendError(WebSocket webSocket, WebSocketException e, WebSocketFrame webSocketFrame) throws Exception {

    }

    public void onUnexpectedError(WebSocket webSocket, WebSocketException e) throws Exception {

    }

    public void handleCallbackError(WebSocket webSocket, Throwable throwable) throws Exception {

    }

    public void onSendingHandshake(WebSocket webSocket, String s, List<String[]> list) throws Exception {

    }
}
