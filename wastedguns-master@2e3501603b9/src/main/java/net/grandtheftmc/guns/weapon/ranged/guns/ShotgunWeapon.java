package net.grandtheftmc.guns.weapon.ranged.guns;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.attribute.WeaponRPM;
import net.grandtheftmc.guns.weapon.ranged.RangedWeapon;

/**
 * Created by Luke Bingham on 21/07/2017.
 */
public class ShotgunWeapon extends RangedWeapon<ShotgunWeapon> implements WeaponRPM {
    protected int shellSize;

    /**
     * Construct a new Weapon.
     */
    public ShotgunWeapon(short uniqueIdentifier, String name, WeaponType weaponType, AmmoType ammoType, ItemStack itemStack, Sound[] sounds, Effect effect) {
        super(uniqueIdentifier, name, weaponType, ammoType, itemStack, sounds, effect);
    }

    @Override
    public int getRpm() {
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
	public int getRPS() {
		return 0;
	}

    @Override
    public boolean isAutomatic() {
        return false;
    }

    public int getShellSize() {
        return shellSize;
    }

    @Override
    public ShotgunWeapon clone() {
        ShotgunWeapon weapon = new ShotgunWeapon(getUniqueIdentifier(), getName(), getWeaponType(), getAmmoType(), getBaseItemStack().clone(), getSounds(), getEffect());
        weapon.oldItemStack = super.oldItemStack.clone();
        weapon.deathMessages = super.deathMessages;
        weapon.walkSpeed = super.walkSpeed;
        weapon.delay = super.delay;

        weapon.attachments = super.attachments;
        weapon.supportedAttachments = super.supportedAttachments;
        weapon.weaponSkins = super.weaponSkins;
        weapon.effect = super.effect;
        weapon.damage = super.damage;
        weapon.meleeDamage = super.meleeDamage;
        weapon.accuracy = super.accuracy;
        weapon.recoil = super.recoil;
        weapon.magSize = super.magSize;
        weapon.reloadTime = super.reloadTime;
        weapon.range = super.range;
        weapon.penetration = super.penetration;
        weapon.zoom = super.zoom;

        weapon.shellSize = this.shellSize;
        weapon.reloadShoot = super.reloadShoot;
        weapon.multiShoot = super.multiShoot;

        return weapon;
    }
}
