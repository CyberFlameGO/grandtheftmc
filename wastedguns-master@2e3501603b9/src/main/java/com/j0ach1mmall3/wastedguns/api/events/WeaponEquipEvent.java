package com.j0ach1mmall3.wastedguns.api.events;

import net.grandtheftmc.guns.weapon.Weapon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 29/09/2016
 */
public final class WeaponEquipEvent extends WeaponEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ItemStack previousItem;
    private final ItemStack newItem;

    public WeaponEquipEvent(LivingEntity who, Weapon weapon, ItemStack previousItem, ItemStack newItem) {
        super(who, weapon);
        this.previousItem = previousItem;
        this.newItem = newItem;
    }

    public ItemStack getPreviousItem() {
        return this.previousItem;
    }

    public ItemStack getNewItem() {
        return this.newItem;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
