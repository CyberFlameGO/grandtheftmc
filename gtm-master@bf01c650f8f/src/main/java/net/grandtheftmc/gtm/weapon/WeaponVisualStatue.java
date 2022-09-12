package net.grandtheftmc.gtm.weapon;

import net.grandtheftmc.core.util.C;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.guns.weapon.Weapon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;

public interface WeaponVisualStatue {

    Location spawnVisual(World world);

    Location getOrigin(World world);

    default ArmorStand spawnEntity(Location location, Weapon<?> weapon, VisualType visualType) {
        ArmorStand entity = location.getWorld().spawn(location, ArmorStand.class);
        if (visualType == VisualType.NAME && weapon != null) {
//            entity.setCustomName(C.YELLOW + C.BOLD + weapon.getName());
//            entity.setCustomNameVisible(true);
            entity.setMetadata("statueview", new FixedMetadataValue(GTM.getInstance(), weapon));
        }
        else if (visualType == VisualType.PRICE && weapon != null) {
//            double price = 0.0;
//            entity.setCustomName(C.GREEN + C.BOLD + "$" + price);
//            entity.setCustomNameVisible(true);
        }

        entity.setGravity(false);
        entity.setRemoveWhenFarAway(false);
        entity.setAI(false);
//        entity.setInvulnerable(true);
        entity.setBasePlate(true);
        entity.setVisible(false);
        entity.setInvulnerable(true);
//        entity.setCollidable(false);
        entity.setMetadata("statue", new FixedMetadataValue(GTM.getInstance(), weapon));
        extras(entity);

        return entity;
    }

    default void extras(ArmorStand entity) {}

    public static enum VisualType {
        NAME,
        PRICE,
        NONE,
        ;
    }
}
