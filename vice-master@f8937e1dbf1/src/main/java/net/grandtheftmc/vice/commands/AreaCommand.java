package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.areas.dao.AreaDAO;
import net.grandtheftmc.vice.areas.dao.DiscoveryDAO;
import net.grandtheftmc.vice.areas.obj.Area;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AreaCommand implements CommandExecutor {

    private final Vice plugin;

    public AreaCommand(Vice plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) return true;

        Player player = (Player) s;

        if (!player.hasPermission("command.area")) {
            s.sendMessage(Lang.NOPERM.toString());

            return true;
        }

        if (args.length < 2) {
            s.sendMessage(Utils.f("&c/area create <name>"));
            s.sendMessage(Utils.f("&c/area delete <name>"));

            return true;
        }

        /*
            Create
         */
        if (args[0].equalsIgnoreCase("create")) {
            if (plugin.getAreaManager().isBuilding(player.getUniqueId())) {
                s.sendMessage(Lang.VICE.f("&cYou are already creating an Area"));
                return true;
            }

            StringBuilder areaName = new StringBuilder();

            for (int i = 1; i < args.length; i++)
                areaName.append(args[i]).append(" ");

            plugin.getAreaManager().getBuilders().add(plugin.getAreaManager().createBuilder(areaName.toString().trim(), player.getUniqueId()));
            player.getInventory().setItemInMainHand(plugin.getAreaManager().getAreaClaimStick());

            s.sendMessage(Lang.VICE.f("&aYou are now building '" + areaName.toString().trim() + "'"));

            return true;
        }

        /*
            Delete
         */
        if (args[0].equalsIgnoreCase("delete")) {
            StringBuilder areaName = new StringBuilder();

            for (int i = 1; i < args.length; i++)
                areaName.append(args[i]).append(" ");

            Area area = plugin.getAreaManager().getAreaByName(areaName.toString().trim());

            if (area == null) {
                s.sendMessage(Lang.VICE.f("&cArea not found"));
                return true;
            }

            plugin.getAreaManager().getAreas().remove(area);

            ServerUtil.runTaskAsync(() -> {
                AreaDAO.delete(area.getID());
                DiscoveryDAO.deleteByArea(area.getID());
            });

            plugin.getAreaManager().getAreaUsers().forEach(user -> {
                if (user.getCurrent() == area.getID()) user.setCurrent(-1);
                if (user.hasVisited(area.getID())) user.getVisited().remove(area.getID());
            });

            s.sendMessage(Lang.VICE.f("&eYou have deleted '" + area.getName() + "'"));

            return true;
        }

        /*
            Cancel
         */
        if (args[0].equalsIgnoreCase("cancel")) {
            if (!plugin.getAreaManager().isBuilding(player.getUniqueId())) {
                s.sendMessage(Lang.VICE.f("&cYou are not building an Area"));
                return true;
            }

            StringBuilder areaName = new StringBuilder();

            for (int i = 1; i < args.length; i++)
                areaName.append(args[i]).append(" ");

            plugin.getAreaManager().getBuilders().remove(plugin.getAreaManager().getBuilderByUserUUID(player.getUniqueId()));

            s.sendMessage(Lang.VICE.f("&eYou are no longer building '" + areaName.toString().trim() + "'"));

            return true;
        }

        s.sendMessage(Utils.f("&c/area create <name>"));
        s.sendMessage(Utils.f("&c/area delete <name>"));

        return false;
    }
}
