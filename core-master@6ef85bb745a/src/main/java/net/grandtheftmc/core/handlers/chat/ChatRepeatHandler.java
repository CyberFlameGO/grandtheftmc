package net.grandtheftmc.core.handlers.chat;

import net.grandtheftmc.core.Core;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ChatRepeatHandler {
    private Map<UUID, List<String>> recentMessages;

    public ChatRepeatHandler() {
        this.recentMessages = new HashMap<>();
    }

    public boolean canChatAgain(UUID uuid, String message) {
        if (this.recentMessages.getOrDefault(uuid, new ArrayList<>()).contains(message)) {
            return false;
        } else {
            for (String string : this.recentMessages.getOrDefault(uuid, new ArrayList<>())) {
                if(message.contains(string)) return false;
            }
        }
        return true;
    }

    public void addRecentMessage(UUID uuid, String message, int cooldown) {
        List<String> messages = this.recentMessages.getOrDefault(uuid, new ArrayList<>());
        messages.add(message);
        this.recentMessages.put(uuid, messages);
        new BukkitRunnable() {
            @Override
            public void run() {
                messages.remove(message);
                recentMessages.put(uuid, messages);
            }
        }.runTaskLaterAsynchronously(Core.getInstance(), cooldown * 20);
    }

    protected Map<UUID, List<String>> getRecentMessages() {
        return recentMessages;
    }
}
