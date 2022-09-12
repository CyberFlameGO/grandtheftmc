package net.grandtheftmc.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class DisplayNameUpdateEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private String rankPrefix;
    private String prefix;
    private String nameColor;
    private String suffix;

    public DisplayNameUpdateEvent(Player player) {
        super(player);
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getRankPrefix() {
        return this.rankPrefix;
    }

    public void setRankPrefix(String rankPrefix) {
        this.rankPrefix = rankPrefix;
    }

    public String getNameColor() {
        return this.nameColor;
    }

    public void setNameColor(String nameColor) {
        this.nameColor = nameColor;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
