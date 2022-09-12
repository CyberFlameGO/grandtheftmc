package com.j0ach1mmall3.wastedguns.api.events.ranged;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 16/07/2016
 */
public final class AmmoUpdateEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private Map<String, Integer> ammo = new HashMap<>();


    public AmmoUpdateEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Map<String, Integer> getAmmo() {
        return this.ammo;
    }

    public void setAmmo(Map<String, Integer> ammo) {
        this.ammo = ammo;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
