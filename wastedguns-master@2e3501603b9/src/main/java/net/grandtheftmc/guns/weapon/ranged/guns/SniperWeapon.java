package net.grandtheftmc.guns.weapon.ranged.guns;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import net.grandtheftmc.guns.WeaponState;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.RangedWeapon;

/**
 * Created by Luke Bingham on 21/07/2017.
 */
public class SniperWeapon extends RangedWeapon<SniperWeapon> {

    /**
     * Construct a new Weapon.
     */
    public SniperWeapon(short uniqueIdentifier, String name, WeaponType weaponType, AmmoType ammoType, ItemStack itemStack, Sound[] sounds, Effect effect) {
        super(uniqueIdentifier, name, weaponType, ammoType, itemStack, sounds, effect);
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }

    @Override
    public void onSneak(Player player, boolean sneaking) {
        if(sneaking && this.weaponState != WeaponState.RELOADING) {
            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.SET_SLOT);
            packetContainer.getIntegers().write(0, 0);
            packetContainer.getIntegers().write(1, 5);
            packetContainer.getItemModifier().write(0, new ItemStack(Material.PUMPKIN));
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, this.zoom - 1, true, false));
            return;
        }

        player.removePotionEffect(PotionEffectType.SLOW);
        player.updateInventory();
    }

    @Override
    public SniperWeapon clone() {
        SniperWeapon weapon = new SniperWeapon(getUniqueIdentifier(), getName(), getWeaponType(), getAmmoType(), getBaseItemStack().clone(), getSounds(), getEffect());
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
        weapon.reloadShoot = super.reloadShoot;
        weapon.multiShoot = super.multiShoot;

        return weapon;
    }
}
