package net.grandtheftmc.gtm.drugs.events.listener;

import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.DrugCommand;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.LockedBlocks;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.users.HouseUser;

public class DrugPlacementListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Player player = event.getPlayer();
        if (player.getWorld().getName().equals("spawn")) return;
        Block block = event.getClickedBlock();
        Location blockLocation = block.getLocation();
        HouseUser houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        ItemStack hand = player.getInventory().getItemInMainHand();
        
        // if right clicking a block
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
        	
        	// IF BONG
            if (block.getType() == Material.BREWING_STAND) {
            	
            	// stop vanilla interaction
                event.setCancelled(true);
                
                // if this is a locked block
                LockedBlocks lockedBlocks = GTM.getDrugManager().getLockedBlocks();
                if (DrugCommand.addingBlocks.contains(player.getName())) {
                    if (lockedBlocks.getLocations().contains(block.getLocation())) {
                        player.sendMessage(Lang.DRUGS.f("&cBlock is no longer locked."));
                        lockedBlocks.getLocations().remove(block.getLocation());
                    } else {
                        player.sendMessage(Lang.DRUGS.f("&aBlock is now locked!"));
                        lockedBlocks.getLocations().add(block.getLocation());
                    }
                    return;
                }
                
                // if sneaking, we want to remove
                if (player.isSneaking()) {
                    if (lockedBlocks.getLocations().contains(block.getLocation())) {
                        player.sendMessage(Lang.DRUGS.f("&7This Bong cannot be removed."));
                        return;
                    }
                    if (houseUser.isInsidePremiumHouse()) {
                        PremiumHouse house = Houses.getHousesManager().getPremiumHouse(houseUser.getInsidePremiumHouse());
                        if (!house.getOwner().equals(player.getUniqueId())) {
                            player.sendMessage(Lang.GTM.f("&7Only the house owner may place a Bong here!"));
                            return;
                        }
                    }
                    block.getWorld().dropItemNaturally(blockLocation, getBongItem());
                    block.setType(Material.AIR);
                    player.playSound(blockLocation, Sound.BLOCK_GLASS_BREAK, 3.0F, 3.0F);
                } 
                else {
                    if (hand.getType() == getWeedItem().getType()) {
                        Optional<Drug> weed = ((DrugService) GTM.getDrugManager().getService()).getDrug("weed");
                        if (weed.isPresent()) {
                            weed.get().apply(player);
                            if (hand.getAmount() > 1) {
                                hand.setAmount(hand.getAmount() - 1);
                            } else {
                                player.getInventory().remove(hand);
                            }
                        } else {
                            player.sendMessage(Lang.DRUGS + "" + ChatColor.RED + "Something went wrong internally, please tell a staff member.");
                            GTM.getInstance().getLogger().log(Level.SEVERE, "Unable to find weed drug (DrugPlacementListener)");
                        }
                    } else {
                        player.sendMessage(Lang.DRUGS.f("&7Put some &2&lWeed &7in here to start smoking!"));
                    }
                }
            } 
            // IF COCAINE
            else if (block.getType() == getCocaineBlock().getType() && block.getData() == getCocaineBlock().getData().getData()) {
                
            	// stop vanilla interaction
            	event.setCancelled(true);
            	
            	// if sneaking, we want to remove
                if (player.isSneaking()) {
                    if (houseUser.isInsidePremiumHouse()) {
                        PremiumHouse house = Houses.getHousesManager().getPremiumHouse(houseUser.getInsidePremiumHouse());
                        if (!house.getOwner().equals(player.getUniqueId())) {
                            player.sendMessage(Lang.GTM.f("&7Only the house owner may do this!"));
                            return;
                        }
                    }
                    
                    player.sendMessage(Lang.GTM.f("&7You quickly gather up the cocaine."));
                    
                    block.getWorld().dropItemNaturally(blockLocation, getCocaineItem());
                    block.setType(Material.AIR);
                    player.playSound(blockLocation, Sound.BLOCK_GLASS_BREAK, 3.0F, 3.0F);
                } 
                else {
                	
                	// apply the cocaine
                    block.setType(Material.AIR);
                    player.playSound(blockLocation, Sound.ENTITY_CAT_HISS, 1, 1);
                    Optional<Drug> cocaine = ((DrugService) GTM.getDrugManager().getService()).getDrug("cocaine");
                    if (cocaine.isPresent()) {
                        cocaine.get().apply(player);
                    } else {
                        player.sendMessage(Lang.DRUGS.f("&7Unable to locate drug cocaine, report this bug to a staff member."));
                    }
                }
            } else {
                Block target = block.getWorld().getBlockAt(blockLocation.getBlockX(),
                        blockLocation.getBlockY() + 1,
                        blockLocation.getBlockZ());
                if (hand.getType() == getBongItem().getType()
                        && hand.getData().getData() == getBongItem().getData().getData()) {
                    if (target.getType() == Material.AIR) {
                        if (houseUser.isInsidePremiumHouse()) {
                            PremiumHouse house = Houses.getHousesManager().getPremiumHouse(houseUser.getInsidePremiumHouse());
                            if (!house.getOwner().equals(player.getUniqueId())) {
                                player.sendMessage(Lang.GTM.f("&7Only the house owner may do this!"));
                                return;
                            }
                        }
                        target.setType(Material.BREWING_STAND);
                        if (hand.getAmount() > 1) {
                            hand.setAmount(hand.getAmount() - 1);
                        } else {
                            player.getInventory().remove(hand);
                        }
                        player.updateInventory();
                        player.playSound(target.getLocation(), Sound.BLOCK_GLASS_PLACE, 3.0F, 3.0F);
                    } else if (target.getType() != Material.BREWING_STAND) {
                        player.sendMessage(Lang.GTM.f("&7Bong cannot be placed here!"));
                    }
                } else if (hand.getType() == getCocaineItem().getType()
                        && hand.getData().getData() == getCocaineItem().getData().getData()) {
                    if (target.getType() == Material.AIR) {
                        if (houseUser.isInsidePremiumHouse()) {
                            PremiumHouse house = Houses.getHousesManager().getPremiumHouse(houseUser.getInsidePremiumHouse());
                            if (!house.getOwner().equals(player.getUniqueId())) {
                                player.sendMessage(Lang.GTM.f("&7Only the house owner may do this!"));
                                return;
                            }
                        }
                        if(block.getType()==Material.JUKEBOX) {
                            player.sendMessage(Lang.GTM.f("&7That cocaine cannot be placed here!"));
                            event.setCancelled(true);
                            return;
                        }
                        target.setType(getCocaineBlock().getType());
                        target.setData(getCocaineBlock().getData().getData());
                        hand.setAmount(hand.getAmount()-1);
                        player.updateInventory();
                        player.playSound(target.getLocation(), Sound.BLOCK_GLASS_PLACE, 3.0F, 3.0F);
                    } else if (target.getType() != getCocaineBlock().getType()) {
                        player.sendMessage(Lang.GTM.f("&7That cocaine cannot be placed here!"));
                    }
                }
            }
        }
    }

    public ItemStack getWeedItem() {
        return GTM.getItemManager().getItem("weed").getItem();
    }

    public ItemStack getBongItem() {
        return GTM.getItemManager().getItem("bong").getItem();
    }

    public ItemStack getCocaineItem() {
        return GTM.getItemManager().getItem("cocaine").getItem();
    }

    public ItemStack getCocaineBlock() {
        return GTM.getItemManager().getItem("cocaineblock").getItem();
    }
}
