package net.grandtheftmc.guns.cache;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;

import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;

/**
 * Created by Luke Bingham on 19/07/2017.
 */
public class PlayerCache {
    private final HashSet<Weapon<?>> playerWeapons;
    public final Set<Entity> stickyBombs;

    public boolean shooting = false;
    public int burst = 0, tick = 0;

    public PlayerCache() {
        this.playerWeapons = Sets.newHashSet();
        this.stickyBombs = Sets.newHashSet();
    }

    public HashSet<Weapon<?>> getPlayerWeapons() {
        return playerWeapons;
    }

    public boolean hasWeapon(ItemStack itemStack) {      
        if(itemStack == null || itemStack.getType() != Material.DIAMOND_SWORD) return false;
        
        return playerWeapons.stream().anyMatch(weapon -> {
            if (weapon.getWeaponSkins() != null) {
                for (WeaponSkin skin : weapon.getWeaponSkins()) {
                    if (skin.getIdentifier() == itemStack.getDurability()) {
                        return true;
                    }
                }
            }

            return weapon.getWeaponIdentifier() == itemStack.getDurability();
        });
    }

    public Weapon<?> getCachedWeapon(ItemStack itemStack) {
        if(itemStack == null || itemStack.getType() != Material.DIAMOND_SWORD) return null;
        return this.getCachedWeapon(itemStack.getDurability());
    }

    public Weapon<?> getCachedWeapon(short identifier) {
        Optional<Weapon<?>> optional = playerWeapons.stream().filter(weapon -> {
            if (weapon.getWeaponSkins() != null) {
                for (WeaponSkin skin : weapon.getWeaponSkins()) {
                    if (skin.getIdentifier() == identifier) {
                        return true;
                    }
                }
            }
            
            return weapon.getWeaponIdentifier() == identifier;    
        }).findFirst();
        
        return optional.orElse(null);
    }

    public Weapon<?> getOrAddWeapon(ItemStack itemStack) {
        if(itemStack == null || itemStack.getType() != Material.DIAMOND_SWORD) return null;
        Weapon<?> weapon = getCachedWeapon(itemStack);
        if(weapon == null) {
            Optional<Weapon<?>> optional = GTMGuns.getInstance().getWeaponManager().getWeapon(itemStack.getDurability());
            weapon = optional.<Weapon<?>>map(Weapon::clone).orElse(null);
            if(weapon != null) playerWeapons.add(weapon);
        }
        return weapon;
    }

    public Weapon<?> getOrAddWeapon(short identifier) {
        Weapon<?> weapon = getCachedWeapon(identifier);
        if(weapon == null) {
            Optional<Weapon<?>> optional = GTMGuns.getInstance().getWeaponManager().getWeapon(identifier);
            weapon = optional.<Weapon<?>>map(Weapon::clone).orElse(null);
            if(weapon != null) playerWeapons.add(weapon);
        }
        return weapon;
    }
}
