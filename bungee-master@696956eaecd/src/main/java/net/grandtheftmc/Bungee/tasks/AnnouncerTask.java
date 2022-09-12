package net.grandtheftmc.Bungee.tasks;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Lang;
import net.grandtheftmc.Bungee.users.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AnnouncerTask {

    public AnnouncerTask() {
        Bungee.getInstance().getProxy().getScheduler().schedule(Bungee.getInstance(), () -> {
            int count = 0;

            for (UUID uuid : Bungee.getRedisManager().getRedisAPI().getPlayersOnline()) {
                Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(uuid);
                if (userOptional.isPresent()) {
                    count++;
                }
            }

            if (count <= 1) return;
            Bungee.getInstance().getProxy().broadcast(Lang.GTM.f("&e&lThere are currently " + count + " staff members online! Need help? Use &a&l/help <your message>"));
        }, 300, 700, TimeUnit.SECONDS);
    }
}
