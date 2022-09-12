package net.grandtheftmc.Creative.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class PotionUse implements Listener {

	@EventHandler
    public void onThrow(PotionSplashEvent event) {
        event.setCancelled(true);
    }

	@EventHandler
    public void onDrink(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (!item.getType().equals(Material.POTION))
			return;
		PotionMeta pot = (PotionMeta) item.getItemMeta();
        for (PotionEffect eff : pot.getCustomEffects()) {
            if (eff.getAmplifier() < 0) event.setCancelled(true);
        }
    }
}
