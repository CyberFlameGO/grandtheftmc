package net.grandtheftmc.gtm.drugs.events.listener;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.example.Alcohol;
import net.grandtheftmc.gtm.drugs.example.Cocaine;
import net.grandtheftmc.gtm.drugs.example.Heroin;
import net.grandtheftmc.gtm.drugs.example.Weed;
import net.grandtheftmc.gtm.drugs.item.DrugItem;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class DrugListener implements Listener {

    @EventHandler
    public void onEat(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack is = event.getItem();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (is != null && is.getItemMeta() != null) {
                DrugItem item = DrugItem.getByItemStack(is);
                DrugService drugService = (DrugService) GTM.getInstance().getDrugManager().getService();
                if (item != null) {
                    if (drugService.getDrugs().stream().anyMatch(item::isValid)) {
                        Drug drug = drugService.getDrug(item);
                        if (drug != null && !(drug instanceof Weed) && !(drug instanceof Cocaine)) {
                            if (drug.apply(player)) {
                                if (event.getItem().getAmount() > 1) {
                                    player.getInventory().getItemInMainHand().setAmount(event.getItem().getAmount() - 1);
                                } else {
                                    player.getInventory().setItemInMainHand(null);
                                }
                                event.setCancelled(true);
                            }
                        }
                    }
                } else {
                    if(GTM.getItemManager().getItem("vodka") != null) {
                        if (is.isSimilar(GTM.getItemManager().getItem("vodka").getItem())) {
                            Optional<Drug> alcohol = ((DrugService) GTM.getInstance().getDrugManager().getService()).getDrug("weed");
                            if (alcohol.isPresent()) {
                                ((Alcohol) alcohol.get()).potentApply(player, true);
                                if (event.getItem().getAmount() > 1) {
                                    player.getInventory().getItemInMainHand().setAmount(event.getItem().getAmount() - 1);
                                } else {
                                    player.getInventory().setItemInMainHand(null);
                                }
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

}
