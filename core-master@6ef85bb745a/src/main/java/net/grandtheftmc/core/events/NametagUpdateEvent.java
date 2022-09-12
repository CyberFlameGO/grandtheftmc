package net.grandtheftmc.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class NametagUpdateEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private String prefix;
    private String nameColor;
    private String suffix;
    // private int value;
    // private String belowName;

    public NametagUpdateEvent(Player player) {
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

    /*
     *
     * public int getValue() { return value; }
     *
     * public void setValue(int value) { this.value = value; }
     *
     * public String getBelowName() { return belowName; }
     *
     * public void setBelowName(String belowName) { this.belowName = belowName;
     *
     * }
     */

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
