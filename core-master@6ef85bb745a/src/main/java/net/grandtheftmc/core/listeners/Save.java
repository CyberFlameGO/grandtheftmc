package net.grandtheftmc.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.events.ServerSaveEvent;
import net.grandtheftmc.core.users.UserManager;

public class Save implements Listener {

    @EventHandler
    public void serverSaveEvent(ServerSaveEvent event) {
        Core.getInstance().save(false);
        UserManager.getInstance().getUsers().forEach(user -> user.updateAchievements());
    }
}
