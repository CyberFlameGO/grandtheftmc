package com.j0ach1mmall3.wastedguns.commands;

import com.j0ach1mmall3.jlib.methods.General;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.commands.RankedCommand;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.WeaponManager;
import net.grandtheftmc.guns.weapon.Weapon;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 7/05/2016
 */
public final class GiveWeaponCommandHandler extends CoreCommand<CommandSender> implements RankedCommand {
    private final WeaponManager weaponManager;

    public GiveWeaponCommandHandler(WeaponManager weaponManager) {
        super("giveweapon", "Gives a weapon to a player");
        this.weaponManager = weaponManager;
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if(strings.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /giveweapon <identifier> <player>");
            return;
        }

        short id = -1;
        try {
            id = Short.parseShort(strings[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Cannot convert '" + strings[0] + "' to a numeral.");
            return;
        }

        if(id == -1) {
            sender.sendMessage("Cannot convert '" + strings[0] + "' to a numeral.");
            return;
        }

//        Optional<Weapon> weapon = this.plugin.getWeapon(strings[0]);
        Optional<Weapon<?>> optional = weaponManager.getWeapon(id);
        if(!optional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Unknown weapon " + strings[0]);
            return;
        }

        Player p = General.getPlayerByName(strings[1], false);
        if(p == null) {
            sender.sendMessage(ChatColor.RED + "Unknown player " + strings[1]);
            return;
        }

        weaponManager.giveWeapon(p, optional.get().clone(), true);
        sender.sendMessage(ChatColor.GREEN + "Successfully gave " + optional.get().getName() + " to " + p.getName());
        return;
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.ADMIN;
    }
}
