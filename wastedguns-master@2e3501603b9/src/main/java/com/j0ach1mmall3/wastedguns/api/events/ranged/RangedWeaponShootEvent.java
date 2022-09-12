package com.j0ach1mmall3.wastedguns.api.events.ranged;

import com.j0ach1mmall3.wastedguns.api.events.WeaponEvent;
import net.grandtheftmc.guns.weapon.Weapon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 6/05/2016
 */
public final class RangedWeaponShootEvent extends WeaponEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public RangedWeaponShootEvent(LivingEntity who, Weapon weapon) {
        super(who, weapon);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
