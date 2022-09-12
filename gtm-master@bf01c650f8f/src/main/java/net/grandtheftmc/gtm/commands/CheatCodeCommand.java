package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.CheatCode;
import net.grandtheftmc.gtm.users.CheatCodeState;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Timothy Lampen on 8/24/2017.
 */
public class CheatCodeCommand extends CoreCommand<CommandSender> {

    public CheatCodeCommand() {
        super("cheatcode", "edit a player's unlocked cheat codes", "cc", "cheatcodes");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length==0) {
            if(!(sender instanceof Player) || Core.getUserManager().getLoadedUser(((Player)sender).getUniqueId()).getUserRank().isHigherThan(UserRank.ADMIN)) {
                sender.sendMessage(Utils.f("&e/cheatcode give <player> <cheatcode> &7- gives a certain player a PERM new cheatcode"));
                sender.sendMessage(Utils.f("&e/cheatcode remove <player> <cheatcode> &7- removes a cheatcode from a player"));
                sender.sendMessage(Utils.f("&e/cheatcode view <player> &7- view a player's cheatcodes"));
                sender.sendMessage(Utils.f("&e/cheatcode list &7- lists all avaliable cheat codes."));
                sender.sendMessage(Utils.f("&e/cheatcode <cheatcode> &7- toggle / activate the specific cheatcode."));
            }
            else {
                sender.sendMessage(Utils.f("&e/cheatcode <cheatcode> &7- toggle / activate the specific cheatcode."));
                sender.sendMessage(Utils.f("&e/cheatcode list &7- lists all avaliable cheat codes."));
            }
            return;
        }
        if(args.length==1) {
            for (CheatCode code : CheatCode.getCodes()) {
                if (args[0].equalsIgnoreCase(code.toString())) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Lang.NOTPLAYER.f(""));
                        return;
                    }
                    Player player = (Player) sender;
                    GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                    if (user.getCheatCodeState(code).getState() == State.LOCKED) {
                        player.sendMessage(Lang.CHEAT_CODES.f("&7You haven't unlocked this cheat code yet!"));
                        return;
                    }
                    code.activate(Core.getUserManager().getLoadedUser(player.getUniqueId()), user, player, user.getCheatCodeState(code));
                    return;
                }
            }
        }
        switch (args[0]) {
            case "remove": {
                if(sender instanceof Player && !Core.getUserManager().getLoadedUser(((Player)sender).getUniqueId()).getUserRank().isHigherThan(UserRank.ADMIN)) {
                    sender.sendMessage(Lang.NOPERM.f(""));
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if(target==null) {
                    sender.sendMessage(Lang.CHEAT_CODES.f("&7Cannot find the player &a" + args[1]));
                    return;
                }
                if (args.length != 3) {
                    sender.sendMessage(Lang.CHEAT_CODES.f("&c/cheatcode remove <player> <cheatcode>"));
                    return;
                }
                Optional<CheatCode> optCode = Arrays.stream(CheatCode.getCodes()).filter(c -> c.toString().equalsIgnoreCase(args[2])).findFirst();
                if (!optCode.isPresent()) {
                    sender.sendMessage(Lang.CHEAT_CODES.f("&cUnable to find cheatcode with name &e" + args[2]));
                    return;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(target.getUniqueId());
                user.setCheatCodeState(optCode.get(), new CheatCodeState(State.LOCKED, false));
                Core.getUserManager().getLoadedUser(target.getUniqueId()).insertLog(target,"removeCheatCodeCommand","CHEATCODE", optCode.get().toString(),1,0);
                target.sendMessage(Lang.CHEAT_CODES.f("&7The cheatcode &e" + optCode.get() + " &7has been removed from your account."));
                sender.sendMessage(Lang.CHEAT_CODES.f("&7You have removed the cheatcode &e" + optCode.get() + " &7from &b" + target.getName() + "&7's account."));
                break;
            }
            case "give": {
                if(sender instanceof Player && !Core.getUserManager().getLoadedUser(((Player)sender).getUniqueId()).getUserRank().isHigherThan(UserRank.ADMIN)) {
                    sender.sendMessage(Lang.NOPERM.f(""));
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if(target==null) {
                    sender.sendMessage(Lang.CHEAT_CODES.f("&7Cannot find the player &a" + args[1]));
                    return;
                }
                if (args.length != 3) {
                    sender.sendMessage(Lang.CHEAT_CODES.f("&c/cheatcode give <player> <cheatcode>"));
                    return;
                }
                Optional<CheatCode> optCode = Arrays.stream(CheatCode.getCodes()).filter(c -> c.toString().equalsIgnoreCase(args[2])).findFirst();
                if (!optCode.isPresent()) {
                    sender.sendMessage(Lang.CHEAT_CODES.f("&cUnable to find cheatcode with name &e" + args[2]));
                    return;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(target.getUniqueId());
                switch (optCode.get()) {
                    case STACK:
                        Core.getPermsManager().addPerm(target.getUniqueId(), "command.stack");
                        break;
                    case FIXALL:
                        Core.getPermsManager().addPerm(target.getUniqueId(), "command.fix.all");
                        break;
                    case FIXHAND:
                        Core.getPermsManager().addPerm(target.getUniqueId(), "command.fix.hand");
                        break;

                }

                Core.getUserManager().getLoadedUser(target.getUniqueId()).insertLog(target,"giveCheatCodeCommand","CHEATCODE", optCode.get().toString(),1,0);
                user.setCheatCodeState(optCode.get(), new CheatCodeState(optCode.get().getDefaultState(), true));
                sender.sendMessage(Lang.CHEAT_CODES.f("&7You have given &b" + target.getName() + " &7the cheatcode &e" + optCode.get().toString() + "&7!"));
                target.sendMessage(Lang.CHEAT_CODES.f("&7You have reiceved the cheatcode &e" + optCode.get().toString() + "&7, go into the kit menu located on your phone to toggle the effects."));
                break;
            }
            case "view": {
                if(sender instanceof Player && !Core.getUserManager().getLoadedUser(((Player)sender).getUniqueId()).getUserRank().isHigherThan(UserRank.ADMIN)) {
                    sender.sendMessage(Lang.NOPERM.f(""));
                    return;
                }
                if(args.length !=2) {
                    sender.sendMessage(Utils.f("&e/cheatcode view <player>"));
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                                try (PreparedStatement statement = connection.prepareStatement("Select * from " + Core.name() + " where  name='" + args[1] + "'")) {
                                    try (ResultSet result = statement.executeQuery()) {
                                        int counter = 0;
                                        HashMap<CheatCode, CheatCodeState> cheatCodes = new HashMap<>();
                                        while (result.next()) {
                                            counter++;
                                            Blob b = result.getBlob("cheatcodes");
                                            if(b!=null) {
                                                String cheatCodesBlob = new String(b.getBytes(1, (int) b.length()));
                                                for (String serializedCheatCode : cheatCodesBlob.split("-")) {
                                                    String[] split = serializedCheatCode.split("#");
                                                    cheatCodes.put(CheatCode.valueOf(split[0]), new CheatCodeState(State.valueOf(split[1]), Boolean.valueOf(split[2])));
                                                }
                                            }
                                        }
                                        if(counter==0) {
                                            sender.sendMessage(Lang.CHEAT_CODES.f("&cThat player cannot be found!"));
                                        }
                                        else{
                                            sender.sendMessage(Utils.f("&7Player's Cheat Codes:"));
                                            for(Map.Entry<CheatCode, CheatCodeState> entry : cheatCodes.entrySet()) {
                                                sender.sendMessage(Utils.f("&2&l" + entry.getKey() + "&7: State: " + (entry.getValue().getState()==State.ON ? "&a " : "&c ") + entry.getValue().getState() + "&7, Purchased: " + (entry.getValue().isPurchased() ? "&atrue" : "&cfalse")));
                                            }

                                        }
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

//                            ResultSet rs = Core.sql.query("Select * from " + Core.name() + " where  name='" + args[1] + "'");
//                            try {
//                                int counter = 0;
//                                HashMap<CheatCode, CheatCodeState> cheatCodes = new HashMap<>();
//                                while (rs.next()) {
//                                    counter++;
//                                    Blob b = rs.getBlob("cheatcodes");
//                                    if(b!=null) {
//                                        String cheatCodesBlob = new String(b.getBytes(1, (int) b.length()));
//                                        for (String serializedCheatCode : cheatCodesBlob.split("-")) {
//                                            String[] split = serializedCheatCode.split("#");
//                                            cheatCodes.put(CheatCode.valueOf(split[0]), new CheatCodeState(State.valueOf(split[1]), Boolean.valueOf(split[2])));
//                                        }
//                                    }
//                                }
//                                if(counter==0) {
//                                    sender.sendMessage(Lang.CHEAT_CODES.f("&cThat player cannot be found!"));
//                                }
//                                else{
//                                    sender.sendMessage(Utils.f("&7Player's Cheat Codes:"));
//                                    for(Map.Entry<CheatCode, CheatCodeState> entry : cheatCodes.entrySet()) {
//                                        sender.sendMessage(Utils.f("&2&l" + entry.getKey() + "&7: State: " + (entry.getValue().getState()==State.ON ? "&a " : "&c ") + entry.getValue().getState() + "&7, Purchased: " + (entry.getValue().isPurchased() ? "&atrue" : "&cfalse")));
//                                    }
//
//                                }
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }.runTaskAsynchronously(GTM.getInstance());
                    return;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(target.getUniqueId());
                sender.sendMessage(Utils.f("&7Player's Cheat Codes:"));
                for(Map.Entry<CheatCode, CheatCodeState> entry : user.getCheatCodes().entrySet()) {
                    sender.sendMessage(Utils.f("&2&l" + entry.getKey() + "&7: State: " + (entry.getValue().getState()==State.ON ? "&a " : "&c ") + entry.getValue().getState() + "&7, Purchased: " + (entry.getValue().isPurchased() ? "&atrue" : "&cfalse")));
                }
                break;
            }

            case "list": {
                StringBuilder sb = new StringBuilder("&7Cheat Codes:");
                Arrays.stream(CheatCode.getCodes()).forEach(code -> sb.append(" &a" + code.toString() + "&7,"));
                sb.deleteCharAt(sb.length()-1);
                sender.sendMessage(Utils.f(sb.toString()));
                break;
            }
            default: {
                if(!(sender instanceof Player) || Core.getUserManager().getLoadedUser(((Player)sender).getUniqueId()).getUserRank().isHigherThan(UserRank.ADMIN)) {
                    sender.sendMessage(Utils.f("&e/cheatcode give <player> <cheatcode> &7- gives a certain player a new cheatcode"));
                    sender.sendMessage(Utils.f("&e/cheatcode remove <player> <cheatcode> &7- removes a cheatcode from a player"));
                    sender.sendMessage(Utils.f("&e/cheatcode view <player> &7- view a player's cheatcodes"));
                    sender.sendMessage(Utils.f("&e/cheatcode list &7- lists all avaliable cheat codes."));
                    sender.sendMessage(Utils.f("&e/cheatcode <cheatcode> &7- toggle / activate the specific cheatcode."));
                }
                else {
                    sender.sendMessage(Utils.f("&e/cheatcode <cheatcode> &7- toggle / activate the specific cheatcode."));
                    sender.sendMessage(Utils.f("&e/cheatcode list &7- lists all avaliable cheat codes."));
                }
                break;
            }
        }
    }
}
