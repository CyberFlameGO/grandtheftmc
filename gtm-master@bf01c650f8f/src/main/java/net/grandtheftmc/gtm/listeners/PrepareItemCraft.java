package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.gtm.GTM;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Arrays;

/**
 * Created by Timothy Lampen on 2017-04-05.
 */
public class PrepareItemCraft implements Listener {

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event){
        /*Recipe recipe = event.getRecipe();
        Bukkit.broadcastMessage("1");
        if(recipe.getResult().equals(GTM.getItemManager().getItem("joint"))){
            Bukkit.broadcastMessage("2");
            if(event.getInventory().getContents()!=null){
                Bukkit.broadcastMessage("4");

                ItemStack[] contents = event.getInventory().getContents();
                boolean isPossible = true;
                for(ItemStack item : contents){
                    if(item!=null) {
                        if (!(item.isSimilar(GTM.getItemManager().getItem("rollingpaper").getItem()) || item.isSimilar(GTM.getItemManager().getItem("groundweed").getItem()))) {
                            isPossible = false;
                        }
                    }
                }
                if(!isPossible){
                    event.getInventory().setResult(new ItemStack(Material.AIR));
                    Bukkit.broadcastMessage("3");

                }
            }
        }*/
    }
}
