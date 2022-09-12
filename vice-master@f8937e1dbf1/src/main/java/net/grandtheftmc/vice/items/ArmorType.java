package net.grandtheftmc.vice.items;

import net.grandtheftmc.core.util.Slot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Created by Timothy Lampen on 7/6/2017.
 */
public enum ArmorType {
    HELMET(Slot.HEAD, 5, "titaniumhelmet", "tacticalmask", "pimpcrown", "nightvisiongoggles", "baseballcap"),
    CHESTPLATE(Slot.CHEST, 6, "titaniumvest", "ceramicvest", "kevlarvest", "shirt"),
    LEGGINGS(Slot.LEGS, 7, "pants"),
    BOOTS(Slot.FEET, 8, "nikes", "samurisairjordans"),
    JETPACK(Slot.CHEST, 6, "jetpack"),
    WINGSUIT(Slot.CHEST, 6, "wingsuit");

    private final Slot slot;
    private final String[] gameItems;
    private final int rawSlot;

    ArmorType(Slot slot, int rawSlot, String... gameItems) {
        this.slot = slot;
        this.rawSlot =rawSlot;
        this.gameItems = gameItems;
    }

    public String getName() {
        String[] a = this.toString().split("_");
        String s = "";
        for (int i = 0; i < a.length; ++i) {
            s = s + a[i].charAt(0) + a[i].substring(1).toLowerCase() + (i == a.length - 1 ? "" : " ");
        }
        return s;
    }


    public Slot getSlot() {
        return this.slot;
    }

    public String[] getGameItems() {
        return this.gameItems;
    }

    public boolean hasGameItem(String gameItem) {
        return Arrays.stream(this.gameItems).anyMatch(s -> s.equalsIgnoreCase(gameItem));
    }

    public static ArmorType getArmorType(String gameItem) {
        return Arrays.stream(ArmorType.values()).filter(type -> type.hasGameItem(gameItem)).findFirst().orElse(null);
    }

    public int getRawSlot() {
        return rawSlot;
    }

    public static ArmorType matchType(ItemStack is){
        if(is==null)
            return null;
        String type = is.getType().toString().toLowerCase();
        if(type.contains("chestplate")){
            return ArmorType.CHESTPLATE;
        }
        else if(type.contains("leggings")){
            return ArmorType.LEGGINGS;
        }
        else if(type.contains("boots")){
            return ArmorType.BOOTS;
        }
        else if(type.contains("helmet")){
            return ArmorType.HELMET;
        }
        return null;
    }
}
