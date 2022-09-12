
package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.items.recipetypes.CraftingRecipeItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Lampen on 7/10/2017.
 */
public class SmeltItem implements Listener{
    @EventHandler
    public void onSmelt(FurnaceSmeltEvent event){
        ItemStack result = event.getResult();
        CraftingRecipeItem craftingRecipeItem = (CraftingRecipeItem)Vice.getItemManager().getCustomRecipes().getOrDefault(result, null);
        if(craftingRecipeItem ==null)
            return;
        if(!craftingRecipeItem.validate(new ItemStack[]{event.getSource()})){
            event.setCancelled(true);
            return;
        }
    }
}