package net.grandtheftmc.Bungee.listeners;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.users.User;
import net.grandtheftmc.Bungee.users.UserRank;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Login implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        UUID uuid = event.getConnection().getUniqueId();
        Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(uuid);
        userOptional.ifPresent(user -> {
            if (!Objects.equals(user.getUsername(), event.getConnection().getName()))
                user.setUsername(event.getConnection().getName());

            user.setLastJoin(System.currentTimeMillis());

            if (user.isRank(UserRank.BUILDER)) {
                String address = event.getConnection().getAddress().getAddress().getHostAddress();
                if (user.getLastIPAddress().equals(address))
                    user.setAuthyVerified(true);

                else {
                    Utils.redisChatLog(user.getUsername(), "logged in with unknown IP address " + address);
                    if (user.getAuthyId() != 0) Bungee.getAuthyManager().sendSMSToken(user.getAuthyId());
                    user.setAuthyVerified(false);
                }
            }
        });

        event.registerIntent(Bungee.getInstance());
        ProxyServer.getInstance().getScheduler().runAsync(Bungee.getInstance(), () -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
            if (player != null)
                Bungee.getUserManager().setPerms(player, userOptional.isPresent() ? userOptional.get().getUserRank() : UserRank.DEFAULT);

            event.completeIntent(Bungee.getInstance());
        });
    }
}

