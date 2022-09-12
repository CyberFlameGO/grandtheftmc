package net.grandtheftmc.vice.drugs.events.listener;

import org.bukkit.event.Listener;

public class DrugPlacementListener implements Listener {

    /*@EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Player player = event.getPlayer();
        if (Objects.equals(player.getWorld().getName(), "spawn")) return;
        Block block = event.getClickedBlock();
        Location blockLocation = block.getLocation();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (block.getType() == Material.BREWING_STAND) {
                event.setCancelled(true);
                LockedBlocks lockedBlocks = Vice.getDrugManager().getLockedBlocks();
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
                if (player.isSneaking()) {
                    if (lockedBlocks.getLocations().contains(block.getLocation())) {
                        player.sendMessage(Lang.DRUGS.f("&7This Bong cannot be removed."));
                        return;
                    }

                    block.getWorld().dropItemNaturally(blockLocation, getBongItem());
                    block.setType(Material.AIR);
                    player.playSound(blockLocation, Sound.BLOCK_GLASS_BREAK, 3.0F, 3.0F);
                } else {
                    if (hand.getType() == getWeedItem().getType()) {
                        Optional<Drug> weed = ((DrugService) Vice.getDrugManager().getService()).getDrug("weed");
                        if (weed.isPresent()) {
                            weed.get().apply(player);
                            if (hand.getAmount() > 1) {
                                hand.setAmount(hand.getAmount() - 1);
                            } else {
                                player.getInventory().remove(hand);
                            }
                        } else {
                            player.sendMessage(Lang.DRUGS + "" + ChatColor.RED + "Something went wrong internally, please tell a staff member.");
                            Vice.getInstance().getLogger().log(Level.SEVERE, "Unable to find weed drug (DrugPlacementListener)");
                        }
                    } else {
                        player.sendMessage(Lang.DRUGS.f("&7Put some &2&lWeed &7in here to start smoking!"));
                    }
                }
            } else if (block.getType() == getCocaineBlock().getType() && block.getData() == getCocaineBlock().getData().getData()) {
                event.setCancelled(true);
                if (player.isSneaking()) {

                    block.getWorld().dropItemNaturally(blockLocation, getCocaineBlock());
                    block.setType(Material.AIR);
                    player.playSound(blockLocation, Sound.BLOCK_GLASS_BREAK, 3.0F, 3.0F);
                } else {
                    block.setType(Material.AIR);
                    player.playSound(blockLocation, Sound.ENTITY_CAT_HISS, 1, 1);
                    Optional<Drug> cocaine = ((DrugService) Vice.getDrugManager().getService()).getDrug("cocaine");
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
                /*if (hand.getType() == getBongItem().getType()
                        && hand.getData().getData() == getBongItem().getData().getData()) {
                    if (target.getType() == Material.AIR) {

                        target.setType(Material.BREWING_STAND);
                        if (hand.getAmount() > 1) {
                            hand.setAmount(hand.getAmount() - 1);
                        } else {
                            player.getInventory().remove(hand);
                        }
                        player.updateInventory();
                        player.playSound(target.getLocation(), Sound.BLOCK_GLASS_PLACE, 3.0F, 3.0F);
                    } else if (target.getType() != Material.BREWING_STAND) {
                        player.sendMessage(Lang.VICE.f("&7Bong cannot be placed here!"));
                    }
                } else if (hand.getType() == getCocaineItem().getType()
                        && hand.getData().getData() == getCocaineItem().getData().getData()) {
                    if (target.getType() == Material.AIR) {

                        target.setType(getCocaineBlock().getType());
                        target.setData(getCocaineBlock().getData().getData());
                        if (hand.getAmount() > 1) {
                            hand.setAmount(hand.getAmount() - 1);
                        } else {
                            player.getInventory().remove(hand);
                        }
                        player.updateInventory();
                        player.playSound(target.getLocation(), Sound.BLOCK_GLASS_PLACE, 3.0F, 3.0F);
                    } else if (target.getType() != getCocaineBlock().getType()) {
                        player.sendMessage(Lang.VICE.f("&7Bong cannot be placed here!"));
                    }
                }
        }
}

    }

    public ItemStack getWeedItem() {
        return Vice.getItemManager().getItem("weed").getItem();
    }

    public ItemStack getBongItem() {
        return Vice.getItemManager().getItem("bong").getItem();
    }

    public ItemStack getCocaineItem() {
        return Vice.getItemManager().getItem("cocaine").getItem();
    }

    public ItemStack getCocaineBlock() {
        return Vice.getItemManager().getItem("cocaineblock").getItem();
    }*/
}
