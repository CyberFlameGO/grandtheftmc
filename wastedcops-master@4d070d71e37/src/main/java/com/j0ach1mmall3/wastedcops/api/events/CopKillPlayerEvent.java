package com.j0ach1mmall3.wastedcops.api.events;

import com.j0ach1mmall3.wastedcops.api.Cop;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CopKillPlayerEvent extends CopEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player playerKilled;
    private List<ItemStack> drops;

    public CopKillPlayerEvent(Cop cop, Player playerKilled, List<ItemStack> drops) {
        super(cop);
        this.playerKilled = playerKilled;
        this.drops = drops;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public Player getPlayerKilled() {
        return this.playerKilled;
    }

    public List<ItemStack> getDrops() {
        return this.drops;
    }

    public void setDrops(List<ItemStack> newDrops) {
        if(newDrops == null || newDrops.isEmpty()) return;
        this.drops = newDrops;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}


