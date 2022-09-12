package com.j0ach1mmall3.wastedguns.api.events;

import net.grandtheftmc.guns.weapon.Weapon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 6/05/2016
 */
public final class WeaponSneakEvent extends WeaponEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final boolean sneaking;

    public WeaponSneakEvent(LivingEntity who, Weapon weapon, boolean sneaking) {
        super(who, weapon);
        this.sneaking = sneaking;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
