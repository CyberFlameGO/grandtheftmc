package net.grandtheftmc.core.casino.coins;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.factory.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by Timothy Lampen on 2017-11-12.
 */
public class VendorMenu extends CoreMenu{

    public VendorMenu() {
        super(1, ChatColor.GRAY + "Buy or Sell Casino Chips", CoreMenuFlag.CLOSE_ON_NULL_CLICK);

        ItemStack[] stacks = new ItemStack[]{
                new ItemFactory(Material.CHEST).setName(ChatColor.GREEN + "Buy Casino Chips").build(),
                new ItemFactory(Material.CHEST).setName(ChatColor.RED + "Sell Casino Chips").build()
        };

        addItem(new ClickableItem(3, stacks[0], (player, clickType) -> {
            new BuyChipMenu().openInventory(player);
        }));

        addItem(new ClickableItem(5, stacks[1], (player, clickType) -> {
            new SellChipMenu().openInventory(player);
        }));
    }

    private class SellChipMenu extends CoreMenu{

        private SellChipMenu() {
            super(1, ChatColor.RED + "Sell Casino Chips");
            int currentSlot = 2;
            ChipAmount[] values = ChipAmount.values();
            for(int i = values.length -1; i>= 0; i--){
                ChipAmount chip = values[i];
                ItemStack is = chip.getItemStack();
                ItemMeta im = is.getItemMeta();
                im.setLore(Arrays.asList(ChatColor.GRAY + "Sell Price: $" + ChatColor.GREEN + Utils.formatMoney(chip.getAmount()*Core.getCoinManager().getBaseCoinSellPrice())));
                is.setItemMeta(im);
                addItem(new ClickableItem(currentSlot, is, (player, clickType) -> {
                    if(Core.getCoinManager().hasCasinoChips(player, chip.getAmount())){
                        Core.getCoinManager().removeCasinoChips(player, chip.getAmount());
                        Core.getUserManager().getLoadedUser(player.getUniqueId()).addMoney(Core.getCoinManager().getBaseCoinSellPrice()*chip.getAmount());
                    }
                    else
                        player.sendMessage(Lang.CASINO.f("&cYou do not have enough chips to sell!"));
                }));
                currentSlot++;
            }
        }
    }

    private class BuyChipMenu extends CoreMenu {
        private BuyChipMenu() {
            super(1, ChatColor.GREEN + "Buy Casino Chips");
            int currentSlot = 1;
            ChipAmount[] values = ChipAmount.values();
            for (int i = values.length - 1; i >= 0; i--) {
                if (currentSlot == 4) {
                    currentSlot++;
                }
                ChipAmount chip = values[i];
                ItemStack is = chip.getItemStack();
                ItemMeta im = is.getItemMeta();
                im.setLore(Arrays.asList(ChatColor.GRAY + "Cost: $" + ChatColor.GREEN + Utils.formatMoney(Math.round(chip.getAmount() * Core.getCoinManager().getBaseCoinBuyPrice() * Core.getCoinManager().getCurrentMultiplier()))));
                is.setItemMeta(im);
                addItem(new ClickableItem(currentSlot, is, (player, clickType) -> {
                    User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                    if (user.hasMoney((int) Math.round(chip.getAmount() * Core.getCoinManager().getBaseCoinBuyPrice() * Core.getCoinManager().getCurrentMultiplier()))) {
                        Core.getCoinManager().giveCasinoChips(player, chip.getAmount());
                        Core.getCoinManager().addSoldCoins(chip.getAmount());
                        user.takeMoney(chip.getAmount() * Core.getCoinManager().getBaseCoinBuyPrice() * Core.getCoinManager().getCurrentMultiplier());
                    } else
                        player.sendMessage(Lang.CASINO.f("&7You need $&b" + chip.getAmount() * Core.getCoinManager().getBaseCoinBuyPrice() * Core.getCoinManager().getCurrentMultiplier()) + " &7to buy these chips!");
                    player.closeInventory();
                    new BuyChipMenu().openInventory(player);
                }));
                currentSlot++;
            }
            ItemStack is = new ItemStack(Material.REDSTONE);
            ItemMeta im = is.getItemMeta();
            DecimalFormat df = new DecimalFormat("#.#");
            im.setDisplayName(ChatColor.GRAY + "Current Cost Multiplier: " + ChatColor.GOLD + df.format(Core.getCoinManager().getCurrentMultiplier()) + ChatColor.GRAY + "x");
            is.setItemMeta(im);
            addItem(new MenuItem(4, is, false));
        }
    }
}
