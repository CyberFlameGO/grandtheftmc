package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import net.grandtheftmc.vice.world.obsidianbreaker.BlockStatus;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BreakBlock implements Listener{

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        switch (block.getType()) {
         case CHEST:
             if (Vice.getCrateManager().getCrate(block.getLocation()) == null) return;
             event.setCancelled(true);
             player.sendMessage(Lang.LOOTCRATES.f("&7You can't break this Loot Crate!"));
             break;
            case MOB_SPAWNER: {
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                if(user.getCheatCodeState(CheatCode.SILKSPAWNERS).getState()== State.ON) {
                    ItemStack hand = player.getInventory().getItemInMainHand();
                    if(hand!=null && hand.containsEnchantment(Enchantment.SILK_TOUCH)){
                        CreatureSpawner spawner = (CreatureSpawner)block.getState();
                        ItemStack is = new ItemStack(Material.MOB_SPAWNER);
                        ItemMeta im = is.getItemMeta();
                        im.setDisplayName(ChatColor.YELLOW + StringUtils.getCapitalized(spawner.getSpawnedType().toString().toLowerCase().replace("_", " ")) + ChatColor.GRAY + " spawner");
                        is.setItemMeta(im);
                        player.getWorld().dropItemNaturally(block.getLocation(), is);
                        event.setExpToDrop(0);
                    }
                }
            }
             default:
                 break;
         }
         if(!event.isCancelled()){
             BlockStatus status = Vice.getWorldManager().getObsidianManager().getDamageStorage().getBlockStatus(block, false);
             if(status!=null){
                 Vice.getWorldManager().getObsidianManager().getDamageStorage().removeBlockStatus(status);
                 Vice.getWorldManager().getObsidianManager().getNMSHandler().sendCrackEffect(block.getLocation(), -1);
             }


         }
     }
}
