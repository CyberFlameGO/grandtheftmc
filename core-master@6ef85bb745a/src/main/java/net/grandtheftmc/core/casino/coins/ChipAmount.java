package net.grandtheftmc.core.casino.coins;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Timothy Lampen on 2017-11-12.
 */
public enum ChipAmount {
    EIGHT_HUNDRED(ChatColor.LIGHT_PURPLE + "800", 800),
    THREE_HUNDRED(ChatColor.BLUE + "300", 300),
    HUNDRED(ChatColor.GREEN + "100", 100),
    FIFTY(ChatColor.YELLOW + "50", 50),
    TEN(ChatColor.GOLD + "10", 10),
    ONE(ChatColor.RED + "1", 1);


    private int amount;
    private String prefix;
    ChipAmount(String prefix, int amount){
        this.amount = amount;
        this.prefix = prefix;
    }

    public int getAmount() {
        return amount;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public static Optional<ChipAmount> getChipAmount(String name) {
        if(name==null)
            return Optional.empty();
        return Stream.of(ChipAmount.values()).filter(c -> name.contains(c.prefix)).findFirst();
    }

    public ItemStack getItemStack(){
        ItemStack is = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta im = is.getItemMeta();
        String disp = this.prefix;
        is.setDurability((short)1002);
        disp += ChatColor.GRAY + " Casino Coins";
        im.setUnbreakable(true);
        im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        im.setDisplayName(disp);
        is.setItemMeta(im);
        return is;
    }
}
