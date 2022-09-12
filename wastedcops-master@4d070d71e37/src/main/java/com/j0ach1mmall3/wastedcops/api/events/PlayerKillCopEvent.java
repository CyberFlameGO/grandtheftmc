package com.j0ach1mmall3.wastedcops.api.events;

import com.j0ach1mmall3.wastedcops.api.Cop;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by colt on 10/27/16.
 */
public class PlayerKillCopEvent extends CopEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private LivingEntity copKiled;
    private Player player;
    private List<ItemStack> drops;

    public PlayerKillCopEvent(Cop cop, LivingEntity copKiled, Player player, List<ItemStack> drops) {
        super(cop);
        this.copKiled = copKiled;
        this.player = player;
        this.drops = drops;
    }

    public LivingEntity getCopKiled() {
        return this.copKiled;
    }

    public Player getPlayer() {
        return this.player;
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

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
