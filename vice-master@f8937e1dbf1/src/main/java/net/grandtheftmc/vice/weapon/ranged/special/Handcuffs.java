package net.grandtheftmc.vice.weapon.ranged.special;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.MeleeWeapon;
import net.grandtheftmc.guns.weapon.WeaponType;

/**
 * Created by Timothy Lampen on 2017-11-22.
 */
public class Handcuffs extends MeleeWeapon {
    public Handcuffs() {
        super((short) 57,
                "Handcuffs",
                WeaponType.MELEE,
                AmmoType.MELEE,
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 1003).build(),
                new Sound[] {
                Sound.ENTITY_SKELETON_SHOOT,
                Sound.ITEM_ARMOR_EQUIP_GENERIC,
                Sound.ITEM_ARMOR_EQUIP_GENERIC
        });
        setOldItemStack(new ItemStack(Material.ACACIA_DOOR));
        setDescription("Multipurpose tool", "for work or for kink");


        this.delay = 10;
        this.meleeDamage = 1.0;
    }

//    @Override
//    public void onHit(EntityDamageByEntityEvent event){
//        if(event.getDamager() instanceof Player && event.getEntity() instanceof Player){
//            Player player = (Player)event.getDamager();
//            Player victim = (Player)event.getEntity();
//            GTMUtils.arrestPlayer(event, this, player, victim);
//        }
//    }
}
