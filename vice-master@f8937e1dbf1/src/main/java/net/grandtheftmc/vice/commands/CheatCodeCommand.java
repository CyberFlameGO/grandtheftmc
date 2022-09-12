package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.dao.CheatCodeDAO;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.CheatCodeState;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            for (CheatCode code : CheatCode.values()) {
                if (args[0].equalsIgnoreCase(code.toString())) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Lang.NOTPLAYER.f(""));
                        return;
                    }
                    Player player = (Player) sender;
                    ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
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
                Optional<CheatCode> optCode = Arrays.stream(CheatCode.values()).filter(c -> c.toString().equalsIgnoreCase(args[2])).findFirst();
                if (!optCode.isPresent()) {
                    sender.sendMessage(Lang.CHEAT_CODES.f("&cUnable to find cheatcode with name &e" + args[2]));
                    return;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(target.getUniqueId());
                Core.getUserManager().getLoadedUser(target.getUniqueId()).insertLog(target,"takeCheatCodeCommand","CHEATCODE", optCode.get().toString(),1,0);
                user.setCheatCodeState(optCode.get(), new CheatCodeState(State.LOCKED, false));
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
                Optional<CheatCode> optCode = Arrays.stream(CheatCode.values()).filter(c -> c.toString().equalsIgnoreCase(args[2])).findFirst();
                if (!optCode.isPresent()) {
                    sender.sendMessage(Lang.CHEAT_CODES.f("&cUnable to find cheatcode with name &e" + args[2]));
                    return;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(target.getUniqueId());
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
                            int counter = 0;
                            Optional<HashMap<CheatCode, CheatCodeState>> optional = CheatCodeDAO.getCheatCodes(args[1]);
                            if(!optional.isPresent()) {
                                sender.sendMessage(Lang.CHEAT_CODES.f("&cAn error occurred."));
                                return;
                            }

                            counter = optional.get().size();

                            if(counter == 0) {
                                sender.sendMessage(Lang.CHEAT_CODES.f("&cThat player cannot be found!"));
                            }
                            else {
                                sender.sendMessage(Utils.f("&7Player's Cheat Codes:"));
                                for(Map.Entry<CheatCode, CheatCodeState> entry : optional.get().entrySet()) {
                                    sender.sendMessage(Utils.f("&2&l" + entry.getKey() + "&7: State: " + (entry.getValue().getState()==State.ON ? "&a " : "&c ") + entry.getValue().getState() + "&7, Purchased: " + (entry.getValue().isPurchased() ? "&atrue" : "&cfalse")));
                                }
                            }
                        }
                    }.runTaskAsynchronously(Vice.getInstance());
                    return;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(target.getUniqueId());
                sender.sendMessage(Utils.f("&7Player's Cheat Codes:"));
                for(Map.Entry<CheatCode, CheatCodeState> entry : user.getCheatCodes().entrySet()) {
                    sender.sendMessage(Utils.f("&2&l" + entry.getKey() + "&7: State: " + (entry.getValue().getState()==State.ON ? "&a " : "&c ") + entry.getValue().getState() + "&7, Purchased: " + (entry.getValue().isPurchased() ? "&atrue" : "&cfalse")));
                }
                break;
            }

            case "list": {
                StringBuilder sb = new StringBuilder("&7Cheat Codes:");
                Arrays.stream(CheatCode.values()).forEach(code -> sb.append(" &a" + code.toString() + "&7,"));
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
