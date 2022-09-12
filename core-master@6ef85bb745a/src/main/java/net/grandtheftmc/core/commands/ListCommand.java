package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;

public class ListCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        Collection<String> defaultPlayers = new ArrayList<>();
        Collection<String> donators = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(online -> {
            User targetUser = Core.getUserManager().getLoadedUser(online.getUniqueId());
            if (online.getGameMode() == GameMode.CREATIVE || online.getGameMode() == GameMode.SPECTATOR || online.isOp())
                return;
            if (targetUser.isRank(UserRank.VIP)) {
                donators.add(online.getDisplayName());
            } else {
                defaultPlayers.add(online.getDisplayName());
            }
        });
        String list = StringUtils.join(donators, "&7, ") + "&7," + StringUtils.join(defaultPlayers, "&7, ");
        if (donators.isEmpty() && defaultPlayers.isEmpty()) {
            s.sendMessage(Lang.GTM.f("&7There are no players online!"));
        } else {
            s.sendMessage(Lang.GTM.f("&7There are &a" + Bukkit.getOnlinePlayers().size() + " &7players online" +
                    " out of a maximum of &a" + Bukkit.getMaxPlayers() + "&7!"));
            s.sendMessage(Utils.f("&7" + list));
        }
        return true;
    }
}