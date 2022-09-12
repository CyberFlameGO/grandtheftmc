package net.grandtheftmc.vice.drugs.events.listener;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.DrugService;
import net.grandtheftmc.vice.drugs.events.DrugUseEvent;
import net.grandtheftmc.vice.drugs.example.Alcohol;
import net.grandtheftmc.vice.drugs.example.Heroin;
import net.grandtheftmc.vice.drugs.example.Weed;
import net.grandtheftmc.vice.drugs.items.DrugItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
                DrugService drugService = (DrugService) Vice.getInstance().getDrugManager().getService();
                if (item != null) {
                    if (drugService.getDrugs().stream().anyMatch(item::isValid)) {
                        Drug drug = drugService.getDrug(item);
                        if (drug != null && !(drug instanceof Weed)) {
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
                    if(Vice.getItemManager().getItem("vodka") != null) {
                        if (is.isSimilar(Vice.getItemManager().getItem("vodka").getItem())) {
                            Optional<Drug> alcohol = ((DrugService) Vice.getInstance().getDrugManager().getService()).getDrug("alcohol");
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

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player hurt = (Player) event.getEntity();
            ItemStack itemStack = damager.getInventory().getItemInMainHand();
            if (itemStack != null && itemStack.getItemMeta() != null) {
                DrugItem item = DrugItem.getByItemStack(itemStack);
                DrugService drugService = (DrugService) Vice.getInstance().getDrugManager().getService();
                if (item != null) {
                    if (drugService.getDrugs().stream().anyMatch(item::isValid)) {
                        Drug drug = drugService.getDrug(item);
                        if (drug instanceof Heroin) {
                            hurt.damage(6);
                            drug.apply(hurt);
                            hurt.sendMessage(Lang.DRUGS.f("&7You have been drugged."));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    protected final void onDrugUse(DrugUseEvent event) {
        if(event.getUser() == null || event.getDrug() == null) return;
        if(event.getUser().getLocation().getWorld().getName().equalsIgnoreCase("spawn")) {
            if(event.getDrug() instanceof Alcohol) {
                event.setCancelled(true);
                event.getUser().updateInventory();
            }
        }
    }
}
