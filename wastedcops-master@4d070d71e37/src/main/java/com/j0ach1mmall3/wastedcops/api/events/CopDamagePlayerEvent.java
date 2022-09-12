package com.j0ach1mmall3.wastedcops.api.events;

import com.j0ach1mmall3.wastedcops.api.Cop;
import com.j0ach1mmall3.wastedguns.api.weapons.Weapon;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class CopDamagePlayerEvent extends CopEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private Player victim;
    private Weapon weapon;
    private double damageDealt;

    public CopDamagePlayerEvent(Cop cop, Player victim, Weapon weapon, double damageDealt) {
        super(cop);
        this.victim = victim;
        this.weapon = weapon;
        this.damageDealt = damageDealt;
    }

    public Player getVictim() {
        return this.victim;
    }

    public Weapon getWeapon() {
        return this.weapon;
    }

    public double getDamageDealt(){
        return this.damageDealt;
    }

    public void setDamageDealt(double newDamage) {
        if(newDamage > victim.getMaxHealth() || newDamage < 0)
            newDamage = victim.getMaxHealth();
        this.damageDealt = newDamage;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}