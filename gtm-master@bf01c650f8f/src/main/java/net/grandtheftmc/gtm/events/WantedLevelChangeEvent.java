package net.grandtheftmc.gtm.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.grandtheftmc.gtm.users.GTMUser;

public class WantedLevelChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private final Player player;
    private final GTMUser user;
    private final int wantedLevel;

    public WantedLevelChangeEvent(Player player, GTMUser user, int wantedLevel) {
        this.player = player;
        this.user = user;
        this.wantedLevel = wantedLevel;
    }

    public Player getPlayer() {
        return this.player;
    }

    public GTMUser getUser() {
        return this.user;
    }

    public int getWantedLevel() {
        return this.wantedLevel;
    }

}