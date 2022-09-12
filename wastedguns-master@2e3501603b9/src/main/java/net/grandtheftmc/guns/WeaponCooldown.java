package net.grandtheftmc.guns;

import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponType;

/**
 * Created by Luke Bingham on 19/07/2017.
 */
public class WeaponCooldown {
    private final Weapon weapon;
    private long cooldown;

    public WeaponCooldown(Weapon weapon, long time) {
        this.weapon = weapon;
        this.cooldown = System.currentTimeMillis() + time;
    }

    public WeaponCooldown(Weapon weapon, int time) {
        this(weapon, (long) time);
    }

    public final Weapon getWeaponType() {
        return this.weapon;
    }

    public boolean hasElapsed() {
        return System.currentTimeMillis() >= this.cooldown;
    }
}
