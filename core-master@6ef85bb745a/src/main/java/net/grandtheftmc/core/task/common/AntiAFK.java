package net.grandtheftmc.core.task.common;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class AntiAFK implements Component<AntiAFK, Core> {
    private final ConcurrentMap<UUID, Long> afk;
    private static final int AFK_MINUTES = 5;

    public AntiAFK() {
        this.afk = new ConcurrentHashMap<>();
        Core.getInstance().getServer().getPluginManager().registerEvents(this, Core.getInstance());
    }

    @Override
    public AntiAFK onDisable(Core plugin) {
        this.afk.clear();
        return this;
    }

    public void refreshAfk(Player player) {
        afk.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void on(PlayerLoginEvent event) {


        User joining = Core.getUserManager().getLoadedUser(event.getPlayer().getUniqueId());
        if (Core.getInstance().getServer().getOnlinePlayers().size() >= Core.getInstance().getServer().getMaxPlayers()) {
            Set<UUID> toBeRemoved = new HashSet<>();
            boolean kick = false;
            for (Map.Entry<UUID, Long> afkPlayers : afk.entrySet()) {
                if (System.currentTimeMillis() > (afkPlayers.getValue() + TimeUnit.MINUTES.toMillis(AFK_MINUTES))) {
                    toBeRemoved.add(afkPlayers.getKey());
                }
            }
            for (UUID uuid : toBeRemoved) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    User user = Core.getUserManager().getLoadedUser(uuid);
                    if (!user.isRank(UserRank.HELPOP) && user.isRank(UserRank.VIP) && !user.hasTrialRank()) continue;
                    kick = true;
                    afk.remove(uuid);
                    player.kickPlayer(Lang.GTM.f("&cYou were kicked for being afk!"));
                } else {
                    if (afk.containsKey(uuid)) afk.remove(uuid);
                }
            }
            toBeRemoved.clear();
            if (kick || joining.isRank(UserRank.PREMIUM)) {
                event.allow();
            } else {
                event.setKickMessage(Utils.f("&cSorry, this server is full! You must be &a&lPREMIUM+ &cto join full games!"));
            }
        }
    }
}