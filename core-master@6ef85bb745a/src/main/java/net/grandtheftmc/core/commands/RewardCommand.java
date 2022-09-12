package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.redis.RedisManager;
import net.grandtheftmc.core.redis.data.DataType;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (s instanceof Player) {
            User u = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
            if (!u.isRank(UserRank.MANAGER)) {
                s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                return true;
            }
        }

        if (args.length != 2) {
            s.sendMessage(Utils.f("&c/reward <username/uuid> <type>"));
            return true;
        }

        //Continute to execute
        String target = args[1];

        UUID targetUUID = null;

        try {
            //try parse from string
            targetUUID = UUID.fromString(target);
        } catch (Exception e){
            //do nothing, if UUID is null we send the string.
        }

        if (targetUUID == null) {
            //Try obtain uuid externally
            OfflinePlayer op = Bukkit.getOfflinePlayer(target);
            if (op != null) {
                targetUUID = op.getUniqueId();
            }
        }

        if (targetUUID == null) {
            //Still failed to find a uuid, so stop
            s.sendMessage(Utils.f("&cFailed to find a UUID for '" + target + "', rewards cannot be given."));
            return true;
        }


        //Publish reward notification on redis channel.
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("target", targetUUID.toString());
        RedisManager.publishMessage(DataType.REWARD_NOTIFY, data);

        return true;
    }
}