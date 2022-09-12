package net.grandtheftmc.core.tutorials;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NextCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String label, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Utils.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player) s;
        UUID uuid = player.getUniqueId();
        User user = Core.getUserManager().getLoadedUser(uuid);
        Tutorial tut = Core.getTutorialManager().getTutorial(user.getTutorial());
        if (tut == null) {
            s.sendMessage(Lang.TUTORIALS.f("&7You are not in a tutorial!"));
            return true;
        }
        if (tut.getSlide(user.getTutorialSlide()) != null && !tut.getSlide(user.getTutorialSlide()).isCanConfirm()) {
            s.sendMessage(Lang.TUTORIALS.f("&7You can not skip this slide!"));
            return true;
        }
        tut.playNextSlide(player, user);
        return true;
    }
}
