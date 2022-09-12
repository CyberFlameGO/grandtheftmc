package net.grandtheftmc.houses.listeners;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Chat implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String msg = e.getMessage();
        HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
        if (user.isAddingGuest()) {
            e.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null)
                        return;

                    HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
                    if ("quit".equalsIgnoreCase(msg)) {
                        player.sendMessage(Utils.f(Lang.HOUSES + "&7You quit adding a guest!"));
                        user.setMenuHouseId(user.getAddingGuest());
                        user.setAddingGuest(-1);
                        MenuManager.openMenu(player, "guests");
                        return;
                    }

                    Player target = Bukkit.getPlayer(msg);
                    if (target == null) {
                        player.sendMessage(Utils.f(Lang.HOUSES + "&7That player is not online!"));
                        user.setMenuHouseId(user.getAddingGuest());
                        user.setAddingGuest(-1);
                        MenuManager.openMenu(player, "guests");
                        return;
                    }

                    PremiumHouse house = Houses.getManager().getPremiumHouse(user.getAddingGuest());
                    house.addGuest(player, target, user);
                    user.setMenuHouseId(user.getAddingGuest());
                    user.setAddingGuest(-1);
                    MenuManager.openMenu(player, "guests");
                }
            }.runTask(Houses.getInstance());
        }
    }
}
