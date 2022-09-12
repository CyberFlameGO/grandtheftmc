package net.grandtheftmc.gtm.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.AmmoType;
import net.grandtheftmc.gtm.users.GTMUser;

public class AmmoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!s.hasPermission("command.ammo")) {
            s.sendMessage(Lang.NOPERM.toString());
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/ammo types"));
            s.sendMessage(Utils.f("&c/ammo balance <player> <type>"));
            s.sendMessage(Utils.f("&c/ammo give <player> <type> <amount>"));
            s.sendMessage(Utils.f("&c/ammo take <player> <type> <amount>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "types": {
                sendAmmoTypes(s);
                return true;
            }
            case "balance": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/ammo balance <player> <type>"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Lang.AMMO.f("&7That player is not online!"));
                    return true;
                }
                AmmoType type = AmmoType.getAmmoType(args[2]);
                if (type == null) {
                    s.sendMessage(Lang.AMMO.f("&7That AmmoType does not exist!"));
                    sendAmmoTypes(s);
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                s.sendMessage(Lang.AMMO.f("&a" + player.getName() + "&7 has &a&l" + user.getAmmo(type) + ' '
                        + type.getGameItem().getDisplayName() + "&7!"));
                return true;
            }
            case "give": {
                if (args.length != 4) {
                    s.sendMessage(Utils.f("&c/ammo give <player> <type> <amount>"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Lang.AMMO.f("&7That player is not online!"));
                    return true;
                }
                AmmoType type = AmmoType.getAmmoType(args[2]);
                if (type == null) {
                    s.sendMessage(Lang.AMMO.f("&7That AmmoType does not exist!"));
                    sendAmmoTypes(s);
                    return true;
                }
                int amnt;
                try {
                    amnt = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Lang.AMMO.f("&7The amount must be a number!"));
                    return true;
                }
                if (amnt <= 0) {
                    s.sendMessage(Lang.AMMO.f("&7The amount must be positive!"));
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                user.addAmmo(type, amnt);
                s.sendMessage(Lang.AMMO.f("&7You gave &a&l" + amnt + ' ' + type.getGameItem().getDisplayName() + "&7 to &a"
                        + player.getName() + "&7!"));
                player.sendMessage(Lang.AMMO.f("&7You were given &a&l" + amnt + ' ' + type.getGameItem().getDisplayName()
                        + "&7 by &a" + s.getName() + "&7!"));
                return true;
            }
            case "take":
                if (args.length != 4) {
                    s.sendMessage(Utils.f("&c/ammo take <player> <type> <amount>"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Lang.AMMO.f("&7That player is not online!"));
                    return true;
                }
                AmmoType type = AmmoType.getAmmoType(args[2]);
                if (type == null) {
                    s.sendMessage(Lang.AMMO.f("&7That AmmoType does not exist!"));
                    sendAmmoTypes(s);
                    return true;
                }
                int amnt;
                try {
                    amnt = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Lang.AMMO.f("&7The amount must be a number!"));
                    return true;
                }
                if (amnt <= 0) {
                    s.sendMessage(Lang.AMMO.f("&7The amount must be positive!"));
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                if (!user.hasAmmo(type, amnt))
                    amnt = user.getAmmo(type);
                user.removeAmmo(type, amnt);
                s.sendMessage(Lang.AMMO.f("&7You took &c&l" + amnt + ' ' + type.getGameItem().getDisplayName()
                        + "&7 from &a" + player.getName() + "&7!"));
                player.sendMessage(Lang.AMMO.f("&c&l" + amnt + ' ' + type.getGameItem().getDisplayName()
                        + "&7 was taken from you by &a" + s.getName() + "&7!"));
                return true;
            default:
                s.sendMessage(Utils.f("&c/ammo types"));
                s.sendMessage(Utils.f("&c/ammo balance <player> <type>"));
                s.sendMessage(Utils.f("&c/ammo give <player> <type> <amount>"));
                s.sendMessage(Utils.f("&c/ammo take <player> <type> <amount>"));
                return true;
        }
    }

    private String ammoTypes = null;

    private void sendAmmoTypes(CommandSender sender) {
        if (ammoTypes == null) {
            StringBuilder b = new StringBuilder("&7");
            for (AmmoType type : AmmoType.getTypes()) {
                b.append(type).append("(").append(type.getGameItemName()).append("),");
            }
            if (b.toString().endsWith(",")) {
                b.setLength(b.length() - 1);
            }
            ammoTypes = b.toString();
        }

        sender.sendMessage(ammoTypes);
    }

}
