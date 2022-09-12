package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.gtm.GTMUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NearCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.GTM.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player)s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (!user.isRank(UserRank.VIP)) {
            player.sendMessage(Lang.GTM.f("&7You must be &6&lVIP+ &7to use this command!"));
            return true;
        }
        Map<String, Integer> nearbySet = new HashMap<>();
        int range = GTMUtils.getNearRange(user.getUserRank());
        for(Entity entity : player.getNearbyEntities(range, range, range)) {
            if(entity.getType() != EntityType.PLAYER) continue;
            Player target = (Player)entity;
            if(target.getGameMode() != GameMode.ADVENTURE) continue;
            int distance = (int)player.getLocation().distance(target.getLocation());
            nearbySet.put(target.getDisplayName(), distance);
        }
        List<String> nearbyFormatted = nearbySet.keySet().stream().map(set -> set + " &f(&c" + nearbySet.get(set) + "b&f)").collect(Collectors.toList());
        if(nearbyFormatted.isEmpty()) {
            player.sendMessage(Lang.GTM.f("&7No nearby players found."));
        } else {
            String message = StringUtils.join(nearbyFormatted, ", ");
            player.sendMessage(Lang.GTM.f("&7Players nearby:" + message));
        }
        return true;
    }
}
