package net.grandtheftmc.vice.items.recipetypes;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Lampen on 2017-08-06.
 */
public class SlotContainer {
    private char id;
    private ItemStack is;

    public SlotContainer(char id, ItemStack is){
        this.id = id;
        this.is = is;
    }

    public char getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return is;
    }
}