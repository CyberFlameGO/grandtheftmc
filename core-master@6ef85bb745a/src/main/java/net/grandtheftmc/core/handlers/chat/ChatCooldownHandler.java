package net.grandtheftmc.core.handlers.chat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatCooldownHandler {
    private Map<UUID, Long> lastChats;

    public ChatCooldownHandler() {
        this.lastChats = new HashMap<>();
    }

    public boolean canChatAgain(UUID uuid, int cooldown) {
        if (!lastChats.containsKey(uuid)) return true;
        if (System.currentTimeMillis() >= this.lastChats.get(uuid)) {
            this.lastChats.remove(uuid);
            return true;
        }
        return false;
    }

    public void setCanChatAgain(UUID uuid, Long timestamp) {
        this.lastChats.put(uuid, timestamp);
    }

    protected Map<UUID, Long> getLastChats() {
        return lastChats;
    }
}
