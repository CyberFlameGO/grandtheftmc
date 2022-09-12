package net.grandtheftmc.gtm.commands;

import com.google.common.collect.Maps;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class TopKillersCommand implements CommandExecutor {

    private Map<String, Integer> topKillers = Maps.newHashMap();

    public TopKillersCommand() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(GTM.getInstance(), () -> this.topKillers = GTMUtils.sortByValue(GTMUtils.getTopKillers(5)), 0, 20*300);
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.GTM.f("&cYou are not a player!"));
            return true;
        }

        UUID uuid = ((Player) s).getUniqueId();
        s.sendMessage(Lang.GTM.f("&7Compiling top killers list..."));
        ServerUtil.runTaskAsync(() -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            int i = 1;
            for(Map.Entry<String,Integer> entry : this.topKillers.entrySet()){
                player.sendMessage(Utils.f("&7#" + i++ + " &a" + entry.getKey() + " &7- " + entry.getValue() + " &7kills"));
            }
        });

        return true;
    }
}