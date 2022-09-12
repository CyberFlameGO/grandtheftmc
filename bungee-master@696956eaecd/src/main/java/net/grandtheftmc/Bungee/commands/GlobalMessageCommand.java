package net.grandtheftmc.Bungee.commands;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Lang;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.redisbungee.RedisManager;
import net.grandtheftmc.Bungee.redisbungee.data.DataType;
import net.grandtheftmc.Bungee.users.User;
import net.grandtheftmc.Bungee.utils.HelpLog;
import net.grandtheftmc.Bungee.utils.TabComplete;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GlobalMessageCommand extends Command implements TabExecutor {

    public GlobalMessageCommand() {
        super(
                "globalmessage",
                null,
                "gmsg", "gmessage", "globalmsg", "gtell", "globaltell", "gwhisper", "globalwhisper", "globalw", "gw", "globalchat", "globalc"
        );
    }

    /*
        Redis procedure:
        ----------------
        - Players may reside on different servers across bungee instances.
        1) Check if name matches, or part matches a player on the current bungee instance.
            Yes: Send msg directly.
            No: Go to step 2.
        2) Check if name matches, or part matches a player across all redis instances.
            Yes: Send GMSG packet on PubSub redis channel.
            No: Notify player not found.
     */

    @Override
    public void execute(CommandSender s, String[] args) {
        if (args.length < 2) {
            s.sendMessage(Utils.ft("&c/gmsg <player> <msg>"));
            return;
        }

        if (!(s instanceof ProxiedPlayer)) {
            s.sendMessage(Utils.ft("&cYou are not a player!"));
            return;
        }

        String sender = s.getName();
        String targetName = null;
        ProxiedPlayer senderPlayer = (ProxiedPlayer) s;

        if (args[0].equalsIgnoreCase(senderPlayer.getName())) {
            s.sendMessage(Utils.ft("&cYou cannot message yourself!"));
            return;
        }

        try {
            ProxiedPlayer target = null;

            //Try to direct match player name with all players on this proxy instance.
            if ((target = ProxyServer.getInstance().getPlayer(args[0])) == null) {

                //Prefix tab matching, if they haven't typed the full name we may still find a match
                String search = args[0].toLowerCase();
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player.getName().toLowerCase().startsWith(search)) {
                        target = player;
                        targetName = player.getName();
                        break;
                    }
                }

                if (target == null) {
                    //The player we are searching for is not on this bungee instance, try search on redis

                    RedisManager mngr = Bungee.getRedisManager();

                    //We fail to find a direct match on redis
                    if (!mngr.isPlayerOnline(args[0])) {

                        //Try a partial match search
                        for (String redisName : mngr.getRedisAPI().getHumanPlayersOnline()) {
                            if (redisName.startsWith(search)) {
                                targetName = redisName;
                                break;
                            }
                        }
                    } else {
                        //the player is online, but on another server so send a pub sub msg.
                        targetName = args[0];
                    }
                }
            }

            //If the player couldn't be found locally, or via redis let the player know.
            if (target == null && targetName == null) {
                Utils.msg(s, "&7That player is not on the server!");
                return;
            }

            else if (targetName != null && targetName.equalsIgnoreCase(senderPlayer.getName())) {
                s.sendMessage(Utils.ft("&cYou cannot message yourself!"));
                return;
            }

            else if (target != null && target.getName().equalsIgnoreCase(senderPlayer.getName())) {
                s.sendMessage(Utils.ft("&cYou cannot message yourself!"));
                return;
            }

            //Build the message
            String msg = "";
            for (int i = 1; i < args.length; i++) {
                msg += (i > 1 ? " " : "") + args[i];
            }


            if (target != null) {
                //player is on same server so we can message them directly
                //This method is deprecated, but still has use in local instances so this should be reviewed. TODO.
                String url = "";
                for (String string : msg.split(" ")) {
                    if (string.matches("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$")) {
                        url = string;
                        break;
                    }
                }

                //Send GMSG to recipient
                BaseComponent[] a = new ComponentBuilder(Lang.GMSG.f("&7[" + Utils.getColoredName(senderPlayer) + "&7 -> me] &r" + msg))
                        .event(url.isEmpty() ? new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gmsg " + sender + " ")
                                : new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                        .create();
                target.sendMessage(a);

                //Send GMSG to sender
                a = new ComponentBuilder(Lang.GMSG.f("&7[me&7 -> " + Utils.getColoredName(target) + "&7] &r" + msg))
                        .event(url.isEmpty() ? new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gmsg " + target.getName() + " ")
                                : new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                        .create();
                senderPlayer.sendMessage(a);

                //show the player what they said
                Optional<User> userOptional;
                if (target == null) {
                    userOptional = Optional.empty();
                }
                else {
                    userOptional = Bungee.getUserManager().getLoadedUser(target.getUniqueId());
                }

                //Color the target name
                String coloredName;
                if (userOptional.isPresent()) {
                    coloredName = Utils.f(userOptional.get().getUserRank().getColor() + target.getName());
                }
                else {
                    coloredName = "&8" + target.getName();
                }

                Optional<User> senderOptional = Bungee.getUserManager().getLoadedUser(senderPlayer.getUniqueId());

                //COlor the senders name
                String coloredSenderName;
                if (senderOptional.isPresent()) {
                    coloredSenderName = Utils.f(senderOptional.get().getUserRank().getColor() + senderPlayer.getName());
                }
                else {
                    coloredSenderName = "&8" + senderPlayer.getName();
                }

                //send on social spy
                sendSocialSpy(senderPlayer, coloredSenderName, coloredName, targetName, msg);

                //Check if sender is staff, if so sent help_close if needed, this should now let staff get tokens for local reqs.
                closeHelp(senderPlayer, target.getName());

                return;
            }

            else {
                //player on another redis bungee instance
                Map<String, Object> map = new HashMap<>();
                map.put("target", targetName);
                map.put("sender", senderPlayer.getName());
                Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(senderPlayer.getUniqueId());
                String coloredName;
                if (userOptional.isPresent()) {
                    coloredName = userOptional.get().getUserRank().getColor() + senderPlayer.getName();
                } else {
                    coloredName = "&8" + sender;
                }
                map.put("senderCol", coloredName);
                map.put("message", msg);
                String ser = Bungee.getRedisManager().serialize(DataType.GMSG, map);
                //Send this serialised object to other redis servers for handling...
                Bungee.getRedisManager().sendMessage(ser);
            }

            //show the player what they said
            Optional<User> userOptional;
            if (targetName == null) {
                userOptional = Optional.empty();
            }
            else {
                userOptional = Bungee.getUserManager().getLoadedUser(Bungee.getRedisManager().getRedisAPI().getUuidFromName(targetName));
            }

            String coloredName;
            if (userOptional.isPresent()) {
                coloredName = Utils.f(userOptional.get().getUserRank().getColor() + targetName);
            }
            else {
                coloredName = "&8" + targetName;
            }

            Optional<User> senderOptional = Bungee.getUserManager().getLoadedUser(senderPlayer.getUniqueId());

            String coloredSenderName;
            if (senderOptional.isPresent()) {
                coloredSenderName = Utils.f(senderOptional.get().getUserRank().getColor() + senderPlayer.getName());
            }
            else {
                coloredSenderName = "&8" + senderPlayer.getName();
            }

            String from = Lang.GMSG.f("&7[me -> " + Utils.f(coloredName) + "&7] &r" + msg);

            String url = "";
            for (String string : msg.split(" ")) {
                if (string.matches("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$")) {
                    url = string;
                    break;
                }
            }

            //Send GMSG to recipient
            BaseComponent[] a = new ComponentBuilder(Lang.GMSG.f("&7[me &7-> " + Utils.f(coloredName) + "&r&7] &r" + msg))
                    .event(url.isEmpty() ? new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gmsg " + targetName + " ")
                            : new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                    .create();

            senderPlayer.sendMessage(a);

            sendSocialSpy(senderPlayer, coloredSenderName, coloredName, targetName, msg);

            //Check if sender is staff, if so sent help_close if needed
            closeHelp(senderPlayer, targetName);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void sendSocialSpy(ProxiedPlayer senderPlayer, String coloredSenderName, String coloredName, String targetName, String msg){
        //regardless of where the recipient is we should socialspy this on the staff chat channel
        String ss = Lang.GSPY.f("&7[" + coloredSenderName + "&7 -> " + coloredName + "&7] &r" + msg);

        Map<String, Object> socialSpySerialized = new HashMap<>();
        socialSpySerialized.put("message", ss);
        socialSpySerialized.put("exclude", ChatColor.stripColor(targetName) + "," + senderPlayer.getName());
        Bungee.getRedisManager().sendMessage(Bungee.getRedisManager().serialize(DataType.SOCIALSPY, socialSpySerialized));
    }

    private void closeHelp(ProxiedPlayer player, String targetName) {
        //Check if sender is staff, if so sent help_close if needed
        if (player.hasPermission("staffchat.use") && HelpLog.helpTicketExists(targetName)) {
            Optional<User> helperUserOptional = Bungee.getUserManager().getLoadedUser(player.getUniqueId());
            String helperName = helperUserOptional.isPresent() ? helperUserOptional.get().getColoredName(player) : player.getName();
            Map<String, Object> map = new HashMap<>();
            map.put("helper", helperName);
            map.put("helperUUID", player.getUniqueId().toString());
            map.put("sender", targetName);
            String ser = Bungee.getRedisManager().serialize(DataType.HELP_CLOSE, map);
            Bungee.getRedisManager().sendMessage(ser);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return TabComplete.onTabComplete(sender, args);
    }
}