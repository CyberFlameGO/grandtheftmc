package com.j0ach1mmall3.wastedguns.api.events.ranged;

import com.j0ach1mmall3.wastedguns.api.events.WeaponEvent;
import net.grandtheftmc.guns.weapon.Weapon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 6/05/2016
 */
public final class RangedWeaponReloadEvent extends WeaponEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private int ammoToReload;

    public RangedWeaponReloadEvent(LivingEntity who, Weapon weapon, int ammoToReload) {
        super(who, weapon);
        this.ammoToReload = ammoToReload;
    }

    public int getAmmoToReload() {
        return this.ammoToReload;
    }

    public void setAmmoToReload(int ammoToReload) {
        this.ammoToReload = ammoToReload;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
