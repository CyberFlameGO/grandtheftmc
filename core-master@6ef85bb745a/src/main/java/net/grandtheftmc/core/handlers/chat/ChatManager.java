package net.grandtheftmc.core.handlers.chat;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Component;
import org.bukkit.configuration.file.YamlConfiguration;

public class ChatManager implements Component<ChatManager, Core> {
    private static ChatSettings chatSettings;

    private static ChatCooldownHandler cooldownHandler;
    private static ChatRepeatHandler repeatHandler;
    private static ChatAdHandler adHandler;

    public ChatManager(YamlConfiguration config) {
        chatSettings = new ChatSettings(config);
        cooldownHandler = new ChatCooldownHandler();
        repeatHandler = new ChatRepeatHandler();
        adHandler = new ChatAdHandler();
    }

    @Override
    public ChatManager onDisable(Core plugin) {
        chatSettings.getDomainWhitelist().clear();
        cooldownHandler.getLastChats().clear();
        repeatHandler.getRecentMessages().clear();
        return this;
    }

    public static ChatSettings getSettings() {
        return chatSettings;
    }

    public static ChatCooldownHandler getCooldownHandler() {
        return cooldownHandler;
    }

    public static ChatRepeatHandler getRepeatHandler() {
        return repeatHandler;
    }

    public static ChatAdHandler getAdHandler() {
        return adHandler;
    }

    public static int getDefaultChatCooldown() {
        return chatSettings.getDefaultChatCooldown();
    }

    public static int getVipChatCooldown() {
        return chatSettings.getVipChatCooldown();
    }
}
