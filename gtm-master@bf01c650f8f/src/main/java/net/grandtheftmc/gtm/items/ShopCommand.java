package net.grandtheftmc.gtm.items;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!s.hasPermission("command.shop")) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/shop armorupgrade <armorupgrade>"));
            s.sendMessage(Utils.f("&c/shop <item> <amount> <price>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "armorupgrade":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/shop armorupgrade <armorupgrade>"));
                    return true;
                }
                GTM.getShopManager().addArmorUpgradeShop((Player) s, args[1]);
                return true;
            default:
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/shop <item> <amount> <price>"));
                }
                GTM.getShopManager().addShop((Player) s, args[0], Integer.parseInt(args[1]), Double.parseDouble(args[2]));
                return true;
        }

    }

}