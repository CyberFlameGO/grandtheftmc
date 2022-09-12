package net.grandtheftmc.guns.cache;

import net.grandtheftmc.guns.WeaponCooldown;
import net.grandtheftmc.guns.WeaponState;
import net.grandtheftmc.guns.weapon.Weapon;
import org.bukkit.entity.LivingEntity;

/**
 * Created by Luke Bingham on 19/07/2017.
 */
public class WeaponCache {
    public WeaponCooldown cooldown;
    public int burstShots, burstTicks, clip = -1;
    public LivingEntity homingEntity;
    public WeaponState weaponState = WeaponState.NONE;

    public Weapon<?> weapon = null;
}
