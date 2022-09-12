package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.Playtime;
import net.grandtheftmc.core.util.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlaytimeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if(Playtime.playtime.isEmpty()) {
            s.sendMessage("No data!");
            return true;
        }
        if(!(s instanceof Player)) {
            for (Map.Entry<String, Long> entrySet : Playtime.playtime.entrySet()) {
                TimeFormatter tf = Utils.timeFormatter(TimeUnit.MILLISECONDS, entrySet.getValue());
                s.sendMessage(Utils.f("&a&lPlaytime of &c&l" + tf.getHours() + "h &a" + tf.getMinutes() + "m"));
            }
            s.sendMessage(Utils.f("&7&lAnyone not listed here has not been online since the last server reboot."));
        } else {
            Player player = (Player) s;
            User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
            if(!player.hasPermission("gtmcore.playtime")) {
                player.sendMessage(Lang.NOPERM.f("&7You do not have permission to use this command."));
                return true;
            }
            Inventory inv = Bukkit.createInventory(null, 54, Utils.f("&e&lRecent Playtime"));

            ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
            ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 0, "&a");

            int[] offlineSlots = {0, 9, 18, 27, 36, 45, 8, 17, 26, 35, 44, 53};
            int[] whiteSlots = {1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52};
            int[] graySlots = {2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 20, 21, 22, 23, 24,
            29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51};
            int[] skullSlots = {11, 12, 13, 14, 15, 20, 21, 22, 23, 24,
                    29, 30, 31, 32, 33, 38, 39, 40, 41, 42};

            ArrayList<ItemStack> skulls = new ArrayList<>();
            ArrayList<ItemStack> offlineSkulls = new ArrayList<>();

            for (Map.Entry<String, Long> entrySet : Playtime.playtime.entrySet()) {
                String name = Bukkit.getPlayer(entrySet.getKey()) != null ?
                        "&a&l" + entrySet.getKey() : "&c&l" + entrySet.getKey();
                ItemStack skull = Utils.createItem(Material.SKULL_ITEM, 3, Utils.f(name));
                SkullMeta meta = (SkullMeta)skull.getItemMeta();

                List<String> lore = new ArrayList<>();
                TimeFormatter tf = Utils.timeFormatter(TimeUnit.MILLISECONDS, entrySet.getValue());
                lore.add(Utils.f("&7Playtime: &a" + tf.getHours() + "h " + tf.getMinutes() + "m"));

                meta.setOwner(entrySet.getKey());
                meta.setLore(lore);

                skull.setItemMeta(meta);

                skulls.add(skull);
            }

            Collection<String> offlineStaff = Utils.getOfflineStaff();
            offlineStaff.removeIf(offlinePlayer -> Playtime.playtime.containsKey(offlinePlayer));

            for(String offline : offlineStaff) {
                String name = "&8&l" + offline;
                ItemStack skull = Utils.createItem(Material.SKULL_ITEM, 3, Utils.f(name));
                SkullMeta meta = (SkullMeta)skull.getItemMeta();

                List<String> lore = new ArrayList<>();
                if(Bukkit.getPlayer(offline) != null) {
                    meta.setDisplayName(Utils.f("&a&l" + offline));
                    Player target = Bukkit.getPlayer(offline);
                    User coreUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
                    TimeFormatter tf = Utils.timeFormatter(TimeUnit.MILLISECONDS, System.currentTimeMillis() - coreUser.getJoinTime());
                    lore.add(Utils.f("&7Playtime: &a" + tf.getHours() + "h " + tf.getMinutes() + "m"));
                } else {
                    lore.add(Utils.f("&7Playtime: &4&lNONE - HAS NOT BEEN ONLINE"));
                }

                meta.setOwner(offline);
                meta.setLore(lore);

                skull.setItemMeta(meta);

                if(Bukkit.getPlayer(offline) == null) {
                    offlineSkulls.add(skull);
                } else {
                    skulls.add(skull);
                }
            }

            for(int skullSlot : skullSlots) {
                if(inv.firstEmpty() == -1) break;
                if(skulls.isEmpty()) break;
                if(inv.getItem(skullSlot) != null && inv.getItem(skullSlot).getType() == Material.SKULL) continue;
                ItemStack skull = skulls.stream().findFirst().get();
                inv.setItem(skullSlot, skull);
                skulls.remove(skulls.stream().findFirst().get());
            }

            for(int skullSlot : offlineSlots) {
                if(inv.firstEmpty() == -1) break;
                if(offlineSkulls.isEmpty()) break;
                if(inv.getItem(skullSlot) != null && inv.getItem(skullSlot).getType() == Material.SKULL) continue;
                ItemStack skull = offlineSkulls.stream().findFirst().get();
                inv.setItem(skullSlot, skull);
                offlineSkulls.remove(offlineSkulls.stream().findFirst().get());
            }

            for(int whiteSlot : whiteSlots)
                inv.setItem(whiteSlot, whiteGlass);

            for(int graySlot : graySlots) {
                if(inv.getItem(graySlot) != null) continue;
                inv.setItem(graySlot, grayGlass);
            }

            player.openInventory(inv);
        }
        return true;
    }
}