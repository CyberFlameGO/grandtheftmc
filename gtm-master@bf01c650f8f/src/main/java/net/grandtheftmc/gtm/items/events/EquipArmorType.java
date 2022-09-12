package net.grandtheftmc.gtm.items.events;

import net.grandtheftmc.gtm.listeners.ArmorEquip;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Created by Timothy Lampen on 2017-08-11.
 */
public enum EquipArmorType {

    HELMET(39), CHESTPLATE(38), LEGGINGS(37), BOOTS(36), CUSTOM(-1);

    private final int slot;

    EquipArmorType(int slot){
        this.slot = slot;
    }


    public static EquipArmorType fromSlot(int slot){
        for(EquipArmorType t : EquipArmorType.values())
            if(t.getSlot()==slot)
                return t;
        return null;
    }

    /**
     * Attempts to match the ArmorType for the specified ItemStack.
     *
     * @param itemStack The ItemStack to parse the type of.
     * @return The parsed ArmorType. (null if none were found.)
     */
    public static EquipArmorType matchType(final ItemStack itemStack){
        if(itemStack == null) { return null; }
        if(itemStack.getType().toString().contains("LEATHER") && ArmorEquip.isCustomColor(((LeatherArmorMeta)itemStack.getItemMeta()).getColor()))
            return CUSTOM;
        switch (itemStack.getType()){
            case DIAMOND_SWORD:
                return CUSTOM;
            case DIAMOND_HELMET:
            case GOLD_HELMET:
            case IRON_HELMET:
            case CHAINMAIL_HELMET:
            case LEATHER_HELMET:
                return HELMET;
            case DIAMOND_CHESTPLATE:
            case GOLD_CHESTPLATE:
            case IRON_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case LEATHER_CHESTPLATE:
                return CHESTPLATE;
            case DIAMOND_LEGGINGS:
            case GOLD_LEGGINGS:
            case IRON_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case LEATHER_LEGGINGS:
                return LEGGINGS;
            case DIAMOND_BOOTS:
            case GOLD_BOOTS:
            case IRON_BOOTS:
            case CHAINMAIL_BOOTS:
            case LEATHER_BOOTS:
                return BOOTS;
            default:
                return null;
        }
    }

    public int getSlot(){
        return slot;
    }

}
