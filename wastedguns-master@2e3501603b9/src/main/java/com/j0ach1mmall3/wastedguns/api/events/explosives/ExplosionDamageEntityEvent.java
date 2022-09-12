package com.j0ach1mmall3.wastedguns.api.events.explosives;

import com.j0ach1mmall3.wastedguns.api.events.WeaponEvent;
import net.grandtheftmc.guns.weapon.Weapon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

import java.util.Collection;

public class ExplosionDamageEntityEvent extends WeaponEvent{
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private Entity projectile;
    private Collection<LivingEntity> victims;
    private boolean cancelled;
    private final LivingEntity shooter;

    public ExplosionDamageEntityEvent(LivingEntity shooter, Entity projectile, Collection<LivingEntity> victims, Weapon weapon) {
        super(shooter, weapon);
        this.projectile = projectile;
        this.victims = victims;
        this.shooter = shooter;
    }

    public Entity getProjectile() {
        return projectile;
    }

    public Collection<LivingEntity> getVictims() {
        return this.victims;
    }

    public void setVictims(Collection<LivingEntity> victims) {
        this.victims = victims;
    }


    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }


    public LivingEntity getShooter() {
        return shooter;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
