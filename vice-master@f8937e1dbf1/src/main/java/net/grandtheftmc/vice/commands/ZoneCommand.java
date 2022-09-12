package net.grandtheftmc.vice.commands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.world.ViceSelection;
import net.grandtheftmc.vice.world.ZoneFlag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Timothy Lampen on 8/25/2017.
 */
public class ZoneCommand extends CoreCommand<Player>{
    public ZoneCommand() {
        super(" zone", "selects cubiod regions for various purposes");
    }

    @Override
    public void execute(Player player, String[] args) {
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if(!user.getUserRank().isHigherThan(UserRank.ADMIN) && !player.isOp()) {
            player.sendMessage(Lang.NOPERM.f(""));
            return;
        }
        if(args.length==0) {

            player.sendMessage(Utils.f("&e/zone add <name> <flag,flag,...> [ignoreheight] &7- makes your current selection into a police-free zone. [ignore height refers to if the zone should take into account the y coord.]"));
            player.sendMessage(Utils.f("&e/zone remove <name> &7- removes the zone that you are currently in."));
            player.sendMessage(Utils.f("&e/zone addflag <name> <flag> &7- Adds a certain flag to the zone with the selected name."));
            player.sendMessage(Utils.f("&e/zone removeflag <name> <flag> &7- Removes a certain flag from the zone with the selected name."));
            player.sendMessage(Utils.f("&e/zone list [all]&7- Lists all the zones that you are currently standing in [or on the server]."));
            player.sendMessage(Utils.f("&7Possible flags: &eCOP_TELEPORT_STATION&7, &eCOP_CANT_ARREST"));
            return;
        }
        switch (args[0].toLowerCase()) {
            case "addflag": {
                if(args.length!=3) {
                    player.sendMessage(Lang.VICE.f("&7/zone addflag <name> <flag>"));
                    return;
                }
                Optional<ViceSelection> zone = Vice.getWorldManager().getZone(args[1]);
                if(!zone.isPresent()) {
                    player.sendMessage(Lang.VICE.f("&7Unable to find zone with the name: &e" + args[1]));
                    return;
                }
                ZoneFlag flag;
                try {
                    flag = ZoneFlag.valueOf(args[2].toUpperCase());
                }catch (Exception e){
                    player.sendMessage(Lang.VICE.f("&7Unable to find flag with name: &e" + args[2].toUpperCase()));
                    return;
                }

                if(zone.get().getFlags().contains(flag)) {
                    player.sendMessage(Lang.VICE.f("&7This zone already has this flag!"));
                    return;
                }

                zone.get().addFlag(flag);
                player.sendMessage(Lang.VICE.f("&7You have added the flag: &e" + flag.toString() + " &7to the zone with name: &e" + args[1] + "&7."));
                return;
            }
            case "removeflag": {
                if(args.length!=3) {
                    player.sendMessage(Lang.VICE.f("&7/zone removeflag <name> <flag>"));
                    return;
                }
                Optional<ViceSelection> zone = Vice.getWorldManager().getZone(args[1]);
                if(!zone.isPresent()) {
                    player.sendMessage(Lang.VICE.f("&7Unable to find zone with the name: &e" + args[1]));
                    return;
                }
                ZoneFlag flag;
                try {
                    flag = ZoneFlag.valueOf(args[2].toUpperCase());
                }catch (Exception e){
                    player.sendMessage(Lang.VICE.f("&7Unable to find flag with name: " + args[2].toUpperCase()));
                    return;
                }

                if(!zone.get().removeFlag(flag)) {
                    player.sendMessage(Lang.VICE.f("&7This zone doesnt have this flag!"));

                }

                player.sendMessage(Lang.VICE.f("&7You have removed the flag: &e" + flag.toString() + " &7from the zone with name: &e" + args[1] + "&7."));
                return;
            }
            case "list": {
                boolean all = args.length==2;
                List<ViceSelection> zones;
                if(all)
                    zones = Vice.getWorldManager().getZones();
                else
                    zones = Vice.getWorldManager().getZones(player.getLocation());
                if(zones.size()==0) {
                    player.sendMessage(Lang.VICE.f("&7Could not find any zones!"));
                    return;
                }
                player.sendMessage(Utils.f("&7Zones:"));
                for(ViceSelection zone : zones) {
                    StringBuilder sb = new StringBuilder();
                    zone.getFlags().forEach(flag -> sb.append("&e " + flag.toString() + "&e,"));
                    sb.deleteCharAt(sb.length()-1);
                    player.sendMessage(Utils.f("&7Name: &e" + zone.getName() + " &7| Flags: &e" + sb.toString()));
                }
                return;
            }
            case "remove": {
                if(args.length < 2) {
                    player.sendMessage(Lang.VICE.f("&7/zone remove <name>"));
                    return;
                }
                String name = args[1];
                if(!Vice.getWorldManager().removeZone(name)) {
                    player.sendMessage(Lang.VICE.f("&7There is no existing zone with the name &e" + name + "&7!"));
                    return;
                }
                player.sendMessage(Lang.VICE.f("&7You have removed the zone with name: &e" + name + "&7."));
                return;
            }
            case "add": {
                if(args.length < 2) {
                    player.sendMessage(Lang.VICE.f("&7/zone add <name> <flag,flag,...> [ignoreheight]"));
                    return;
                }
                WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
                Selection selction = worldEdit.getSelection(player);
                if(selction==null) {
                    player.sendMessage(Lang.VICE.f("&7Please select a region using the world edit tool (found using //wand)"));
                    return;
                }

                List<ZoneFlag> flags = new ArrayList<>();
                if(!args[2].contains(",")) {
                    ZoneFlag flag;
                    try {
                        flag = ZoneFlag.valueOf(args[2].toUpperCase());
                    }catch (Exception e){
                        player.sendMessage(Lang.VICE.f("&7Unable to find flag with name: " + args[2].toUpperCase()));
                        return;
                    }
                    flags.add(flag);
                }
                else{
                    for(String s : args[2].toUpperCase().split(",")) {
                        ZoneFlag flag;
                        try {
                            flag = ZoneFlag.valueOf(s);
                        }catch (Exception e){
                            player.sendMessage(Lang.VICE.f("&7Unable to find flag with name: " + s.toUpperCase()));
                            return;
                        }
                        flags.add(flag);
                    }
                }

                boolean ignoreheight = args.length != 4 || !args[3].equalsIgnoreCase("false");

                ViceSelection vSelection = new ViceSelection(args[1], flags, selction.getMaximumPoint(), selction.getMinimumPoint(), ignoreheight);
                Vice.getWorldManager().addZone(vSelection);
                player.sendMessage(Lang.VICE.f("&7You have created a zone with your current selection with flags: &e" + args[2].toUpperCase()) + "&7 and ignoreheight: &e" + ignoreheight);
                return;
            }
        }

    }
}
