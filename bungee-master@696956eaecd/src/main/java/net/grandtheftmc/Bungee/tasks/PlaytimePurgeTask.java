package net.grandtheftmc.Bungee.tasks;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Lang;
import net.grandtheftmc.Bungee.users.User;
import net.grandtheftmc.Bungee.utils.PlaytimeManager;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlaytimePurgeTask {

    public PlaytimePurgeTask() {
        Bungee.getInstance().getProxy().getScheduler().schedule(Bungee.getInstance(), PlaytimeManager::purgeOldSessions, 0, 1, TimeUnit.DAYS);
    }
}
