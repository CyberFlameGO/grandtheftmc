package net.grandtheftmc.gtm.armor;

import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.GameItem;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;

public abstract class ShopStatue {

    public abstract Location spawnVisual(World world);

    public abstract Location getOrigin(World world);

    public abstract String getGameItem();

    public ArmorStand spawnEntity(Location location, VisualType visualType) {
        ArmorStand entity = location.getWorld().spawn(location, ArmorStand.class);

        entity.setGravity(false);
        entity.setRemoveWhenFarAway(false);
        entity.setAI(false);
//        entity.setInvulnerable(true);
        entity.setBasePlate(true);
        entity.setVisible(false);
        entity.setInvulnerable(true);
//        entity.setCollidable(false);
        entity.setMetadata("armor-statue", new FixedMetadataValue(GTM.getInstance(), this));
        extras(entity);

        return entity;
    }

    public void extras(ArmorStand entity) {}

    public int getAmount() {
        return 1;
    }

    public static enum VisualType {
        NAME,
        PRICE,
        NONE,
        ;
    }
}
