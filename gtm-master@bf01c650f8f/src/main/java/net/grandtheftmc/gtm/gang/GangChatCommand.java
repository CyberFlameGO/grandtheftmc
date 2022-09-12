package net.grandtheftmc.gtm.gang;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMUser;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public class GangChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());

        Optional<Gang> gang = GangManager.getInstance().getGangByMember(player.getUniqueId());
        if (!gang.isPresent()) {
            s.sendMessage(Lang.GANGCHAT.f("&7You are not in any gang!"));
            return true;
        }

        if (args.length == 0) {
            gang.get().toggleChat(player.getUniqueId());
            s.sendMessage(Lang.GANGCHAT.f("&7You turned " + (gang.get().isGangChat(player.getUniqueId()) ? "&a&lon" : "&c&loff") + "&7 gang chat!"));
            return true;
        }

        if ("on".equalsIgnoreCase(args[0])) {
//            gtmUser.setGangChat(true);
            gang.get().setGangChat(player.getUniqueId(), true);
            s.sendMessage(Lang.GANGCHAT.f("&7You turned &a&lon&7 gang chat!"));
            return true;
        }

        if ("off".equalsIgnoreCase(args[0])) {
//            gtmUser.setGangChat(false);
            gang.get().setGangChat(player.getUniqueId(), false);
            s.sendMessage(Lang.GANGCHAT.f("&7You turned &c&loff&7 gang chat!"));
            return true;
        }

        String msg = StringUtils.join(Arrays.asList(args), " ");
        gang.get().chat(player, user, gtmUser, msg);
        return true;
    }
}
