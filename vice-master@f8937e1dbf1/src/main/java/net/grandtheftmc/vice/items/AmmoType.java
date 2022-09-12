package net.grandtheftmc.vice.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.vice.Vice;

import java.util.Arrays;

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

    public String getGameItemName() {
        return this.gameItem;
    }

    public GameItem getGameItem() {
        return Vice.getItemManager().getItem(this.gameItem);
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

    public static AmmoType getAmmoType(ItemStack itemStack) {
        for (AmmoType type : AmmoType.getTypes()) {
            ItemStack item = type.getGameItem().getItem();
            if (itemStack.getType() == item.getType() && itemStack.getDurability() == item.getDurability() && (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().contains("Ammo")))
                return type;
        }
        return null;
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
