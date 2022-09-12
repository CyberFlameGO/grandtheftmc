package net.grandtheftmc.Bungee.listeners;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.redisbungee.data.DataType;
import net.grandtheftmc.Bungee.users.User;
import net.grandtheftmc.Bungee.utils.HelpLog;
import net.grandtheftmc.Bungee.utils.PlaytimeManager;
import net.grandtheftmc.Bungee.utils.TimeFormatter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Disconnect implements Listener {

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        //Stop tracking playtime
        PlaytimeManager.endSession(player);

        if (HelpLog.helpTicketExists(player.getName())) {
            //send a close
            Map<String, Object> map = new HashMap<>();
            map.put("helper", "null");
            map.put("sender", player.getName());
            String ser = Bungee.getRedisManager().serialize(DataType.HELP_CLOSE, map);
            //Send this serialised object to other redis servers for handling...
            Bungee.getRedisManager().sendMessage(ser);
        }

        Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(player.getUniqueId());
        userOptional.ifPresent(user -> {
            user.setLastQuit(System.currentTimeMillis());
            Long session = user.getLastQuit() - user.getLastJoin();
            user.setPlaytime(user.getPlaytime() + session);
            TimeFormatter timeFormatter = new TimeFormatter(TimeUnit.MILLISECONDS, session);
            String msg = "[QUIT] played for " + timeFormatter.getHours() +
                    "h " + timeFormatter.getMinutes() + "m " + timeFormatter.getSeconds() + "s";
            Utils.chatLog(player.getName(), msg);
        });

//        Bungee.getInstance().getProxy().getScheduler().runAsync(Bungee.getInstance(), () -> {
//            try (PreparedStatement statement = Bungee.getSQL().prepareStatement("DELETE FROM user_respack WHERE user=UNHEX(?);")) {
//                statement.setString(1, player.getUniqueId().toString().replaceAll("-", ""));
//                statement.execute();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
    }
}
