package net.grandtheftmc.core.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.util.UUID;

public class SignChange implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        User u = Core.getUserManager().getLoadedUser(uuid);
        if (Core.getWorldManager().usesEditMode(player.getWorld().getName()) && !u.hasEditMode()) {
            e.setCancelled(true);
            return;
        }
        if (u.isSpecial())
            for (int i = 0; i < 4; i++) {
                e.setLine(i, Utils.f(e.getLine(i)));
            }
            /*
        if (e.getBlock().getType() != Material.WALL_SIGN) return;
        String line = ChatColor.stripColor(e.getLine(0));
        if (!line.startsWith("[") || !line.endsWith("]")) return;
        String serverName = ChatColor.stripColor(e.getLine(0)).replace("[", "").replace("]", "").toLowerCase();
        ServerManager sm = Core.getServerManager();
        Server server = sm.getServer(serverName);
        if (server == null) {
            server = new Server(serverName, null, -1, null, -1, true, 0, 0, null, null, 0, null, Collections.singletonList(e.getBlock().getLocation()));
            sm.getServers().add(server);
        } else server.getJoinSigns().add(e.getBlock().getLocation());
        e.getPlayer().sendMessage(Utils.f("You added a join sign for server " + server.getName() + '.'));*/
    }
}
