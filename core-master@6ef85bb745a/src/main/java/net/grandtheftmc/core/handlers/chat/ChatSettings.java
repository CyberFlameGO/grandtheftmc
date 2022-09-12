package net.grandtheftmc.core.handlers.chat;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collection;

public class ChatSettings {
    private YamlConfiguration config;

    private Collection<String> domainWhitelist;
    private int defaultChatCooldown;
    private int vipChatCooldown;

    public ChatSettings(YamlConfiguration chatSettingsConfig) {
        this.config = chatSettingsConfig;
        this.loadSettings();
    }

    public void loadSettings() {
        this.defaultChatCooldown = this.config.getInt("default-chat-delay");
        this.vipChatCooldown = this.config.getInt("vip-chat-delay");
        this.domainWhitelist = this.config.getStringList("domain-whitelist");
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }

    public Collection<String> getDomainWhitelist() {
        return domainWhitelist;
    }

    public int getDefaultChatCooldown() {
        return defaultChatCooldown;
    }

    public void setDefaultChatCooldown(int defaultChatCooldown) {
        this.defaultChatCooldown = defaultChatCooldown;
    }

    public int getVipChatCooldown() {
        return vipChatCooldown;
    }

    public void setVipChatCooldown(int vipChatCooldown) {
        this.vipChatCooldown = vipChatCooldown;
    }
}
