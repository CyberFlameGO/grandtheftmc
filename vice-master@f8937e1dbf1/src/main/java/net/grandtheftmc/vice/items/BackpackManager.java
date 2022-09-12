package net.grandtheftmc.vice.items;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.users.CopRank;
import net.grandtheftmc.vice.users.ViceRank;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BackpackManager implements Listener {

    private final Map<Integer, Inventory> corpses = new HashMap<>();

    public void openBackpack(Player player) {
        this.openBackpack(player, Vice.getUserManager().getLoadedUser(player.getUniqueId()), Core.getUserManager().getLoadedUser(player.getUniqueId()));
    }

    public Inventory getBackpack(Player player, boolean monitor) {
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        User coreUser = Core.getUserManager().getLoadedUser(player.getUniqueId());
        int size = 9 * ViceUtils.getBackpackRows(coreUser.getUserRank());
        Inventory inv = Bukkit.createInventory(null, size, monitor ? Utils.f(player.getName()) : Utils.f("&6&lBackpack"));
        ItemStack[] backpackContents = viceUser.getBackpackContents();
        if (backpackContents != null)
            for (int i = 0; i < backpackContents.length && i < size; i++)
                inv.setItem(i, backpackContents[i]);
        return inv;
    }

    public void openBackpack(Player player, ViceUser user, User u) {
        if (user.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't open your backpack in jail!"));
            return;
        }
        if (user.isInCombat()) {
            player.sendMessage(Lang.COMBATTAG.f("&7You can't open your backpack in combat!"));
            return;
        }
        if (user.getBooleanFromStorage(BooleanStorageType.BACKPACK_OPEN)) {
            player.sendMessage(Lang.VICE.f("&7Your backpack may not be opened at this time!"));
            return;
        }
        if (player.getOpenInventory() != null
                && Objects.equals("Backpack", ChatColor.stripColor(player.getOpenInventory().getTitle())))
            return;
        Inventory inv = this.getBackpack(player, false);
        player.openInventory(inv);
        user.setBooleanToStorage(BooleanStorageType.BACKPACK_OPEN, true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if (!Objects.equals("Backpack", ChatColor.stripColor(inv.getTitle())) && Bukkit.getPlayer(inv.getTitle()) != null) {
            Player target = Bukkit.getPlayer(inv.getTitle());
            if (target.getOpenInventory() != null && Objects.equals("Backpack", ChatColor.stripColor(target.getOpenInventory().getTitle())))
                target.getOpenInventory().close();
            ViceUser user = Vice.getUserManager().getLoadedUser(target.getUniqueId());
            user.setBackpackContents(inv.getContents());
            user.setBooleanToStorage(BooleanStorageType.BACKPACK_OPEN, false);
            return;
        }
        if (!"backpack".equalsIgnoreCase(ChatColor.stripColor(inv.getTitle())))
            return;
        Player player = (Player) e.getPlayer();
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        user.setBackpackContents(inv.getContents());
        user.setBooleanToStorage(BooleanStorageType.BACKPACK_OPEN, false);
    }

    private final int[] glassSlots = new int[]{0, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private final ItemStack glass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");

    public void kitPreview(Player player, Kit kit) {
        String name = kit.getName();
        UserRank ur = UserRank.getUserRankOrNull(kit.getName());
        ViceRank rank = ViceRank.getRankOrNull(kit.getName());
        CopRank copRank = CopRank.getRankOrNull(kit.getName());
        if (copRank != null)
            name = copRank.getColoredNameBold();
        else if (ur != null)
            name = ur.getColoredNameBold();
        else if (rank != null)
            name = rank.getColoredNameBold();
        Inventory inv = Bukkit.createInventory(null, 54, Utils.f("&b&lKit Preview: " + name));

        for (int i : this.glassSlots)
            inv.setItem(i, this.glass);
        inv.setItem(1, kit.getHelmet() == null ? this.glass : kit.getHelmet().getItem().getItem());
        inv.setItem(2, kit.getChestPlate() == null ? this.glass : kit.getChestPlate().getItem().getItem());
        inv.setItem(3, kit.getLeggings() == null ? this.glass : kit.getLeggings().getItem().getItem());
        inv.setItem(4, kit.getBoots() == null ? this.glass : kit.getBoots().getItem().getItem());
        inv.setItem(6, kit.getOffHand() == null ? this.glass : kit.getOffHand().getItem().getItem());
        for (int i = 0; i < kit.getItems().size(); i++) {
            if (i < 9)
                inv.setItem(45 + i, kit.getItems().get(i));
            else
                inv.setItem(9 + i, kit.getItems().get(i));
        }
        player.openInventory(inv);
    }

}
