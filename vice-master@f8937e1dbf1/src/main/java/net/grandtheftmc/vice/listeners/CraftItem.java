package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.items.recipetypes.RecipeItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

/**
 * Created by Timothy Lampen on 7/10/2017.
 */
public class CraftItem implements Listener {

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe != null) {
            if (Vice.getItemManager().getBannedCraftingRecipes().contains(recipe.getResult().getType()) && !recipe.getResult().hasItemMeta()) {
                event.getInventory().setResult(new ItemStack(Material.AIR));
                return;
            }

            for (ItemStack itemStack : event.getInventory().getMatrix()) {
                if (itemStack != null && itemStack.getType() == Material.DIAMOND_SPADE && Vice.getItemManager().getItem(itemStack) != null && Vice.getItemManager().getItem(itemStack).getType() == GameItem.ItemType.DRUG) {
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    return;
                }
            }
        }

    }

    @EventHandler
    public void onInteract(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() != null && (event.getClickedInventory().getType() == InventoryType.WORKBENCH || event.getClickedInventory().getType() == InventoryType.CRAFTING) && event.getSlot() == 0) {
            CraftingInventory inv = (CraftingInventory) event.getClickedInventory();
            Optional<RecipeItem> craftingRecipeItem = Vice.getItemManager().getCraftingRecipe(inv.getMatrix());
            if (!craftingRecipeItem.isPresent())
                return;
            if (inv.getItem(0) == null) return;
            int amt = 0;
            event.setCancelled(true);
            if (event.getClick().toString().contains("SHIFT")) {
                for (int i = 0; i < (event.getClickedInventory().getType() == InventoryType.WORKBENCH ? 9 : 4); i++) {
                    ItemStack ingredient = inv.getMatrix()[i];
                    if (ingredient == null || ingredient.getType() == Material.AIR)
                        continue;
                    if (amt == 0 || ingredient.getAmount() < amt)
                        amt = ingredient.getAmount();
                }
            }
            ItemStack result = inv.getItem(0).clone();
            if (amt == 0)
                amt = 1;
            result.setAmount(amt);
            if (event.getClick().toString().contains("SHIFT")) {
                if (player.getInventory().firstEmpty() == -1) {
                    return;
                }
                player.getInventory().addItem(result);
            } else if (player.getItemOnCursor() != null && player.getItemOnCursor().getType() != Material.AIR) {
                if (player.getItemOnCursor().isSimilar(result)) {
                    if ((result.getAmount() + player.getItemOnCursor().getAmount()) > result.getMaxStackSize()) {
                        return;
                    } else {
                        result.setAmount(result.getAmount() + player.getItemOnCursor().getAmount());
                        player.setItemOnCursor(result);
                    }
                } else {
                    return;
                }
            } else {
                player.setItemOnCursor(result);
            }
            for (int i = 1; i < (inv.getType() == InventoryType.WORKBENCH ? 10 : 5); i++) {
                ItemStack is = inv.getItem(i);
                if (is == null || is.getType() == Material.AIR)
                    continue;
                if (is.getAmount() > amt) {
                    is.setAmount(is.getAmount() - amt);
                } else {
                    inv.setItem(i, null);
                }
            }
            if (!Vice.getItemManager().getCraftingRecipe(inv.getMatrix()).isPresent()) {
                inv.setResult(null);
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.updateInventory();
                }
            }.runTaskLater(Vice.getInstance(), 5);
        }
    }
}
