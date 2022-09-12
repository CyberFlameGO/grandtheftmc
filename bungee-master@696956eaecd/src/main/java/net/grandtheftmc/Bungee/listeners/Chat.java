package net.grandtheftmc.Bungee.listeners;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.users.User;
import net.grandtheftmc.Bungee.users.UserRank;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Optional;

public class Chat implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        try {
            if (event.isCancelled() || !(event.getSender() instanceof ProxiedPlayer)) return;
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(player.getUniqueId());
            userOptional.ifPresent(user -> {
                if (!user.isAuthyVerified()) {
                    if (event.isCommand() && event.getMessage().startsWith("/authy")) return;
                    event.setCancelled(true);
                    return;
                }

                if (user.isRank(UserRank.HELPOP)) {
                    Utils.redisChatLog(player.getName(), event.getMessage());
                }

                if (event.isCommand()) return;

                if (player.hasPermission("staffchat.use") && (user.getStaffChat() || event.getMessage().startsWith("#"))) {
                    event.setCancelled(true);
                    String msg = event.getMessage().startsWith("#") ? event.getMessage().substring(1) : event.getMessage();
                    Utils.redisStaffChat(user.getColoredName(player), msg);
                }
            });
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

}
