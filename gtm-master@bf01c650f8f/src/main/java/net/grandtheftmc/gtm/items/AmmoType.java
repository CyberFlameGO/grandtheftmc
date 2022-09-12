package net.grandtheftmc.gtm.items;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.gtm.GTM;

public enum AmmoType {

    PISTOL("pistolAmmo"), SMG("smgAmmo"), SHOTGUN("shotgunShell"), ASSAULT_RIFLE("assaultRifleAmmo"), MG(
            "mgAmmo"), SNIPER("sniperRifleAmmo"), ROCKET("rocket"), MINIGUN("minigunAmmo"), GRENADE("grenade", true);

    private final String gameItem;
    private final boolean inInventory;

    AmmoType(String gameItem) {
        this.gameItem = gameItem;
        this.inInventory = false;
    }

    AmmoType(String gameItem, boolean inInventory) {
        this.gameItem = gameItem;
        this.inInventory = inInventory;
    }
    
    /**
     * Get the identifier of the ammo type.
     * 
     * @return The string representation for the id for the ammo type.
     */
    public String getId(){
    	return name();
    }

    public String getGameItemName() {
        return this.gameItem;
    }

    public GameItem getGameItem() {
        return GTM.getItemManager().getItem(this.gameItem);
    }

    public static AmmoType[] getTypes() {
        return values();
    }

    public static AmmoType getAmmoType(Material material, short dataValue) {
        for (AmmoType type : AmmoType.getTypes()) {
            ItemStack item = type.getGameItem().getItem();
            if (material == item.getType() && dataValue == item.getDurability())
                return type;
        }
        return null;
    }
    
    /**
     * Get the ammo type based off the identifier.
     * 
     * @param id - the id to use
     * 
     * @return The ammo type, if found, otherwise empty.
     */
    public static Optional<AmmoType> getAmmoTypeByID(String id) {
    	
    	for (AmmoType at : values()){
    		if (at.getId().equalsIgnoreCase(id)){
    			return Optional.of(at);
    		}
    	}
    	
    	return Optional.empty();
    }

    public static AmmoType getAmmoType(String ammoType) {
        return Arrays.stream(AmmoType.getTypes()).filter(type -> type.toString().equalsIgnoreCase(ammoType)).findFirst().orElse(null);
    }

    public static AmmoType getAmmoTypeFriendly(String ammoType) {
        return Arrays.stream(AmmoType.getTypes()).filter(type -> type.toString().equalsIgnoreCase(ammoType) || type.gameItem.equalsIgnoreCase(ammoType)).findFirst().orElse(null);
    }

    public static boolean isAmmo(ItemStack item) {
        return getAmmoType(item.getType(), item.getDurability()) != null;
    }

    public boolean isInInventory() {
        return this.inInventory;
    }

}
