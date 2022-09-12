package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.items.ArmorUpgrade;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * Created by Timothy Lampen on 7/20/2017.
 */
public class GamemodeChange implements Listener {

    @EventHandler
    public void onChange(PlayerGameModeChangeEvent event){
        Player player = event.getPlayer();

        if(event.getNewGameMode()== GameMode.CREATIVE){
            player.setMaxHealth(20);
            player.getActivePotionEffects().stream().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }
        else if(event.getNewGameMode()!=GameMode.CREATIVE){
            int amtOfEnchaned = 0;
            ItemStack[] armor = player.getInventory().getArmorContents();
            for(ItemStack piece : armor){
                if(piece==null)
                    continue;
                for(ArmorUpgrade upgrade : ArmorUpgrade.getArmorUpgrades(piece)){
                    if(upgrade==ArmorUpgrade.ENHANCED){
                        player.setMaxHealth(player.getMaxHealth()+5);
                    }
                    for(PotionEffect effect : upgrade.getPotionEffects()){
                        player.addPotionEffect(effect);
                    }
                }
                amtOfEnchaned += ArmorUpgrade.getArmorUpgrades(piece).contains(ArmorUpgrade.ENHANCED) ? 10 : 0;
            }
            player.setMaxHealth(20 + amtOfEnchaned);
            player.setHealth(player.getMaxHealth());
        }
    }
}
