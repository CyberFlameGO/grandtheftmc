package net.grandtheftmc.Bungee.listeners;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.redisbungee.data.DataType;
import net.grandtheftmc.Bungee.users.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Connect implements Listener {

    @EventHandler
    public void onConnect(ServerConnectEvent event) {

        // grab event variables
        ProxiedPlayer player = event.getPlayer();

        // TODO test messages remove
        System.out.println("target: " + event.getTarget());
        System.out.println("player server: " + player.getServer());

        Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(player.getUniqueId());
        userOptional.ifPresent(user -> {
            user.update();
            Bungee.getUserManager().setPerms(player, user.getUserRank());

            if (!user.isAuthyVerified() && player.getServer() != null) {
                event.setCancelled(true);
                return;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("uuid", player.getUniqueId().toString());

            String ser = Bungee.getRedisManager().serialize(DataType.STAFF_JOIN, map);
            //Send this serialised object to other redis servers for handling...
            Bungee.getRedisManager().sendMessage(ser);
        });

        // if the player is currently not on a server
        if (player.getServer() == null) {

            // NOTE: The below attempted to allow force hosts to connect directly through
            // This failed because force_default_server in bungee MUST be false to allow
            // players to use force hosts. However, force_default_server being false means
            // players will joining without a force hosts will ALWAYS go back to the
            // server they last connected to

            // if no target
            if (event.getTarget() == null) {
                event.setTarget(Utils.getRandomHub());
            }

            else {
                // if they are attempting to join ANY hub, pick a random one
                if (event.getTarget().getName().contains("hub")) {
                    event.setTarget(Utils.getRandomHub());
                } else {
                    // do nothing, as this should let
                    // the player force host through

                    // if case force default fails, set their "rejoin server"
                    // as a random hub
                    player.setReconnectServer(Utils.getRandomHub());
                }
            }
        }

        // TODO remove test messages
        System.out.println("target server after: " + event.getTarget());
    }
}
