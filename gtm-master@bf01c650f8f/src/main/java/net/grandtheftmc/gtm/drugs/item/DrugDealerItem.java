package net.grandtheftmc.gtm.drugs.item;

import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.DrugUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Timothy Lampen on 2017-04-01.
 */
public class DrugDealerItem {
    private final DrugItem item;
    private final int minAmount, maxAmount, displayChance, minPrice, maxPrice;
    private int stockRemaining;
    private boolean shouldDisplay;
    private ItemStack is;
    private int price;

    public DrugDealerItem(DrugItem item, int minAmount, int maxAmount, int displayChance, int minPrice, int maxPrice) {
        this.item = item;
        this.minAmount = minAmount > maxAmount ? (maxAmount > 0 ? maxAmount - 1 : 0) : minAmount > -1 ? minAmount : 0;
        this.maxAmount = maxAmount > 0 ? maxAmount + 1 : 1;
        this.displayChance = displayChance <= -1 ? 0 : displayChance;
        this.minPrice = minPrice > maxPrice ? (maxPrice > 0 ? maxPrice - 1 : 0) : minPrice > -1 ? minPrice : 0;
        this.maxPrice = maxPrice > 0 ? maxPrice : 1;
        ItemStack is = item.getItemStack().clone();
        this.is = is;
        reroll();
    }

    public static Optional<DrugDealerItem> byDrugItem(DrugItem item) {
        return GTM.getDrugManager().getDrugDealer().getItems().stream().filter(drugDealerItem -> drugDealerItem.item.equals(item)).findFirst();
    }

    public int getStockRemaining() {
        return stockRemaining;
    }

    public void setStockRemaining(int stockRemaining) {
        this.stockRemaining = stockRemaining;
        ItemMeta im = is.getItemMeta();
        List<String> lore = im.getLore();
        lore.set(0, ChatColor.GOLD + "Amount Left: " + ChatColor.YELLOW + stockRemaining);
        im.setLore(lore);
        is.setItemMeta(im);
    }

    public int getPrice() {
        return this.price;
    }

    public void reroll() {
        stockRemaining = ThreadLocalRandom.current().nextInt(this.minAmount, this.maxAmount);
        shouldDisplay = ThreadLocalRandom.current().nextInt(101) <= displayChance;
        this.price = ThreadLocalRandom.current().nextInt(this.minPrice, this.maxPrice);
        ItemMeta im = is.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GOLD + "Amount Left: " + ChatColor.YELLOW + stockRemaining);
        lore.add(ChatColor.GOLD + "Price: " + ChatColor.YELLOW + "$" + this.price);
        im.setLore(lore);
        is.setItemMeta(im);
    }

    public boolean isShouldDisplay() {
        return shouldDisplay;
    }

    public ItemStack getItemStack() {
        return DrugUtil.hideDurability(this.is);
    }
}
