package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.CooldownPayload;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 2017-08-26.
 */
public class CooldownCommand extends CoreCommand<CommandSender> {


    public CooldownCommand() {
        super("cooldown", "changes cooldowns for a player");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length==0) {
            sender.sendMessage(Utils.f("&e/cooldown remove <id> <player>"));
            sender.sendMessage(Utils.f("&e/cooldown list <player>"));
            return;
        }
        if(sender instanceof Player && !Core.getUserManager().getLoadedUser(((Player)sender).getUniqueId()).getUserRank().isHigherThan(UserRank.ADMIN)) {
            sender.sendMessage(Lang.NOPERM.f(""));
            return;
        }
        switch (args[0].toLowerCase()) {
            case "list": {
                Player target = Bukkit.getPlayer(args[1]);
                if(target==null){
                    sender.sendMessage(Lang.HEY.f("&7That player is currently not online!"));
                    return;
                }
                User user = Core.getUserManager().getLoadedUser(target.getUniqueId());
                for(CooldownPayload cd : user.getCooldowns()) {
                    sender.sendMessage(Utils.f("&a" + cd.getId() + " &cExpire: " + cd.getExpireTime()));
                }
                break;
            }
            case "remove" : {
                Player target = Bukkit.getPlayer(args[2]);
                if(target==null){
                    sender.sendMessage(Lang.HEY.f("&7That player is currently not online!"));
                    return;
                }
                String id = args[1].toLowerCase();
                User user = Core.getUserManager().getLoadedUser(target.getUniqueId());
                if(!user.isOnCooldown(id)){
                    sender.sendMessage(Lang.HEY.f("&7That player isn't on a cooldown for that ID"));
                    return;
                }
                user.removeCooldown(id);
                sender.sendMessage(Lang.HEY.f("&7You have removed the cooldown for &b" + target.getName() + " &7with id &e" + id));
                target.sendMessage(Lang.HEY.f("&7The cooldown &e" + id + " &7has been removed from your player."));
                break;
            }
        }
    }
}
